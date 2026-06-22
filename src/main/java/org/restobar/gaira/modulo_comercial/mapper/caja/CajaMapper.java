package org.restobar.gaira.modulo_comercial.mapper.caja;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restobar.gaira.modulo_acceso.entity.Empleado;
import org.restobar.gaira.modulo_comercial.dto.caja.ArqueoResponse;
import org.restobar.gaira.modulo_comercial.dto.caja.CajaResponse;
import org.restobar.gaira.modulo_comercial.dto.caja.MovimientoCajaResponse;
import org.restobar.gaira.modulo_comercial.entity.Caja;
import org.restobar.gaira.modulo_comercial.entity.MovimientoCaja;
import org.springframework.stereotype.Component;

@Component
public class CajaMapper {

    // ─── Movimiento ─────────────────────────────────────────────────────────

    public MovimientoCajaResponse toMovimientoResponse(MovimientoCaja m) {
        if (m == null) return null;
        return MovimientoCajaResponse.builder()
                .idMovimiento(m.getIdMovimiento())
                .idCaja(m.getCaja() != null ? m.getCaja().getIdCaja() : null)
                .tipo(m.getTipo())
                .concepto(m.getConcepto())
                .monto(m.getMonto())
                .descripcion(m.getDescripcion())
                .idEmpleado(m.getEmpleado() != null ? m.getEmpleado().getIdEmpleado() : null)
                .nombreEmpleado(nombreEmpleado(m.getEmpleado()))
                .referenciaId(m.getReferenciaId())
                .fecha(m.getFecha())
                .build();
    }

    // ─── Caja ───────────────────────────────────────────────────────────────

    /**
     * Convierte una caja a su response. Los totales se reciben ya calculados
     * desde el servicio (vía consultas SUM) para evitar inicializaciones lazy.
     *
     * @param movimientos lista de movimientos ya mapeados; null para vistas de listado.
     */
    public CajaResponse toResponse(Caja caja, BigDecimal totalIngresos, BigDecimal totalEgresos,
                                   long cantidadMovimientos, List<MovimientoCajaResponse> movimientos) {
        if (caja == null) return null;

        BigDecimal ingresos = totalIngresos != null ? totalIngresos : BigDecimal.ZERO;
        BigDecimal egresos = totalEgresos != null ? totalEgresos : BigDecimal.ZERO;
        BigDecimal saldoEsperado = caja.getMontoInicial().add(ingresos).subtract(egresos);

        return CajaResponse.builder()
                .idCaja(caja.getIdCaja())
                .idSucursal(caja.getSucursal() != null ? caja.getSucursal().getIdSucursal() : null)
                .nombreSucursal(caja.getSucursal() != null ? caja.getSucursal().getNombre() : null)
                .estado(caja.getEstado())
                .montoInicial(caja.getMontoInicial())
                .idEmpleadoApertura(caja.getEmpleadoApertura() != null ? caja.getEmpleadoApertura().getIdEmpleado() : null)
                .empleadoApertura(nombreEmpleado(caja.getEmpleadoApertura()))
                .fechaApertura(caja.getFechaApertura())
                .observacionApertura(caja.getObservacionApertura())
                .totalIngresos(ingresos)
                .totalEgresos(egresos)
                .saldoEsperado(saldoEsperado)
                .cantidadMovimientos((int) cantidadMovimientos)
                .montoFinal(caja.getMontoFinal())
                .diferencia(caja.getDiferencia())
                .idEmpleadoCierre(caja.getEmpleadoCierre() != null ? caja.getEmpleadoCierre().getIdEmpleado() : null)
                .empleadoCierre(nombreEmpleado(caja.getEmpleadoCierre()))
                .fechaCierre(caja.getFechaCierre())
                .observacionCierre(caja.getObservacionCierre())
                .movimientos(movimientos)
                .build();
    }

    public ArqueoResponse toArqueo(Caja caja, BigDecimal totalIngresos, BigDecimal totalEgresos,
                                   long cantidadMovimientos) {
        BigDecimal ingresos = totalIngresos != null ? totalIngresos : BigDecimal.ZERO;
        BigDecimal egresos = totalEgresos != null ? totalEgresos : BigDecimal.ZERO;
        return ArqueoResponse.builder()
                .idCaja(caja.getIdCaja())
                .montoInicial(caja.getMontoInicial())
                .totalIngresos(ingresos)
                .totalEgresos(egresos)
                .saldoEsperado(caja.getMontoInicial().add(ingresos).subtract(egresos))
                .cantidadMovimientos((int) cantidadMovimientos)
                .build();
    }

    // ─── Auditoría ──────────────────────────────────────────────────────────

    public Map<String, Object> toAuditMap(Caja caja) {
        if (caja == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idCaja", caja.getIdCaja());
        map.put("idSucursal", caja.getSucursal() != null ? caja.getSucursal().getIdSucursal() : null);
        map.put("estado", caja.getEstado() != null ? caja.getEstado().name() : null);
        map.put("montoInicial", caja.getMontoInicial());
        map.put("montoFinal", caja.getMontoFinal());
        map.put("saldoEsperado", caja.getSaldoEsperado());
        map.put("diferencia", caja.getDiferencia());
        return map;
    }

    public Map<String, Object> toAuditMap(CajaResponse dto) {
        if (dto == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idCaja", dto.getIdCaja());
        map.put("idSucursal", dto.getIdSucursal());
        map.put("estado", dto.getEstado() != null ? dto.getEstado().name() : null);
        map.put("montoInicial", dto.getMontoInicial());
        map.put("montoFinal", dto.getMontoFinal());
        map.put("saldoEsperado", dto.getSaldoEsperado());
        map.put("diferencia", dto.getDiferencia());
        return map;
    }

    // ─── Helpers ────────────────────────────────────────────────────────────

    private String nombreEmpleado(Empleado empleado) {
        if (empleado == null || empleado.getUsuario() == null) return null;
        return empleado.getUsuario().getNombre() + " " + empleado.getUsuario().getApellido();
    }
}
