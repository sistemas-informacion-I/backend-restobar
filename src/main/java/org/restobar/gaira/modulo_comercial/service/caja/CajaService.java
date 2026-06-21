package org.restobar.gaira.modulo_comercial.service.caja;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.restobar.gaira.modulo_acceso.entity.Empleado;
import org.restobar.gaira.modulo_acceso.repository.EmpleadoRepository;
import org.restobar.gaira.modulo_comercial.dto.caja.AbrirCajaRequest;
import org.restobar.gaira.modulo_comercial.dto.caja.ArqueoResponse;
import org.restobar.gaira.modulo_comercial.dto.caja.CajaResponse;
import org.restobar.gaira.modulo_comercial.dto.caja.CerrarCajaRequest;
import org.restobar.gaira.modulo_comercial.dto.caja.MovimientoCajaResponse;
import org.restobar.gaira.modulo_comercial.dto.caja.MovimientoManualRequest;
import org.restobar.gaira.modulo_comercial.entity.Caja;
import org.restobar.gaira.modulo_comercial.entity.Caja.Estado;
import org.restobar.gaira.modulo_comercial.entity.MovimientoCaja;
import org.restobar.gaira.modulo_comercial.entity.MovimientoCaja.Concepto;
import org.restobar.gaira.modulo_comercial.entity.MovimientoCaja.Tipo;
import org.restobar.gaira.modulo_comercial.mapper.caja.CajaMapper;
import org.restobar.gaira.modulo_comercial.repository.caja.CajaRepository;
import org.restobar.gaira.modulo_comercial.repository.caja.MovimientoCajaRepository;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.restobar.gaira.modulo_operaciones.repository.SucursalRepository;
import org.restobar.gaira.security.audit.annotation.Auditable;
import org.restobar.gaira.security.audit.util.AuditableService;
import org.restobar.gaira.security.userdetails.ApplicationUserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

