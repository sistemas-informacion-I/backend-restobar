package org.restobar.gaira.modulo_operaciones.service.preparacion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_operaciones.dto.comanda.MarcarListoResponseDTO;
import org.restobar.gaira.modulo_operaciones.dto.comanda.PreparacionQueueItemDTO;
import org.restobar.gaira.modulo_operaciones.dto.comanda.PreparacionQueueResponseDTO;
import org.restobar.gaira.modulo_operaciones.entity.Comanda;
import org.restobar.gaira.modulo_operaciones.entity.DetalleComanda;
import org.restobar.gaira.modulo_operaciones.repository.ComandaRepository;
import org.restobar.gaira.modulo_operaciones.repository.DetalleComandaRepository;
import org.restobar.gaira.security.utils.SecurityUtils;
import org.restobar.gaira.shared.websocket.WebSocketService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PreparacionService {

    private final DetalleComandaRepository detalleComandaRepository;
    private final ComandaRepository comandaRepository;
    private final InventarioPreparacionService inventarioPreparacionService;
    private final SecurityUtils securityUtils;
    private final WebSocketService webSocketService;

    @Transactional(readOnly = true)
    public List<PreparacionQueueResponseDTO> obtenerColaPorEstacion(Long idSucursal, String estacion) {
        if (!estacion.equals("COCINA") && !estacion.equals("BARRA")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estación inválida");
        }

        List<DetalleComanda> detalles = detalleComandaRepository
                .findByComandaSucursalIdSucursalAndEstacionPreparacionOrderByFechaCreacionAsc(idSucursal, estacion);

        List<DetalleComanda> detallesFiltrados = detalles.stream()
                .filter(d -> DetalleComanda.EstadoDetalle.PENDIENTE.name().equals(d.getEstado()) ||
                        DetalleComanda.EstadoDetalle.EN_PREPARACION.name().equals(d.getEstado()))
                .collect(Collectors.toList());

        if (detallesFiltrados.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, List<DetalleComanda>> agrupado = detallesFiltrados.stream()
                .collect(Collectors.groupingBy(d -> d.getComanda().getIdComanda()));

        return agrupado.entrySet().stream()
                .map(entry -> {
                    Comanda comanda = entry.getValue().get(0).getComanda();
                    List<DetalleComanda> detallesList = entry.getValue();
                    return mapToQueueResponse(comanda, detallesList);
                })
                .sorted(Comparator.comparing(PreparacionQueueResponseDTO::getIdComanda))
                .collect(Collectors.toList());
    }

    @Transactional
    public PreparacionQueueItemDTO tomarItem(Long idDetalleComanda) {
        DetalleComanda detalle = detalleComandaRepository.findById(idDetalleComanda)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item no encontrado"));

        if (!DetalleComanda.EstadoDetalle.PENDIENTE.name().equals(detalle.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo se pueden tomar items en estado PENDIENTE");
        }

        Usuario usuario = securityUtils.getCurrentUser();
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }

        String nombreEmpleado = (usuario.getNombre() != null ? usuario.getNombre() : "")
                + (usuario.getApellido() != null ? " " + usuario.getApellido() : "");

        detalle.setEmpleadoAsignado(nombreEmpleado.trim());
        detalle.setFechaAceptacion(LocalDateTime.now());
        detalle.setEstado(DetalleComanda.EstadoDetalle.EN_PREPARACION.name());
        detalle = detalleComandaRepository.save(detalle);

        log.info("Item {} tomado por {} para preparación", idDetalleComanda, nombreEmpleado);

        // Notificar por WebSocket
        PreparacionQueueItemDTO itemDTO = mapToQueueItem(detalle);
        Long sucursalId = detalle.getComanda().getSucursal().getIdSucursal();
        webSocketService.emitirEventoSucursal(sucursalId, "preparacion/item-tomado", itemDTO);

        return itemDTO;
    }

    @Transactional
    public MarcarListoResponseDTO marcarItemListo(Long idDetalleComanda) {
        DetalleComanda detalle = detalleComandaRepository.findById(idDetalleComanda)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item no encontrado"));

        if (!DetalleComanda.EstadoDetalle.EN_PREPARACION.name().equals(detalle.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo se pueden marcar como LISTO items en estado EN_PREPARACION");
        }

        // Intentar descontar inventario
        Map<String, Object> descuentoResult = inventarioPreparacionService.descontarInventarioFIFO(
                detalle.getProductoFinal(),
                detalle.getComanda().getSucursal().getIdSucursal(),
                detalle.getCantidad()
        );

        if (!(Boolean) descuentoResult.get("exitoso")) {
            log.warn("Fallo descuento de inventario para item {}: {}", idDetalleComanda, descuentoResult.get("mensaje"));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Error descuento de inventario: " + descuentoResult.get("mensaje"));
        }

        // Cambiar estado del item a LISTO
        detalle.setEstado(DetalleComanda.EstadoDetalle.LISTO.name());
        detalle = detalleComandaRepository.save(detalle);

        // Notificar por WebSocket: item listo
        PreparacionQueueItemDTO itemDTO = mapToQueueItem(detalle);
        Long sucursalId = detalle.getComanda().getSucursal().getIdSucursal();
        webSocketService.emitirEventoSucursal(sucursalId, "preparacion/item-listo", itemDTO);

        // Verificar si todos los items de la comanda están LISTO
        Comanda comanda = detalle.getComanda();
        List<DetalleComanda> detallesComanda = detalleComandaRepository.findByComandaIdComanda(comanda.getIdComanda());
        boolean todosListos = detallesComanda.stream()
                .allMatch(d -> DetalleComanda.EstadoDetalle.LISTO.name().equals(d.getEstado()));

        String estadoComanda = comanda.getEstado();
        if (todosListos && !Comanda.EstadoComanda.LISTA.name().equals(comanda.getEstado())) {
            comanda.setEstado(Comanda.EstadoComanda.LISTA.name());
            comanda = comandaRepository.save(comanda);
            estadoComanda = Comanda.EstadoComanda.LISTA.name();
            log.info("Comanda {} ahora está LISTA", comanda.getIdComanda());

            // Notificar por WebSocket: comanda lista
            Map<String, Object> comandaListaPayload = new HashMap<>();
            comandaListaPayload.put("idComanda", comanda.getIdComanda());
            comandaListaPayload.put("numeroComanda", comanda.getNumeroComanda());
            comandaListaPayload.put("estado", estadoComanda);
            comandaListaPayload.put("mesaNombre", comanda.getMesa() != null ? comanda.getMesa().getNumeroMesa() : "Mostrador");
            comandaListaPayload.put("tipoServicio", comanda.getTipoServicio());
            webSocketService.emitirEventoSucursal(sucursalId, "preparacion/comanda-lista", comandaListaPayload);
        }

        return MarcarListoResponseDTO.builder()
                .idDetalleComanda(idDetalleComanda)
                .idComanda(comanda.getIdComanda())
                .estado(DetalleComanda.EstadoDetalle.LISTO.name())
                .estadoComanda(estadoComanda)
                .exitoso(true)
                .mensaje("Item marcado como LISTO y inventario descontado")
                .descuentoInventario(MarcarListoResponseDTO.DescuentoInventarioDTO.builder()
                        .exitoso(true)
                        .mensaje((String) descuentoResult.get("mensaje"))
                        .ingredientesDescontados((Integer) descuentoResult.get("ingredientesDescontados"))
                        .ingredientesConError(0)
                        .build())
                .build();
    }

    @Transactional(readOnly = true)
    public List<PreparacionQueueResponseDTO> obtenerColaCompleta(Long idSucursal) {
        List<DetalleComanda> detalles = detalleComandaRepository
                .findByComandaSucursalIdSucursalOrderByFechaCreacionAsc(idSucursal);

        List<DetalleComanda> detallesFiltrados = detalles.stream()
                .filter(d -> DetalleComanda.EstadoDetalle.PENDIENTE.name().equals(d.getEstado()) ||
                        DetalleComanda.EstadoDetalle.EN_PREPARACION.name().equals(d.getEstado()))
                .collect(Collectors.toList());

        if (detallesFiltrados.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, List<DetalleComanda>> agrupado = detallesFiltrados.stream()
                .collect(Collectors.groupingBy(d -> d.getComanda().getIdComanda()));

        return agrupado.entrySet().stream()
                .map(entry -> {
                    Comanda comanda = entry.getValue().get(0).getComanda();
                    List<DetalleComanda> detallesList = entry.getValue();
                    return mapToQueueResponse(comanda, detallesList);
                })
                .sorted(Comparator.comparing(PreparacionQueueResponseDTO::getIdComanda))
                .collect(Collectors.toList());
    }

    private PreparacionQueueResponseDTO mapToQueueResponse(Comanda comanda, List<DetalleComanda> detalles) {
        int pendientes = (int) detalles.stream()
                .filter(d -> DetalleComanda.EstadoDetalle.PENDIENTE.name().equals(d.getEstado()))
                .count();
        int enPreparacion = (int) detalles.stream()
                .filter(d -> DetalleComanda.EstadoDetalle.EN_PREPARACION.name().equals(d.getEstado()))
                .count();
        int listos = (int) detalles.stream()
                .filter(d -> DetalleComanda.EstadoDetalle.LISTO.name().equals(d.getEstado()))
                .count();

        return PreparacionQueueResponseDTO.builder()
                .idComanda(comanda.getIdComanda())
                .numeroComanda(comanda.getNumeroComanda())
                .mesaNombre(comanda.getMesa() != null ? comanda.getMesa().getNumeroMesa() : "Mostrador")
                .tipoServicio(comanda.getTipoServicio())
                .estadoComanda(comanda.getEstado())
                .totalItems(detalles.size())
                .itemsPendientes(pendientes)
                .itemsEnPreparacion(enPreparacion)
                .itemsListos(listos)
                .items(detalles.stream()
                        .map(this::mapToQueueItem)
                        .sorted(Comparator.comparing(PreparacionQueueItemDTO::getFechaCreacion))
                        .collect(Collectors.toList()))
                .build();
    }

    private PreparacionQueueItemDTO mapToQueueItem(DetalleComanda detalle) {
        Integer tiempoTranscurrido = null;
        if (detalle.getFechaCreacion() != null) {
            tiempoTranscurrido = (int) ChronoUnit.SECONDS.between(detalle.getFechaCreacion(), LocalDateTime.now());
        }

        return PreparacionQueueItemDTO.builder()
                .idDetalleComanda(detalle.getIdDetalleComanda())
                .idComanda(detalle.getComanda().getIdComanda())
                .numeroComanda(detalle.getComanda().getNumeroComanda())
                .mesaNombre(detalle.getComanda().getMesa() != null ? detalle.getComanda().getMesa().getNumeroMesa() : "Mostrador")
                .tipoServicio(detalle.getComanda().getTipoServicio())
                .nombreProducto(detalle.getProductoFinal().getNombre())
                .cantidad(detalle.getCantidad())
                .notas(detalle.getNotas())
                .estado(detalle.getEstado())
                .estacionPreparacion(detalle.getEstacionPreparacion())
                .fechaCreacion(detalle.getFechaCreacion())
                .fechaAceptacion(detalle.getFechaAceptacion())
                .empleadoAsignado(detalle.getEmpleadoAsignado())
                .tiempoTranscurrido(tiempoTranscurrido)
                .build();
    }
}
