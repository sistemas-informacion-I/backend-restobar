package org.restobar.gaira.modulo_electronico.service.carrito;

import lombok.RequiredArgsConstructor;
import org.restobar.gaira.modulo_electronico.dto.carrito.CarritoResponse;
import org.restobar.gaira.modulo_electronico.dto.item.AgregarItemRequest;
import org.restobar.gaira.modulo_electronico.dto.item.ActualizarItemRequest;
import org.restobar.gaira.modulo_electronico.entity.CarritoCompras;
import org.restobar.gaira.modulo_electronico.entity.ItemCarrito;
import org.restobar.gaira.modulo_electronico.mapper.carrito.CarritoMapper;
import org.restobar.gaira.modulo_electronico.repository.CarritoComprasRepository;
import org.restobar.gaira.modulo_electronico.repository.ItemCarritoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpStatus.*;

/**
 * Lógica de negocio del carrito de compras online (CU20).
 *
 * <h3>Fuente de verdad en caliente</h3>
 * Redis almacena el estado mutable del carrito (ítems, cantidades, notas)
 * con TTL de 30 minutos. La BD solo recibe escrituras al:
 * <ul>
 *   <li>Convertir el carrito en comanda (checkout).</li>
 *   <li>Guardar historial ABANDONADO (tarea programada opcional).</li>
 * </ul>
 *
 * <h3>Precios</h3>
 * Los precios NUNCA se fían de Redis. Se recalculan desde {@code producto_sucursal}
 * en cada llamada a {@link #obtenerCarrito} y obligatoriamente en el checkout.
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CarritoService {

    private static final Logger log = LoggerFactory.getLogger(CarritoService.class);

    /** TTL del carrito en Redis: 30 minutos. */
    private static final long CARRITO_TTL_MINUTOS = 30L;

    private static final String ESTADO_ACTIVO     = "ACTIVO";
    private static final String ESTADO_CONVERTIDO = "CONVERTIDO";

    // ── Dependencias ──────────────────────────────────────────────────────────

    private final RedisTemplate<String, Object> redisTemplate;
    private final CarritoComprasRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final CarritoMapper carritoMapper;

    /**
     * Repositorio JPA proyectado sobre {@code producto_sucursal}.
     * Inyectado del módulo de operaciones/productos (ciclo 2).
     *
     * <p>Interfaz mínima esperada:
     * <pre>
     * Optional&lt;ProductoSucursalProjection&gt; findByIdProductoFinalAndIdSucursal(Long, Long);
     * List&lt;ProductoSucursalProjection&gt; findByIdSucursalAndIdProductoFinalIn(Long, List&lt;Long&gt;);
     * </pre>
     */
    private final ProductoSucursalLookup productoSucursalLookup;

    // ── Redis helpers ─────────────────────────────────────────────────────────

    /** Genera la clave Redis para el carrito. */
    private String redisKey(Long idCliente, String sessionId) {
        return idCliente != null
                ? "cart:" + idCliente
                : "cart:" + sessionId;
    }

    /**
     * Devuelve el mapa de ítems desde Redis.
     * La estructura almacenada es {@code Map<String idProductoFinal, CartItemRedis>}.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getRedisCart(String key) {
        Object raw = redisTemplate.opsForValue().get(key);
        if (raw instanceof Map<?, ?> m) {
            return (Map<String, Object>) m;
        }
        return new HashMap<>();
    }

    /** Persiste el mapa de ítems en Redis y renueva el TTL. */
    private void saveRedisCart(String key, Map<String, Object> cartData) {
        redisTemplate.opsForValue().set(key, cartData, CARRITO_TTL_MINUTOS, TimeUnit.MINUTES);
    }

    /** Elimina el carrito de Redis (post-checkout). */
    private void deleteRedisCart(String key) {
        redisTemplate.delete(key);
    }

    // ── CU20-1 : Agregar ítem ─────────────────────────────────────────────────

    /**
     * Agrega o incrementa un producto en el carrito.
     *
     * @param idCliente  Nulo si el usuario es anónimo.
     * @param sessionId  ID de sesión temporal del anónimo.
     * @param idSucursal Sucursal seleccionada.
     * @param request    Datos del producto a agregar.
     */
    @Transactional
    public CarritoResponse agregarItem(
            Long idCliente,
            String sessionId,
            Long idSucursal,
            AgregarItemRequest request) {

        validarIdentidad(idCliente, sessionId);

        // 1. Verificar disponibilidad en producto_sucursal
        ProductoSucursalProjection ps = productoSucursalLookup
                .findByIdProductoFinalAndIdSucursal(request.idProductoFinal(), idSucursal)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                        "Producto no encontrado en la sucursal seleccionada"));

        if (!ps.isDisponible()) {
            throw new ResponseStatusException(CONFLICT,
                    "El producto no está disponible en esta sucursal");
        }

        // 2. Obtener/crear carrito en Redis
        String key = redisKey(idCliente, sessionId);
        Map<String, Object> cartData = getRedisCart(key);

        // Metadatos del carrito
        cartData.put("idSucursal", idSucursal);
        if (idCliente != null) cartData.put("idCliente", idCliente);
        else                   cartData.put("sessionId", sessionId);

        // 3. Ítems: Map<idProductoFinal, {cantidad, notas}>
        @SuppressWarnings("unchecked")
        Map<String, Object> items = (Map<String, Object>) cartData.computeIfAbsent("items", k -> new HashMap<String, Object>());

        String pid = String.valueOf(request.idProductoFinal());
        if (items.containsKey(pid)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> existing = (Map<String, Object>) items.get(pid);
            int nuevaCantidad = ((Number) existing.get("cantidad")).intValue() + request.cantidad();
            existing.put("cantidad", nuevaCantidad);
            if (request.notasEspeciales() != null) {
                existing.put("notas", request.notasEspeciales());
            }
        } else {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("cantidad", request.cantidad());
            itemData.put("notas", request.notasEspeciales());
            items.put(pid, itemData);
        }

        saveRedisCart(key, cartData);
        log.debug("Ítem {} agregado al carrito Redis '{}'", pid, key);

        return buildCarritoResponseFromRedis(cartData, key, idSucursal);
    }

    // ── CU20-2 : Ver carrito ──────────────────────────────────────────────────

    /**
     * Recupera el carrito desde Redis y recalcula subtotales con precios actuales.
     */
    @Transactional(readOnly = true)
    public CarritoResponse obtenerCarrito(Long idCliente, String sessionId, Long idSucursal) {
        validarIdentidad(idCliente, sessionId);

        String key = redisKey(idCliente, sessionId);
        Map<String, Object> cartData = getRedisCart(key);

        if (cartData.isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND, "No existe un carrito activo");
        }

        return buildCarritoResponseFromRedis(cartData, key, idSucursal);
    }

    // ── CU20-2 : Actualizar cantidad ─────────────────────────────────────────

    @Transactional
    public CarritoResponse actualizarItem(
            Long idCliente,
            String sessionId,
            Long idSucursal,
            Long idProductoFinal,
            ActualizarItemRequest request) {

        validarIdentidad(idCliente, sessionId);

        String key = redisKey(idCliente, sessionId);
        Map<String, Object> cartData = getRedisCart(key);
        requireCarritoActivo(cartData);

        @SuppressWarnings("unchecked")
        Map<String, Object> items = (Map<String, Object>) cartData.get("items");
        String pid = String.valueOf(idProductoFinal);

        if (items == null || !items.containsKey(pid)) {
            throw new ResponseStatusException(NOT_FOUND, "El ítem no existe en el carrito");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> itemData = (Map<String, Object>) items.get(pid);
        itemData.put("cantidad", request.cantidad());

        saveRedisCart(key, cartData);
        return buildCarritoResponseFromRedis(cartData, key, idSucursal);
    }

    // ── CU20-2 : Eliminar ítem ────────────────────────────────────────────────

    @Transactional
    public CarritoResponse eliminarItem(
            Long idCliente,
            String sessionId,
            Long idSucursal,
            Long idProductoFinal) {

        validarIdentidad(idCliente, sessionId);

        String key = redisKey(idCliente, sessionId);
        Map<String, Object> cartData = getRedisCart(key);
        requireCarritoActivo(cartData);

        @SuppressWarnings("unchecked")
        Map<String, Object> items = (Map<String, Object>) cartData.get("items");
        String pid = String.valueOf(idProductoFinal);

        if (items == null || items.remove(pid) == null) {
            throw new ResponseStatusException(NOT_FOUND, "El ítem no existe en el carrito");
        }

        saveRedisCart(key, cartData);
        return buildCarritoResponseFromRedis(cartData, key, idSucursal);
    }

    // ── CU20-3 : Cambiar sucursal ─────────────────────────────────────────────

    /**
     * Cambia la sucursal del carrito y verifica disponibilidad de cada ítem.
     * Los productos no disponibles en la nueva sucursal se marcan en la respuesta
     * pero <strong>NO se eliminan automáticamente</strong>: el cliente debe
     * corregir el carrito antes del checkout.
     */
    @Transactional
    public CarritoResponse cambiarSucursal(
            Long idCliente,
            String sessionId,
            Long nuevaSucursal) {

        validarIdentidad(idCliente, sessionId);

        String key = redisKey(idCliente, sessionId);
        Map<String, Object> cartData = getRedisCart(key);
        requireCarritoActivo(cartData);

        cartData.put("idSucursal", nuevaSucursal);
        saveRedisCart(key, cartData);

        return buildCarritoResponseFromRedis(cartData, key, nuevaSucursal);
    }

    // ── CU20-4 : Checkout ─────────────────────────────────────────────────────

    /**
     * Convierte el carrito en una comanda ONLINE con estado PENDIENTE_PAGO.
     *
     * <p>Precondiciones:
     * <ul>
     *   <li>El cliente DEBE estar autenticado ({@code idCliente} no nulo).</li>
     *   <li>Todos los ítems deben estar disponibles en la sucursal.</li>
     * </ul>
     *
     * @return ID de la comanda creada (manejado por CU21).
     */
    @Transactional
    public Long checkout(Long idCliente, Long idSucursal) {
        if (idCliente == null) {
            throw new ResponseStatusException(UNAUTHORIZED,
                    "Debe iniciar sesión para completar el pedido");
        }

        String key = "cart:" + idCliente;
        Map<String, Object> cartData = getRedisCart(key);
        requireCarritoActivo(cartData);

        @SuppressWarnings("unchecked")
        Map<String, Object> items = (Map<String, Object>) cartData.get("items");

        if (items == null || items.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "El carrito está vacío");
        }

        List<Long> ids = items.keySet().stream().map(Long::valueOf).toList();

        // Recalcular precios y verificar disponibilidad desde BD
        Map<Long, ProductoSucursalProjection> psMap = productoSucursalLookup
                .findByIdSucursalAndIdProductoFinalIn(idSucursal, ids);

        List<String> noDisponibles = new ArrayList<>();
        for (Long pid : ids) {
            ProductoSucursalProjection ps = psMap.get(pid);
            if (ps == null || !ps.isDisponible()) {
                noDisponibles.add(String.valueOf(pid));
            }
        }

        if (!noDisponibles.isEmpty()) {
            throw new ResponseStatusException(CONFLICT,
                    "Los siguientes productos no están disponibles: " + noDisponibles);
        }

        // Persistir carrito histórico en BD
        CarritoCompras carritoDb = carritoRepository.save(
                CarritoCompras.builder()
                        .idCliente(idCliente)
                        .idSucursal(idSucursal)
                        .estado(ESTADO_CONVERTIDO)
                        .build());

        // Guardar ítems históricos con precio recalculado
        for (Map.Entry<String, Object> entry : items.entrySet()) {
            Long pid = Long.valueOf(entry.getKey());
            @SuppressWarnings("unchecked")
            Map<String, Object> itemData = (Map<String, Object>) entry.getValue();
            int cantidad = ((Number) itemData.get("cantidad")).intValue();
            String notas = (String) itemData.get("notas");
            BigDecimal precio = psMap.get(pid).getPrecio();

            ItemCarrito item = ItemCarrito.builder()
                    .carrito(carritoDb)
                    .idProductoFinal(pid)
                    .cantidad(cantidad)
                    .precioUnitario(precio)
                    .notasEspeciales(notas)
                    .build();
            itemCarritoRepository.save(item);
        }

        // Delegar creación de comanda al servicio de CU21 (inyección externa)
        // El id del carrito se pasa para que comanda.id_carrito quede referenciado.
        Long idComanda = crearComandaOnline(carritoDb, idCliente, idSucursal, psMap, items);

        // Eliminar carrito de Redis
        deleteRedisCart(key);
        log.info("Carrito {} convertido en comanda {} para cliente {}", carritoDb.getIdCarrito(), idComanda, idCliente);

        return idComanda;
    }

    // ── CU20 Sincronización: migrar carrito anónimo al autenticarse ───────────

    /**
     * Migra el carrito de sesión anónima al usuario recién autenticado.
     * Se invoca desde el servicio de login tras una autenticación exitosa
     * cuando existía un {@code sessionId} activo.
     */
    @Transactional
    public void migrarCarritoAnonimo(String sessionId, Long idCliente) {
        String anonKey = "cart:" + sessionId;
        String userKey = "cart:" + idCliente;

        Map<String, Object> anonCart = getRedisCart(anonKey);
        if (anonCart.isEmpty()) return;

        Map<String, Object> userCart = getRedisCart(userKey);
        if (userCart.isEmpty()) {
            // El usuario no tenía carrito; simplemente reasignar
            anonCart.put("idCliente", idCliente);
            anonCart.remove("sessionId");
            saveRedisCart(userKey, anonCart);
        } else {
            // Fusionar: sumar cantidades del carrito anónimo al del usuario
            @SuppressWarnings("unchecked")
            Map<String, Object> anonItems = (Map<String, Object>) anonCart.getOrDefault("items", new HashMap<>());
            @SuppressWarnings("unchecked")
            Map<String, Object> userItems = (Map<String, Object>) userCart.computeIfAbsent("items", k -> new HashMap<String, Object>());

            for (Map.Entry<String, Object> entry : anonItems.entrySet()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> anonItem = (Map<String, Object>) entry.getValue();
                int cantAnon = ((Number) anonItem.get("cantidad")).intValue();

                if (userItems.containsKey(entry.getKey())) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> userItem = (Map<String, Object>) userItems.get(entry.getKey());
                    int cantUser = ((Number) userItem.get("cantidad")).intValue();
                    userItem.put("cantidad", cantAnon + cantUser);
                } else {
                    userItems.put(entry.getKey(), anonItem);
                }
            }
            saveRedisCart(userKey, userCart);
        }

        deleteRedisCart(anonKey);
        log.info("Carrito anónimo '{}' migrado a usuario {}", sessionId, idCliente);
    }

    // ── Helpers internos ──────────────────────────────────────────────────────

    private void validarIdentidad(Long idCliente, String sessionId) {
        if (idCliente == null && (sessionId == null || sessionId.isBlank())) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Se requiere idCliente o sessionId para operar el carrito");
        }
    }

    private void requireCarritoActivo(Map<String, Object> cartData) {
        if (cartData.isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND, "No existe un carrito activo");
        }
    }

    /**
     * Construye el {@link CarritoResponse} consultando precios actuales en BD.
     */
    private CarritoResponse buildCarritoResponseFromRedis(
            Map<String, Object> cartData,
            String key,
            Long idSucursal) {

        @SuppressWarnings("unchecked")
        Map<String, Object> items = (Map<String, Object>) cartData.getOrDefault("items", new HashMap<>());

        if (items.isEmpty()) {
            return new org.restobar.gaira.modulo_electronico.dto.carrito.CarritoResponse(
                    null, idSucursal, ESTADO_ACTIVO, List.of(), BigDecimal.ZERO, java.time.LocalDateTime.now());
        }

        List<Long> ids = items.keySet().stream().map(Long::valueOf).toList();
        Map<Long, ProductoSucursalProjection> psMap =
                productoSucursalLookup.findByIdSucursalAndIdProductoFinalIn(idSucursal, ids);

        Map<Long, BigDecimal> precios      = new HashMap<>();
        Map<Long, Boolean>    disponibles  = new HashMap<>();
        Map<Long, String>     nombres      = new HashMap<>();

        for (Long pid : ids) {
            ProductoSucursalProjection ps = psMap.get(pid);
            if (ps != null) {
                precios.put(pid, ps.getPrecio());
                disponibles.put(pid, ps.isDisponible());
                nombres.put(pid, ps.getNombreProducto());
            } else {
                precios.put(pid, BigDecimal.ZERO);
                disponibles.put(pid, false);
                nombres.put(pid, "Producto " + pid);
            }
        }

        // Construir entidad temporal (sin persistir) para pasar al mapper
        CarritoCompras carritoTransient = buildTransientCarrito(cartData, items, precios);

        return carritoMapper.toResponse(carritoTransient, precios, disponibles, nombres);
    }

    private CarritoCompras buildTransientCarrito(
            Map<String, Object> cartData,
            Map<String, Object> items,
            Map<Long, BigDecimal> precios) {

        Long idSucursal = cartData.get("idSucursal") != null
                ? ((Number) cartData.get("idSucursal")).longValue() : null;

        CarritoCompras carrito = CarritoCompras.builder()
                .idSucursal(idSucursal)
                .estado(ESTADO_ACTIVO)
                .build();

        List<ItemCarrito> itemList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : items.entrySet()) {
            Long pid = Long.valueOf(entry.getKey());
            @SuppressWarnings("unchecked")
            Map<String, Object> d = (Map<String, Object>) entry.getValue();
            int cant = ((Number) d.get("cantidad")).intValue();
            String notas = (String) d.get("notas");
            BigDecimal precio = precios.getOrDefault(pid, BigDecimal.ZERO);

            itemList.add(ItemCarrito.builder()
                    .carrito(carrito)
                    .idProductoFinal(pid)
                    .cantidad(cant)
                    .precioUnitario(precio)
                    .notasEspeciales(notas)
                    .build());
        }
        carrito.setItems(itemList);
        return carrito;
    }

    /**
     * Punto de extensión para la creación de la comanda (CU21).
     * En producción se reemplaza por la inyección del servicio de comandas.
     */
    protected Long crearComandaOnline(
            CarritoCompras carrito,
            Long idCliente,
            Long idSucursal,
            Map<Long, ProductoSucursalProjection> psMap,
            Map<String, Object> items) {
        // TODO: inyectar ComandaService (CU21) y llamar:
        // return comandaService.crearDesdeCarrito(carrito, idCliente, idSucursal, psMap, items);
        throw new UnsupportedOperationException("ComandaService (CU21) aún no integrado");
    }

    // ── Proyección interna (interfaz de contrato con módulo de productos) ─────

    /**
     * Contrato mínimo que el módulo de productos/operaciones debe satisfacer.
     * Permite desacoplar este servicio del módulo de ciclo 2.
     */
    public interface ProductoSucursalLookup {

        Optional<ProductoSucursalProjection> findByIdProductoFinalAndIdSucursal(
                Long idProductoFinal, Long idSucursal);

        /** Devuelve un mapa {@code idProductoFinal → proyección}. */
        Map<Long, ProductoSucursalProjection> findByIdSucursalAndIdProductoFinalIn(
                Long idSucursal, List<Long> ids);
    }

    public interface ProductoSucursalProjection {
        boolean isDisponible();
        BigDecimal getPrecio();
        String getNombreProducto();
    }
}
