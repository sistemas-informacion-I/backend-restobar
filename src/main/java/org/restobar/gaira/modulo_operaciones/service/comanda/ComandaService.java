package org.restobar.gaira.modulo_operaciones.service.comanda;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.restobar.gaira.modulo_acceso.entity.Cliente;
import org.restobar.gaira.modulo_acceso.entity.Empleado;
import org.restobar.gaira.modulo_acceso.repository.ClienteRepository;
import org.restobar.gaira.modulo_acceso.repository.EmpleadoRepository;
import org.restobar.gaira.modulo_comercial.entity.ProductoFinal;
import org.restobar.gaira.modulo_comercial.entity.ProductoSucursal;
import org.restobar.gaira.modulo_comercial.repository.ProductoSucursalRepository;
import org.restobar.gaira.modulo_operaciones.dto.comanda.ComandaCreateDTO;
import org.restobar.gaira.modulo_operaciones.dto.comanda.ComandaResponseDTO;
import org.restobar.gaira.modulo_operaciones.dto.comanda.ComandaUpdateDTO;
import org.restobar.gaira.modulo_operaciones.dto.comanda.DetalleComandaItemDTO;
import org.restobar.gaira.modulo_operaciones.dto.comanda.DetalleComandaUpdateDTO;
import org.restobar.gaira.modulo_operaciones.entity.Comanda;
import org.restobar.gaira.modulo_operaciones.entity.DetalleComanda;
import org.restobar.gaira.modulo_operaciones.entity.Mesa;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.restobar.gaira.modulo_operaciones.mapper.comanda.ComandaMapper;
import org.restobar.gaira.modulo_operaciones.repository.ComandaRepository;
import org.restobar.gaira.modulo_operaciones.repository.DetalleComandaRepository;
import org.restobar.gaira.modulo_operaciones.repository.MesaRepository;
import org.restobar.gaira.modulo_operaciones.repository.SucursalRepository;
import org.restobar.gaira.security.audit.annotation.Auditable;
import org.restobar.gaira.security.utils.SecurityUtils;
import org.restobar.gaira.shared.websocket.WebSocketService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComandaService {

    private final ComandaRepository comandaRepository;
    private final SucursalRepository sucursalRepository;
    private final MesaRepository mesaRepository;
    private final DetalleComandaRepository detalleComandaRepository;
    private final ProductoSucursalRepository productoSucursalRepository;
    private final ClienteRepository clienteRepository;
    private final EmpleadoRepository empleadoRepository;
    private final SecurityUtils securityUtils;
    private final ComandaMapper comandaMapper;
    private final WebSocketService webSocketService;

    @Transactional(readOnly = true)
    public List<ComandaResponseDTO> getComandas() {
        if (isSuperUser()) {
            return comandaRepository.findAll().stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }

        Long sucursalId = securityUtils.getCurrentSucursalId();
        if (sucursalId == null) {
            return List.of();
        }

        return comandaRepository.findBySucursalIdSucursal(sucursalId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ComandaResponseDTO getComandaById(Long id) {
        return mapToResponse(findComandaById(id));
    }

    @Transactional(readOnly = true)
    public List<ComandaResponseDTO> getComandasBySucursal(Long idSucursal) {
        return comandaRepository.findBySucursalIdSucursal(idSucursal).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ComandaResponseDTO> getComandasByMesa(Long idMesa) {
        return comandaRepository.findByMesa_IdMesa(idMesa).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ComandaResponseDTO> getComandasByEstado(String estado) {
        if (isSuperUser()) {
            return comandaRepository.findByEstado(estado).stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }

        Long sucursalId = securityUtils.getCurrentSucursalId();
        if (sucursalId == null) {
            return List.of();
        }

        return comandaRepository.findBySucursalIdSucursal(sucursalId).stream()
                .filter(c -> estado.equals(c.getEstado()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Auditable(tabla = "comanda", operacion = "INSERT", idParamName = "id")
    public ComandaResponseDTO createComanda(ComandaCreateDTO request) {
        String tipoServicio = normalize(request.getTipoServicio());
        Comanda.TipoServicio servicio = parseTipoServicio(tipoServicio);

        if (servicio == Comanda.TipoServicio.ONLINE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comandas ONLINE deben crearse desde el checkout en línea");
        }

        Sucursal sucursal = resolveSucursal(request.getIdSucursal());
        Mesa mesa = null;

        if (servicio == Comanda.TipoServicio.MESA) {
            if (request.getIdMesa() == null && request.getIdReserva() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Para servicio MESA se requiere una mesa o una reserva");
            }
            if (request.getIdMesa() != null) {
                mesa = validateMesaForSucursal(request.getIdMesa(), sucursal.getIdSucursal());
                mesa.setDisponibilidad("OCUPADA");
                mesaRepository.save(mesa);
            }
        } else {
            if (request.getIdMesa() != null || request.getIdReserva() != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id de mesa o reserva solo se aceptan para servicio MESA");
            }
        }

        Comanda comanda = Comanda.builder()
                .numeroComanda(generarNumeroComanda())
                .sucursal(sucursal)
                .cliente(fetchCliente(request.getIdCliente()))
                .empleado(findCurrentEmpleado().orElse(null))
                .tipoServicio(servicio.name())
                .estado(Comanda.EstadoComanda.ABIERTA.name())
                .idReserva(request.getIdReserva())
                .mesa(mesa)
                .fechaApertura(LocalDateTime.now())
                .numeroPersonas(request.getNumeroPersonas())
                .observaciones(request.getObservaciones())
                .build();

        comanda = comandaRepository.save(comanda);
        saveDetalleItems(comanda, request.getItems());
        return mapToResponse(comanda);
    }

    @Transactional
    @Auditable(tabla = "comanda", operacion = "UPDATE", idParamName = "id")
    public ComandaResponseDTO updateComanda(Long id, ComandaUpdateDTO request) {
        Comanda comanda = findComandaById(id);
        validateComandaNotClosed(comanda);

        if (request.getEstado() != null) {
            comanda.setEstado(parseEstado(normalize(request.getEstado())).name());
        }
        if (request.getObservaciones() != null) {
            comanda.setObservaciones(request.getObservaciones());
        }
        if (request.getNumeroPersonas() != null) {
            comanda.setNumeroPersonas(request.getNumeroPersonas());
        }
        if (request.getIdCliente() != null) {
            // Permite asignar (o cambiar) el cliente de la comanda; 0 = anónimo (sin cliente)
            comanda.setCliente(request.getIdCliente() == 0 ? null : fetchCliente(request.getIdCliente()));
        }

        comanda = comandaRepository.save(comanda);

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            saveDetalleItems(comanda, request.getItems());
        }

        return mapToResponse(comanda);
    }

    @Transactional
    @Auditable(tabla = "comanda", operacion = "UPDATE", idParamName = "id")
    public ComandaResponseDTO closeComanda(Long id) {
        Comanda comanda = findComandaById(id);

        if (Comanda.EstadoComanda.CERRADA.name().equals(comanda.getEstado())) {
            return mapToResponse(comanda);
        }
        if (Comanda.EstadoComanda.CANCELADA.name().equals(comanda.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede cerrar una comanda cancelada");
        }

        comanda.setEstado(Comanda.EstadoComanda.CERRADA.name());
        comanda.setFechaCierre(LocalDateTime.now());

        if (comanda.getMesa() != null) {
            Mesa mesa = comanda.getMesa();
            mesa.setDisponibilidad("DISPONIBLE");
            mesaRepository.save(mesa);
        }

        comanda = comandaRepository.save(comanda);
        return mapToResponse(comanda);
    }

    @Transactional
    public Comanda crearOnlineDesdeCarrito(
            Long idCarrito,
            Long idCliente,
            Long idSucursal,
            Map<Long, BigDecimal> precios,
            List<ItemData> items,
            Cliente cliente) {

        Sucursal sucursal = sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> new IllegalArgumentException("Sucursal no encontrada: " + idSucursal));

        String numeroComanda = generarNumeroComanda();

        Comanda comanda = Comanda.builder()
                .numeroComanda(numeroComanda)
                .sucursal(sucursal)
                .cliente(cliente)
                .empleado(null)
                .tipoServicio(Comanda.TipoServicio.ONLINE.name())
                .estado(Comanda.EstadoComanda.PENDIENTE_PAGO.name())
                .idCarrito(idCarrito)
                .fechaApertura(LocalDateTime.now())
                .build();

        comanda = comandaRepository.save(comanda);
        if (items != null && !items.isEmpty()) {
            saveDetalleItemsFromItemData(comanda, items);
        }
        log.info("Comanda ONLINE {} creada para carrito {} (cliente {})", numeroComanda, idCarrito, idCliente);
        return comanda;
    }

    private Comanda findComandaById(Long id) {
        if (isSuperUser()) {
            return comandaRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comanda no encontrada"));
        }

        Long sucursalId = securityUtils.getCurrentSucursalId();
        if (sucursalId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se encontró sucursal actual");
        }

        return comandaRepository.findByIdComandaAndSucursalIdSucursal(id, sucursalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comanda no encontrada en la sucursal actual"));
    }

    private void validateComandaNotClosed(Comanda comanda) {
        if (Comanda.EstadoComanda.CERRADA.name().equals(comanda.getEstado()) ||
                Comanda.EstadoComanda.CANCELADA.name().equals(comanda.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede modificar una comanda cerrada o cancelada");
        }
    }

    private Sucursal resolveSucursal(Long idSucursal) {
        if (isSuperUser()) {
            if (idSucursal == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID de sucursal es requerido para superusuario");
            }
            return sucursalRepository.findById(idSucursal)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada"));
        }

        Long sucursalId = securityUtils.getCurrentSucursalId();
        if (sucursalId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se encontró sucursal actual");
        }

        if (idSucursal != null && !idSucursal.equals(sucursalId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No tiene permisos sobre recursos de otra sucursal");
        }

        return sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada"));
    }

    private Mesa validateMesaForSucursal(Long idMesa, Long idSucursal) {
        Mesa mesa = mesaRepository.findById(idMesa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));

        if (!mesa.getSector().getSucursal().getIdSucursal().equals(idSucursal)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La mesa pertenece a otra sucursal");
        }
        if (!mesa.getActivo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La mesa no está activa");
        }
        if (!"DISPONIBLE".equals(mesa.getDisponibilidad())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La mesa ya está ocupada");
        }

        return mesa;
    }

    private List<DetalleComanda> saveDetalleItems(Comanda comanda, List<DetalleComandaItemDTO> items) {
        List<DetalleComanda> detalles = items.stream()
                .map(item -> {
                    ProductoSucursal productoSucursal = productoSucursalRepository
                            .findByIdProductoFinalAndIdSucursal(item.getIdProductoFinal(), comanda.getSucursal().getIdSucursal())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                    "Producto no disponible en la sucursal: " + item.getIdProductoFinal()));

                    if (!productoSucursal.isActivo() || !productoSucursal.isDisponible()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Producto no disponible o inactivo en la sucursal: " + item.getIdProductoFinal());
                    }

                    ProductoFinal producto = productoSucursal.getProductoFinal();
                    return DetalleComanda.builder()
                            .comanda(comanda)
                            .productoFinal(producto)
                            .precioUnitario(productoSucursal.getPrecio())
                            .cantidad(item.getCantidad())
                            .notas(item.getNotas())
                            .estado(DetalleComanda.EstadoDetalle.PENDIENTE.name())
                            .estacionPreparacion(determineEstacion(producto))
                            .build();
                })
                .collect(Collectors.toList());

        List<DetalleComanda> saved = detalleComandaRepository.saveAll(detalles);

        // Notificar a cocina/barra via WebSocket
        emitirEventoNuevosItems(comanda, saved);

        return saved;
    }

    private ComandaResponseDTO mapToResponse(Comanda comanda) {
        List<DetalleComanda> detalles = detalleComandaRepository.findByComandaIdComanda(comanda.getIdComanda());
        return comandaMapper.toResponseDTO(comanda, detalles);
    }

    private Optional<Empleado> findCurrentEmpleado() {
        Long idUsuario = securityUtils.getCurrentUserId();
        if (idUsuario == null) {
            return Optional.empty();
        }
        return empleadoRepository.findByUsuario_IdUsuario(idUsuario);
    }

    private Cliente fetchCliente(Long idCliente) {
        if (idCliente == null) {
            return null;
        }
        return clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
    }

    private boolean isSuperUser() {
        return "S".equals(securityUtils.getCurrentUserTipoUsuario());
    }

    private String normalize(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }

    private Comanda.TipoServicio parseTipoServicio(String tipoServicio) {
        if (tipoServicio == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de servicio es requerido");
        }
        try {
            return Comanda.TipoServicio.valueOf(tipoServicio);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de servicio inválido");
        }
    }

    private Comanda.EstadoComanda parseEstado(String estado) {
        if (estado == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado es requerido");
        }
        try {
            return Comanda.EstadoComanda.valueOf(estado);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado de comanda inválido");
        }
    }

    private String determineEstacion(ProductoFinal producto) {
        if (producto == null || producto.getCategoria() == null || producto.getCategoria().getNombre() == null) {
            return "COCINA";
        }
        String categoria = producto.getCategoria().getNombre().toUpperCase();
        if (categoria.contains("BEBIDA") || categoria.contains("BAR") || categoria.contains("COCTEL")) {
            return "BARRA";
        }
        return "COCINA";
    }

    private String generarNumeroComanda() {
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long random = System.currentTimeMillis() % 10000;
        return "CMD-" + fecha + "-" + String.format("%04d", random);
    }

    public record ItemData(Long idProductoFinal, Integer cantidad, BigDecimal precioUnitario, String notas) {}

    private List<DetalleComanda> saveDetalleItemsFromItemData(Comanda comanda, List<ItemData> items) {
        List<DetalleComanda> detalles = items.stream()
                .map(item -> {
                    ProductoSucursal productoSucursal = productoSucursalRepository
                            .findByIdProductoFinalAndIdSucursal(item.idProductoFinal(), comanda.getSucursal().getIdSucursal())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                    "Producto no disponible en la sucursal: " + item.idProductoFinal()));

                    if (!productoSucursal.isActivo() || !productoSucursal.isDisponible()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Producto no disponible o inactivo en la sucursal: " + item.idProductoFinal());
                    }

                    ProductoFinal producto = productoSucursal.getProductoFinal();
                    return DetalleComanda.builder()
                            .comanda(comanda)
                            .productoFinal(producto)
                            .precioUnitario(productoSucursal.getPrecio())
                            .cantidad(item.cantidad())
                            .notas(item.notas())
                            .estado(DetalleComanda.EstadoDetalle.PENDIENTE.name())
                            .estacionPreparacion(determineEstacion(producto))
                            .build();
                })
                .collect(Collectors.toList());

        List<DetalleComanda> saved = detalleComandaRepository.saveAll(detalles);

        // Notificar a cocina/barra via WebSocket
        emitirEventoNuevosItems(comanda, saved);

        return saved;
    }

    private void ensureNoInPreparation(Comanda comanda) {
        List<DetalleComanda> detalles = detalleComandaRepository.findByComandaIdComanda(comanda.getIdComanda());
        boolean anyInPrep = detalles.stream().anyMatch(d -> DetalleComanda.EstadoDetalle.EN_PREPARACION.name().equals(d.getEstado()));
        if (anyInPrep) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se pueden modificar items: existe un ítem en preparación");
        }
    }

    @Transactional
    public ComandaResponseDTO updateDetalle(Long idDetalle, DetalleComandaUpdateDTO request) {
        DetalleComanda detalle = detalleComandaRepository.findById(idDetalle)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle no encontrado"));
        Comanda comanda = detalle.getComanda();
        validateComandaNotClosed(comanda);
        ensureNoInPreparation(comanda);
        if (!DetalleComanda.EstadoDetalle.PENDIENTE.name().equals(detalle.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se pueden modificar items en estado PENDIENTE");
        }
        if (request.getCantidad() != null && request.getCantidad() > 0) {
            detalle.setCantidad(request.getCantidad());
        }
        if (request.getNotas() != null) {
            detalle.setNotas(request.getNotas());
        }
        detalleComandaRepository.save(detalle);
        return mapToResponse(comanda);
    }

    @Transactional
    public ComandaResponseDTO cancelarDetalle(Long idDetalle) {
        DetalleComanda detalle = detalleComandaRepository.findById(idDetalle)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle no encontrado"));
        Comanda comanda = detalle.getComanda();
        validateComandaNotClosed(comanda);
        ensureNoInPreparation(comanda);
        if (!DetalleComanda.EstadoDetalle.PENDIENTE.name().equals(detalle.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se pueden cancelar items en estado PENDIENTE");
        }
        detalle.setEstado(DetalleComanda.EstadoDetalle.CANCELADO.name());
        detalleComandaRepository.save(detalle);
        checkAndTransitionComandaIfAllListo(comanda);
        return mapToResponse(comanda);
    }

    @Transactional
    public ComandaResponseDTO deleteDetalle(Long idDetalle) {
        DetalleComanda detalle = detalleComandaRepository.findById(idDetalle)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle no encontrado"));
        Comanda comanda = detalle.getComanda();
        validateComandaNotClosed(comanda);
        ensureNoInPreparation(comanda);
        if (!DetalleComanda.EstadoDetalle.PENDIENTE.name().equals(detalle.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se pueden eliminar items en estado PENDIENTE");
        }
        detalleComandaRepository.delete(detalle);
        return mapToResponse(comanda);
    }

    private void checkAndTransitionComandaIfAllListo(Comanda comanda) {
        List<DetalleComanda> detalles = detalleComandaRepository.findByComandaIdComanda(comanda.getIdComanda());
        boolean anyOutstanding = detalles.stream().anyMatch(d -> {
            String e = d.getEstado();
            return !DetalleComanda.EstadoDetalle.LISTO.name().equals(e) && !DetalleComanda.EstadoDetalle.CANCELADO.name().equals(e);
        });
        if (!anyOutstanding) {
            comanda.setEstado(Comanda.EstadoComanda.LISTA.name());
            comandaRepository.save(comanda);
        }
    }

    private void emitirEventoNuevosItems(Comanda comanda, List<DetalleComanda> items) {
        try {
            Long sucursalId = comanda.getSucursal().getIdSucursal();

            Map<String, Object> payload = new HashMap<>();
            payload.put("idComanda", comanda.getIdComanda());
            payload.put("numeroComanda", comanda.getNumeroComanda());
            payload.put("idMesa", comanda.getMesa() != null ? comanda.getMesa().getIdMesa() : null);
            payload.put("mesaNombre", comanda.getMesa() != null ? comanda.getMesa().getNumeroMesa() : "Mostrador");
            payload.put("tipoServicio", comanda.getTipoServicio());
            payload.put("estado", comanda.getEstado());
            payload.put("totalItems", items.size());

            List<Map<String, Object>> itemsPayload = items.stream().map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("idDetalleComanda", item.getIdDetalleComanda());
                itemMap.put("nombreProducto", item.getProductoFinal().getNombre());
                itemMap.put("cantidad", item.getCantidad());
                itemMap.put("notas", item.getNotas());
                itemMap.put("estado", item.getEstado());
                itemMap.put("estacionPreparacion", item.getEstacionPreparacion());
                return itemMap;
            }).collect(Collectors.toList());
            payload.put("items", itemsPayload);

            webSocketService.emitirEventoSucursal(sucursalId, "preparacion/nuevos-items", payload);

            log.debug("Evento WebSocket emitido: nuevos items para comanda {} en sucursal {}",
                    comanda.getNumeroComanda(), sucursalId);
        } catch (Exception e) {
            log.error("Error al emitir evento WebSocket de nuevos items", e);
        }
    }
}