/**
 * CU22 – Gestionar Caja.
 * Único responsable de la contabilidad operativa de la sucursal: apertura,
 * registro centralizado de movimientos, arqueo y cierre. Ningún otro CU debe
 * insertar en {@code movimiento_caja} directamente; deben usar
 * {@link #registrarMovimientoAutomatico}.
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CajaService implements AuditableService<Long, Object> {

    private static final String TIPO_SUPERUSER = "S";

    private final CajaRepository cajaRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;
    private final SucursalRepository sucursalRepository;
    private final EmpleadoRepository empleadoRepository;
    private final CajaMapper cajaMapper;

    // ─── AuditableService ─────────────────────────────────────────────────────

    @Override
    public Object getEntity(Long id) {
        return cajaRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof Caja c) {
            return cajaMapper.toAuditMap(c);
        } else if (entity instanceof CajaResponse dto) {
            return cajaMapper.toAuditMap(dto);
        }
        return Map.of();
    }

    // ─── Consultas ────────────────────────────────────────────────────────────

    /** Caja actualmente ABIERTA de la sucursal en contexto. Lanza 404 si no hay. */
    @Transactional(readOnly = true)
    public CajaResponse getCajaActual(ApplicationUserPrincipal principal, Long idSucursalParam) {
        Long idSucursal = resolverSucursalConsulta(principal, idSucursalParam);
        Caja caja = cajaRepository
                .findFirstBySucursal_IdSucursalAndEstado(idSucursal, Estado.ABIERTA)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No hay una caja abierta en la sucursal"));
        return buildResponse(caja, true);
    }

    @Transactional(readOnly = true)
    public CajaResponse findById(Long idCaja, ApplicationUserPrincipal principal) {
        Caja caja = cajaRepository.findById(idCaja)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Caja no encontrada"));
        verificarAccesoSucursal(principal, caja.getSucursal().getIdSucursal());
        return buildResponse(caja, true);
    }

    /** Historial de cajas. SU ve todas (o filtra por sucursal); el empleado solo su sucursal. */
    @Transactional(readOnly = true)
    public List<CajaResponse> findAll(ApplicationUserPrincipal principal, Long idSucursal, Estado estado) {
        boolean esSuper = esSuperusuario(principal);

        Long sucursalFiltro = esSuper ? idSucursal : principal.getSucursalId();
        if (!esSuper && sucursalFiltro == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario sin sucursal asignada");
        }

        List<Caja> cajas = sucursalFiltro != null
                ? cajaRepository.findBySucursal_IdSucursalOrderByFechaAperturaDesc(sucursalFiltro)
                : cajaRepository.findAllByFechaAperturaDesc();

        return cajas.stream()
                .filter(c -> estado == null || c.getEstado() == estado)
                .map(c -> buildResponse(c, false))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MovimientoCajaResponse> getMovimientos(Long idCaja, ApplicationUserPrincipal principal) {
        Caja caja = cajaRepository.findById(idCaja)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Caja no encontrada"));
        verificarAccesoSucursal(principal, caja.getSucursal().getIdSucursal());
        return movimientoCajaRepository.findByCaja_IdCajaOrderByFechaDesc(idCaja)
                .stream()
                .map(cajaMapper::toMovimientoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ArqueoResponse getArqueo(Long idCaja, ApplicationUserPrincipal principal) {
        Caja caja = cajaRepository.findById(idCaja)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Caja no encontrada"));
        verificarAccesoSucursal(principal, caja.getSucursal().getIdSucursal());
        BigDecimal ingresos = movimientoCajaRepository.sumMontoByCajaAndTipo(idCaja, Tipo.INGRESO);
        BigDecimal egresos = movimientoCajaRepository.sumMontoByCajaAndTipo(idCaja, Tipo.EGRESO);
        long cantidad = movimientoCajaRepository.countByCaja_IdCaja(idCaja);
        return cajaMapper.toArqueo(caja, ingresos, egresos, cantidad);
    }

    // ─── Apertura ───────────────────────────────────────────────────────────

    @Transactional
    @Auditable(tabla = "caja", operacion = "INSERT")
    public CajaResponse abrirCaja(AbrirCajaRequest request, ApplicationUserPrincipal principal) {
        Long idSucursal = resolverSucursalOperacion(principal, request.getIdSucursal());

        if (cajaRepository.existsBySucursal_IdSucursalAndEstado(idSucursal, Estado.ABIERTA)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe una caja abierta en esta sucursal. Ciérrela antes de abrir una nueva.");
        }

        Sucursal sucursal = sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada"));

        Empleado empleado = empleadoRepository.findByUsuario_IdUsuario(principal.getIdUsuario()).orElse(null);

        Caja caja = Caja.builder()
                .sucursal(sucursal)
                .empleadoApertura(empleado)
                .estado(Estado.ABIERTA)
                .montoInicial(request.getMontoInicial() != null ? request.getMontoInicial() : BigDecimal.ZERO)
                .observacionApertura(trimToNull(request.getObservacion()))
                .build();

        caja = cajaRepository.save(caja);
        return buildResponse(caja, true);
    }

    // ─── Movimientos ──────────────────────────────────────────────────────────

    /** Movimiento manual (INGRESO_EXTRA o RETIRO) registrado por el cajero. */
    @Transactional
    @Auditable(tabla = "caja", operacion = "UPDATE", idParamName = "idCaja")
    public CajaResponse registrarMovimientoManual(Long idCaja, MovimientoManualRequest request,
                                                  ApplicationUserPrincipal principal) {
        Caja caja = cajaRepository.findById(idCaja)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Caja no encontrada"));
        verificarAccesoSucursal(principal, caja.getSucursal().getIdSucursal());
        verificarCajaAbierta(caja);

        Concepto concepto = request.getConcepto();
        if (concepto != Concepto.INGRESO_EXTRA && concepto != Concepto.RETIRO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo se permiten movimientos manuales de tipo INGRESO_EXTRA o RETIRO");
        }
        Tipo tipo = concepto == Concepto.INGRESO_EXTRA ? Tipo.INGRESO : Tipo.EGRESO;

        Empleado empleado = empleadoRepository.findByUsuario_IdUsuario(principal.getIdUsuario()).orElse(null);

        MovimientoCaja movimiento = MovimientoCaja.builder()
                .caja(caja)
                .tipo(tipo)
                .concepto(concepto)
                .monto(request.getMonto())
                .descripcion(trimToNull(request.getDescripcion()))
                .empleado(empleado)
                .build();
        movimientoCajaRepository.save(movimiento);

        return buildResponse(caja, true);
    }

    /**
     * Punto de entrada para que OTROS CU registren movimientos automáticos
     * (Ventas CU15, Compras CU12, Notas de Salida CU17). Busca la caja abierta
     * de la sucursal y le inserta el movimiento. Si no hay caja abierta, falla.
     */
    @Transactional
    public MovimientoCajaResponse registrarMovimientoAutomatico(Long idSucursal, Tipo tipo, Concepto concepto,
                                                               BigDecimal monto, String descripcion, Long referenciaId) {
        if (monto == null || monto.signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El monto del movimiento debe ser mayor a 0");
        }
        Caja caja = cajaRepository.findFirstBySucursal_IdSucursalAndEstado(idSucursal, Estado.ABIERTA)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                        "No hay una caja abierta en la sucursal; no se puede registrar el movimiento"));

        MovimientoCaja movimiento = MovimientoCaja.builder()
                .caja(caja)
                .tipo(tipo)
                .concepto(concepto)
                .monto(monto)
                .descripcion(trimToNull(descripcion))
                .referenciaId(referenciaId)
                .build();
        movimiento = movimientoCajaRepository.save(movimiento);
        return cajaMapper.toMovimientoResponse(movimiento);
    }

    // ─── Cierre / Arqueo ────────────────────────────────────────────────────

    @Transactional
    @Auditable(tabla = "caja", operacion = "UPDATE", idParamName = "idCaja")
    public CajaResponse cerrarCaja(Long idCaja, CerrarCajaRequest request, ApplicationUserPrincipal principal) {
        Caja caja = cajaRepository.findById(idCaja)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Caja no encontrada"));
        verificarAccesoSucursal(principal, caja.getSucursal().getIdSucursal());
        verificarCajaAbierta(caja);

        BigDecimal ingresos = movimientoCajaRepository.sumMontoByCajaAndTipo(idCaja, Tipo.INGRESO);
        BigDecimal egresos = movimientoCajaRepository.sumMontoByCajaAndTipo(idCaja, Tipo.EGRESO);
        BigDecimal saldoEsperado = caja.getMontoInicial().add(ingresos).subtract(egresos);
        BigDecimal montoFinal = request.getMontoFinal();
        BigDecimal diferencia = montoFinal.subtract(saldoEsperado);

        Empleado empleado = empleadoRepository.findByUsuario_IdUsuario(principal.getIdUsuario()).orElse(null);

        caja.setEstado(Estado.CERRADA);
        caja.setSaldoEsperado(saldoEsperado);
        caja.setMontoFinal(montoFinal);
        caja.setDiferencia(diferencia);
        caja.setFechaCierre(LocalDateTime.now());
        caja.setEmpleadoCierre(empleado);
        caja.setObservacionCierre(trimToNull(request.getObservacion()));

        caja = cajaRepository.save(caja);
        return buildResponse(caja, true);
    }

    // ─── Helpers privados ─────────────────────────────────────────────────────

    private CajaResponse buildResponse(Caja caja, boolean includeMovimientos) {
        Long idCaja = caja.getIdCaja();
        BigDecimal ingresos = movimientoCajaRepository.sumMontoByCajaAndTipo(idCaja, Tipo.INGRESO);
        BigDecimal egresos = movimientoCajaRepository.sumMontoByCajaAndTipo(idCaja, Tipo.EGRESO);
        long cantidad = movimientoCajaRepository.countByCaja_IdCaja(idCaja);

        List<MovimientoCajaResponse> movimientos = null;
        if (includeMovimientos) {
            movimientos = movimientoCajaRepository.findByCaja_IdCajaOrderByFechaDesc(idCaja)
                    .stream()
                    .map(cajaMapper::toMovimientoResponse)
                    .toList();
        }
        return cajaMapper.toResponse(caja, ingresos, egresos, cantidad, movimientos);
    }

    private void verificarCajaAbierta(Caja caja) {
        if (caja.getEstado() != Estado.ABIERTA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La caja está cerrada");
        }
    }

    private boolean esSuperusuario(ApplicationUserPrincipal principal) {
        return TIPO_SUPERUSER.equals(principal.getTipoUsuario());
    }

    /** Resuelve la sucursal para operaciones de escritura (abrir caja). */
    private Long resolverSucursalOperacion(ApplicationUserPrincipal principal, Long idSucursalRequest) {
        if (esSuperusuario(principal)) {
            if (idSucursalRequest == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Debe seleccionar la sucursal de la caja");
            }
            return idSucursalRequest;
        }
        Long propia = principal.getSucursalId();
        if (propia == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario sin sucursal asignada");
        }
        if (idSucursalRequest != null && !idSucursalRequest.equals(propia)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No puede operar la caja de otra sucursal");
        }
        return propia;
    }

    /** Resuelve la sucursal para consultas (caja actual). */
    private Long resolverSucursalConsulta(ApplicationUserPrincipal principal, Long idSucursalParam) {
        if (esSuperusuario(principal)) {
            if (idSucursalParam == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Debe indicar la sucursal a consultar");
            }
            return idSucursalParam;
        }
        Long propia = principal.getSucursalId();
        if (propia == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario sin sucursal asignada");
        }
        return propia;
    }

    /** El empleado solo puede acceder a cajas de su propia sucursal; el SU a todas. */
    private void verificarAccesoSucursal(ApplicationUserPrincipal principal, Long idSucursalCaja) {
        if (esSuperusuario(principal)) {
            return;
        }
        Long propia = principal.getSucursalId();
        if (propia == null || !propia.equals(idSucursalCaja)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tiene acceso a la caja de esta sucursal");
        }
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
