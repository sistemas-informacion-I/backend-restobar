package org.restobar.gaira.modulo_electronico.service.catalogo;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.restobar.gaira.modulo_electronico.dto.catalogo.CatalogoProductoResponse;
import org.restobar.gaira.modulo_electronico.dto.catalogo.CatalogoUpdateRequest;
import org.restobar.gaira.modulo_comercial.entity.ProductoSucursal;
import org.restobar.gaira.modulo_electronico.mapper.catalogo.CatalogoMapper;
import org.restobar.gaira.modulo_comercial.repository.ProductoSucursalRepository;
import org.restobar.gaira.modulo_inventario.entity.Receta;
import org.restobar.gaira.modulo_inventario.repository.RecetaRepository;
import org.restobar.gaira.modulo_inventario.repository.StockSucursalRepository;
import org.restobar.gaira.security.audit.annotation.Auditable;
import org.restobar.gaira.security.utils.SecurityUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CatalogoService {

    private static final String CACHE_PREFIX = "catalogo:sucursal:";
    private static final long CACHE_TTL_MINUTES = 15;

    private final ProductoSucursalRepository productoSucursalRepository;
    private final StockSucursalRepository stockSucursalRepository;
    private final RecetaRepository recetaRepository;
    private final SecurityUtils securityUtils;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final CatalogoMapper catalogoMapper;

    // ─── Vista Cliente: catálogo público ─────────────────────────────────────

    @Transactional(readOnly = true)
    public List<CatalogoProductoResponse> getCatalogoPorSucursal(
            Long idSucursal, String busqueda, Long idCategoria) {

        String cacheKey = CACHE_PREFIX + idSucursal;

        try {
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                List<CatalogoProductoResponse> lista = objectMapper.convertValue(
                        cached, new TypeReference<List<CatalogoProductoResponse>>() {});
                return filtrarCatalogo(lista, busqueda, idCategoria);
            }
        } catch (Exception e) {
            log.warn("Error al leer caché catálogo sucursal {}: {}", idSucursal, e.getMessage());
        }

        List<CatalogoProductoResponse> catalogo = productoSucursalRepository
                .findByIdSucursal(idSucursal).stream()
                .filter(ps -> ps.isActivo()
                        && ps.isDisponible()
                        && ps.getProductoFinal() != null
                        && ps.getProductoFinal().isActivo())
                .map(ps -> toResponse(ps, idSucursal))
                .collect(Collectors.toList());

        try {
            redisTemplate.opsForValue().set(cacheKey, catalogo, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("Error al escribir caché catálogo sucursal {}: {}", idSucursal, e.getMessage());
        }

        return filtrarCatalogo(catalogo, busqueda, idCategoria);
    }

    // ─── Vista Admin ──────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<CatalogoProductoResponse> getCatalogoAdmin(Long idSucursal) {
        Long idSucursalFinal = resolverSucursal(idSucursal);
        return productoSucursalRepository.findByIdSucursal(idSucursalFinal).stream()
                .filter(ps -> ps.getProductoFinal() != null && ps.getProductoFinal().isActivo())
                .map(ps -> toResponse(ps, idSucursalFinal))
                .collect(Collectors.toList());
    }

    // ─── Admin: actualizar precio y disponibilidad ────────────────────────────

    @Transactional
    @Auditable(tabla = "producto_sucursal", operacion = "UPDATE")
    public CatalogoProductoResponse actualizarDisponibilidad(
            Long idProducto, Long idSucursal, CatalogoUpdateRequest request) {

        Long idSucursalFinal = resolverSucursal(idSucursal);

        ProductoSucursal ps = productoSucursalRepository
                .findByIdProductoFinalAndIdSucursal(idProducto, idSucursalFinal)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Producto no encontrado en esta sucursal"));

        if (Boolean.TRUE.equals(request.disponible())) {
            boolean hayStock = tieneStockPorReceta(idProducto, idSucursalFinal);
            if (!hayStock) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No se puede activar la disponibilidad: sin stock suficiente de ingredientes");
            }
        }

        ps.setPrecio(request.precio());
        ps.setDisponible(request.disponible());
        productoSucursalRepository.save(ps);
        invalidarCache(idSucursalFinal);

        return toResponse(ps, idSucursalFinal);
    }

    // ─── Privados ─────────────────────────────────────────────────────────────

    private CatalogoProductoResponse toResponse(ProductoSucursal ps, Long idSucursal) {
        boolean hayStock = tieneStockPorReceta(ps.getIdProductoFinal(), idSucursal);
        boolean disponibleFinal = ps.isDisponible() && hayStock;
        return catalogoMapper.toResponse(ps, disponibleFinal, hayStock);
    }

    private boolean tieneStockPorReceta(Long idProductoFinal, Long idSucursal) {
        List<Receta> recetas = recetaRepository.findByFiltros(null, true, idProductoFinal);
        if (recetas.isEmpty()) return true;

        Receta receta = recetas.get(0);
        if (receta.getIngredientes() == null || receta.getIngredientes().isEmpty()) return true;

        for (var ingrediente : receta.getIngredientes()) {
            Long idInventario = ingrediente.getInventario().getIdInventario();
            boolean hayStock = stockSucursalRepository
                    .findByInventarioIdInventarioAndSucursalIdSucursal(idInventario, idSucursal)
                    .map(stock -> Boolean.TRUE.equals(stock.getActivo())
                            && stock.getCantidad() != null
                            && stock.getCantidad().compareTo(BigDecimal.ZERO) > 0)
                    .orElse(false);
            if (!hayStock) return false;
        }
        return true;
    }

    private List<CatalogoProductoResponse> filtrarCatalogo(
            List<CatalogoProductoResponse> lista, String busqueda, Long idCategoria) {
        return lista.stream()
                .filter(CatalogoProductoResponse::disponible)
                .filter(p -> busqueda == null || busqueda.isBlank()
                        || p.nombre().toLowerCase().contains(busqueda.toLowerCase())
                        || (p.descripcion() != null
                                && p.descripcion().toLowerCase().contains(busqueda.toLowerCase())))
                .filter(p -> idCategoria == null || idCategoria.equals(p.idCategoria()))
                .collect(Collectors.toList());
    }

    private Long resolverSucursal(Long idSucursalRequest) {
        Long idSucursalUsuario = securityUtils.getCurrentSucursalId();
        if (idSucursalUsuario != null) return idSucursalUsuario;
        if (idSucursalRequest == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar la sucursal");
        }
        return idSucursalRequest;
    }

    private void invalidarCache(Long idSucursal) {
        try {
            redisTemplate.delete(CACHE_PREFIX + idSucursal);
        } catch (Exception e) {
            log.warn("Error al invalidar caché catálogo sucursal {}: {}", idSucursal, e.getMessage());
        }
    }
}
