package org.restobar.gaira.modulo_electronico.service.entrega;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.restobar.gaira.modulo_acceso.entity.Empleado;
import org.restobar.gaira.modulo_acceso.repository.EmpleadoRepository;
import org.restobar.gaira.modulo_electronico.dto.entrega.EntregaResponse;
import org.restobar.gaira.modulo_electronico.dto.entrega.UbicacionRequest;
import org.restobar.gaira.modulo_electronico.entity.Entrega;
import org.restobar.gaira.modulo_electronico.entity.UbicacionEmpleado;
import org.restobar.gaira.modulo_electronico.mapper.entrega.EntregaMapper;
import org.restobar.gaira.modulo_electronico.repository.EntregaRepository;
import org.restobar.gaira.modulo_electronico.repository.UbicacionEmpleadoRepository;
import org.restobar.gaira.modulo_operaciones.entity.Comanda;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.restobar.gaira.modulo_operaciones.repository.ComandaRepository;
import org.restobar.gaira.security.audit.annotation.Auditable;
import org.restobar.gaira.security.utils.SecurityUtils;
import org.restobar.gaira.shared.websocket.WebSocketService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntregaService {

    private final EntregaRepository entregaRepository;
    private final UbicacionEmpleadoRepository ubicacionEmpleadoRepository;
    private final ComandaRepository comandaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final EntregaMapper entregaMapper;
    private final WebSocketService webSocketService;
    private final SecurityUtils securityUtils;

    private static final BigDecimal RADIO_MAXIMO_KM = BigDecimal.valueOf(2.0);
    private static final int RADIO_TERRESTRE_M = 6371000;

    private Long getCurrentUserId() {
        Long id = securityUtils.getCurrentUserId();
        if (id == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        return id;
    }

    /**
     * Resuelve el idEmpleado a partir del idUsuario autenticado.
     * Retorna null si el usuario no tiene registro de empleado (ej. SUPERUSER sin empleado).
     */
    private Long resolveEmpleadoId(Long idUsuario) {
        return empleadoRepository.findByUsuario_IdUsuario(idUsuario)
                .map(Empleado::getIdEmpleado)
                .orElse(null);
    }

    @Transactional
    @Auditable(tabla = "entrega", operacion = "INSERT")
    public EntregaResponse crearEntrega(Long idComanda, String direccionEntrega,
                                        BigDecimal latitud, BigDecimal longitud,
                                        BigDecimal costoEnvio) {
        Comanda comanda = comandaRepository.findById(idComanda)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comanda no encontrada"));

        if (!"ONLINE".equals(comanda.getTipoServicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo se pueden crear entregas para comandas ONLINE");
        }

        entregaRepository.findByComandaIdComanda(idComanda).ifPresent(e -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe una entrega para esta comanda");
        });

        Sucursal sucursal = comanda.getSucursal();
        if (sucursal.getLatitud() == null || sucursal.getLongitud() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La sucursal no tiene coordenadas configuradas");
        }

        double distanciaKm = haversine(
                sucursal.getLatitud().doubleValue(), sucursal.getLongitud().doubleValue(),
                latitud.doubleValue(), longitud.doubleValue());

        int tiempoEstimadoMin = (int) Math.ceil(distanciaKm / 20.0 * 60);

        Entrega entrega = Entrega.builder()
                .comanda(comanda)
                .direccionEntrega(direccionEntrega)
                .latitud(latitud)
                .longitud(longitud)
                .distanciaKm(BigDecimal.valueOf(distanciaKm).setScale(2, RoundingMode.HALF_UP))
                .tiempoEstimadoMin(tiempoEstimadoMin)
                .costoEnvio(costoEnvio != null ? costoEnvio : BigDecimal.ZERO)
                .estado(Entrega.EstadoEntrega.PENDIENTE)
                .build();

        entrega = entregaRepository.save(entrega);
        log.info("Entrega creada para comanda {}: distancia={}km, tiempo={}min",
                comanda.getNumeroComanda(), distanciaKm, tiempoEstimadoMin);

        EntregaResponse response = entregaMapper.toResponse(entrega);
        notificarRepartidoresCercanos(entrega, response);
        return response;
    }

    @Transactional(readOnly = true)
    public EntregaResponse getEntrega(Long id) {
        Entrega entrega = entregaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrega no encontrada"));
        return entregaMapper.toResponse(entrega);
    }

    @Transactional(readOnly = true)
    public EntregaResponse getEntregaByComanda(Long idComanda) {
        Entrega entrega = entregaRepository.findByComandaIdComanda(idComanda)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrega no encontrada"));
        return entregaMapper.toResponse(entrega);
    }

    @Transactional(readOnly = true)
    public List<EntregaResponse> getEntregasByUsuario(Long idUsuario) {
        return entregaRepository.findByIdUsuarioRepartidor(idUsuario).stream()
                .map(entregaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EntregaResponse> listarPendientes() {
        return entregaRepository.findByEstado(Entrega.EstadoEntrega.PENDIENTE).stream()
                .map(entregaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> verificarDisponibilidad(Long idEntrega, BigDecimal latParam, BigDecimal lngParam) {
        Long idUsuario = getCurrentUserId();

        Entrega entrega = entregaRepository.findById(idEntrega)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrega no encontrada"));

        boolean tieneEntregaActiva = entregaRepository.existsByIdUsuarioRepartidorAndEstadoIn(
                idUsuario, List.of(Entrega.EstadoEntrega.ASIGNADO, Entrega.EstadoEntrega.EN_CAMINO));

        if (tieneEntregaActiva) {
            return Map.of("disponible", false, "motivo", "Ya tienes una entrega activa");
        }

        BigDecimal latRepartidor = null;
        BigDecimal lngRepartidor = null;

        // 1. Si el frontend envio lat/lng (ej: GPS manual del admin/superuser), usarlos directamente
        if (latParam != null && lngParam != null) {
            latRepartidor = latParam;
            lngRepartidor = lngParam;
        } else {
            // 2. Intentar con ubicacion_empleado
            Long idEmpleado = resolveEmpleadoId(idUsuario);
            Optional<UbicacionEmpleado> optUbicacion = idEmpleado != null
                    ? ubicacionEmpleadoRepository.findLatestByEmpleadoId(idEmpleado)
                    : Optional.empty();

            if (optUbicacion.isPresent()) {
                UbicacionEmpleado ue = optUbicacion.get();
                latRepartidor = ue.getLatitud();
                lngRepartidor = ue.getLongitud();
            } else {
                // 3. Fallback: latitudActual de entrega activa
                List<Entrega> activas = entregaRepository.findByIdUsuarioRepartidorAndEstadoIn(
                        idUsuario, List.of(Entrega.EstadoEntrega.ASIGNADO, Entrega.EstadoEntrega.EN_CAMINO));
                if (!activas.isEmpty() && activas.get(0).getLatitudActual() != null) {
                    latRepartidor = activas.get(0).getLatitudActual();
                    lngRepartidor = activas.get(0).getLongitudActual();
                }
            }
        }

        if (latRepartidor == null || lngRepartidor == null) {
            return Map.of("disponible", false, "motivo", "No se ha registrado tu ubicacion. Activa el GPS.");
        }

        Sucursal sucursal = entrega.getComanda().getSucursal();
        if (sucursal.getLatitud() == null || sucursal.getLongitud() == null) {
            return Map.of("disponible", false, "motivo", "Sucursal sin coordenadas");
        }

        double distancia = haversine(
                latRepartidor.doubleValue(), lngRepartidor.doubleValue(),
                sucursal.getLatitud().doubleValue(), sucursal.getLongitud().doubleValue());

        boolean dentroDelRadio = BigDecimal.valueOf(distancia).compareTo(RADIO_MAXIMO_KM) <= 0;

        return Map.of(
                "disponible", dentroDelRadio,
                "distanciaKm", BigDecimal.valueOf(distancia).setScale(3, RoundingMode.HALF_UP),
                "motivo", dentroDelRadio ? "Disponible" : "Estas a mas de 2 km de la sucursal"
        );
    }

    @Transactional
    @Auditable(tabla = "entrega", operacion = "UPDATE", idParamName = "idEntrega")
    public EntregaResponse aceptarEntrega(Long idEntrega, BigDecimal latParam, BigDecimal lngParam) {
        Long idUsuario = getCurrentUserId();

        Entrega entrega = entregaRepository.findById(idEntrega)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrega no encontrada"));

        if (entrega.getEstado() != Entrega.EstadoEntrega.PENDIENTE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La entrega ya no esta disponible");
        }

        boolean tieneActiva = entregaRepository.existsByIdUsuarioRepartidorAndEstadoIn(
                idUsuario, List.of(Entrega.EstadoEntrega.ASIGNADO, Entrega.EstadoEntrega.EN_CAMINO));
        if (tieneActiva) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya tienes una entrega activa. Finalizala antes de aceptar otra.");
        }

        BigDecimal latRepartidor = null;
        BigDecimal lngRepartidor = null;

        // 1. Si el frontend envio lat/lng (GPS manual del admin/superuser)
        if (latParam != null && lngParam != null) {
            latRepartidor = latParam;
            lngRepartidor = lngParam;
        } else {
            // 2. Intentar ubicacion_empleado
            Long idEmpleado = resolveEmpleadoId(idUsuario);
            Optional<UbicacionEmpleado> optUbicacion = idEmpleado != null
                    ? ubicacionEmpleadoRepository.findLatestByEmpleadoId(idEmpleado)
                    : Optional.empty();
            if (optUbicacion.isPresent()) {
                UbicacionEmpleado ue = optUbicacion.get();
                latRepartidor = ue.getLatitud();
                lngRepartidor = ue.getLongitud();
            } else {
                // 3. Fallback: latitudActual de entrega activa
                List<Entrega> activas = entregaRepository.findByIdUsuarioRepartidorAndEstadoIn(
                        idUsuario, List.of(Entrega.EstadoEntrega.ASIGNADO, Entrega.EstadoEntrega.EN_CAMINO));
                if (!activas.isEmpty() && activas.get(0).getLatitudActual() != null) {
                    latRepartidor = activas.get(0).getLatitudActual();
                    lngRepartidor = activas.get(0).getLongitudActual();
                }
            }
        }

        if (latRepartidor == null || lngRepartidor == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se ha registrado tu ubicacion. Activa el GPS antes de aceptar.");
        }

        Sucursal sucursal = entrega.getComanda().getSucursal();
        if (sucursal.getLatitud() == null || sucursal.getLongitud() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sucursal sin coordenadas");
        }

        double distancia = haversine(
                latRepartidor.doubleValue(), lngRepartidor.doubleValue(),
                sucursal.getLatitud().doubleValue(), sucursal.getLongitud().doubleValue());

        if (BigDecimal.valueOf(distancia).compareTo(RADIO_MAXIMO_KM) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Estas a mas de 2 km de la sucursal. No puedes aceptar esta entrega.");
        }

        entrega.setIdUsuarioRepartidor(idUsuario);
        entrega.setEstado(Entrega.EstadoEntrega.ASIGNADO);
        entrega.setFechaAsignacion(LocalDateTime.now());
        entrega = entregaRepository.save(entrega);

        log.info("Usuario {} acepto entrega {}", idUsuario, idEntrega);

        EntregaResponse response = entregaMapper.toResponse(entrega);
        webSocketService.emitirEventoEntrega(idEntrega, "asignada", response);
        webSocketService.emitirEventoRepartidores("entrega-tomada",
                Map.of("idEntrega", idEntrega, "estado", "ASIGNADO"));

        return response;
    }

    @Transactional
    @Auditable(tabla = "entrega", operacion = "UPDATE", idParamName = "idEntrega")
    public EntregaResponse iniciarViaje(Long idEntrega) {
        Long idUsuario = getCurrentUserId();
        Entrega entrega = validarPropietario(idEntrega, idUsuario);

        if (entrega.getEstado() != Entrega.EstadoEntrega.ASIGNADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La entrega debe estar en ASIGNADO para iniciar el viaje");
        }

        entrega.setEstado(Entrega.EstadoEntrega.EN_CAMINO);
        entrega = entregaRepository.save(entrega);

        log.info("Usuario {} inicio viaje para entrega {}", idUsuario, idEntrega);
        EntregaResponse response = entregaMapper.toResponse(entrega);
        webSocketService.emitirEventoEntrega(idEntrega, "en-camino", response);
        return response;
    }

    @Transactional
    public void reportarUbicacion(UbicacionRequest ubicacion) {
        Long idUsuario = getCurrentUserId();

        Empleado empleado = empleadoRepository.findByUsuario_IdUsuario(idUsuario).orElse(null);
        Long idEmpleado = empleado != null ? empleado.getIdEmpleado() : null;

        if (idEmpleado != null) {
            UbicacionEmpleado ue = UbicacionEmpleado.builder()
                    .empleado(empleado)
                    .latitud(ubicacion.latitud())
                    .longitud(ubicacion.longitud())
                    .build();
            ubicacionEmpleadoRepository.save(ue);
        }

        List<Entrega> activas = entregaRepository.findByIdUsuarioRepartidorAndEstadoIn(
                idUsuario, List.of(Entrega.EstadoEntrega.ASIGNADO, Entrega.EstadoEntrega.EN_CAMINO));

        for (Entrega e : activas) {
            e.setLatitudActual(ubicacion.latitud());
            e.setLongitudActual(ubicacion.longitud());
            entregaRepository.save(e);

            Map<String, Object> posicion = Map.of(
                    "idEntrega", e.getIdEntrega(),
                    "latitud", ubicacion.latitud(),
                    "longitud", ubicacion.longitud(),
                    "timestamp", LocalDateTime.now().toString()
            );
            webSocketService.emitirEventoEntrega(e.getIdEntrega(), "ubicacion", posicion);
        }
    }

    @Transactional
    @Auditable(tabla = "entrega", operacion = "UPDATE", idParamName = "idEntrega")
    public EntregaResponse marcarEntregado(Long idEntrega) {
        Long idUsuario = getCurrentUserId();
        Entrega entrega = validarPropietario(idEntrega, idUsuario);

        if (entrega.getEstado() != Entrega.EstadoEntrega.EN_CAMINO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La entrega debe estar EN_CAMINO para marcarla como entregada");
        }

        entrega.setEstado(Entrega.EstadoEntrega.ENTREGADO);
        entrega.setFechaEntrega(LocalDateTime.now());

        Comanda comanda = entrega.getComanda();
        comanda.setEstado(Comanda.EstadoComanda.CERRADA.name());
        comanda.setFechaCierre(LocalDateTime.now());

        entrega = entregaRepository.save(entrega);
        comandaRepository.save(comanda);

        log.info("Entrega {} completada por usuario {}. Comanda {} cerrada.", idEntrega, idUsuario, comanda.getNumeroComanda());
        EntregaResponse response = entregaMapper.toResponse(entrega);
        webSocketService.emitirEventoEntrega(idEntrega, "entregado", response);
        return response;
    }

    @Transactional
    @Auditable(tabla = "entrega", operacion = "UPDATE", idParamName = "idEntrega")
    public EntregaResponse cancelarEntrega(Long idEntrega, String motivo) {
        Entrega entrega = entregaRepository.findById(idEntrega)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrega no encontrada"));

        if (entrega.getEstado() == Entrega.EstadoEntrega.ENTREGADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede cancelar una entrega ya completada");
        }

        entrega.setEstado(Entrega.EstadoEntrega.CANCELADO);
        entrega.setObservaciones(
                (entrega.getObservaciones() != null ? entrega.getObservaciones() + ". " : "")
                        + "Cancelado: " + (motivo != null ? motivo : "Sin motivo especificado"));
        entrega = entregaRepository.save(entrega);

        log.info("Entrega {} cancelada. Motivo: {}", idEntrega, motivo);
        EntregaResponse response = entregaMapper.toResponse(entrega);
        webSocketService.emitirEventoEntrega(idEntrega, "cancelada", response);
        return response;
    }

    // --- metodos privados ---

    private Entrega validarPropietario(Long idEntrega, Long idUsuario) {
        Entrega entrega = entregaRepository.findById(idEntrega)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrega no encontrada"));

        if (entrega.getIdUsuarioRepartidor() == null || !entrega.getIdUsuarioRepartidor().equals(idUsuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta entrega no te pertenece");
        }

        return entrega;
    }

    private void notificarRepartidoresCercanos(Entrega entrega, EntregaResponse response) {
        Sucursal sucursal = entrega.getComanda().getSucursal();
        if (sucursal.getLatitud() == null || sucursal.getLongitud() == null) return;

        Map<String, Object> notificacion = Map.of(
                "idEntrega", entrega.getIdEntrega(),
                "numeroComanda", entrega.getComanda().getNumeroComanda(),
                "direccionEntrega", entrega.getDireccionEntrega(),
                "distanciaKm", entrega.getDistanciaKm(),
                "tiempoEstimadoMin", entrega.getTiempoEstimadoMin(),
                "costoEnvio", entrega.getCostoEnvio(),
                "sucursalLatitud", sucursal.getLatitud(),
                "sucursalLongitud", sucursal.getLongitud(),
                "nombreSucursal", sucursal.getNombre()
        );

        webSocketService.emitirEventoRepartidores("nueva-entrega", notificacion);
    }

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (RADIO_TERRESTRE_M * c) / 1000.0;
    }
}
