package org.restobar.gaira.modulo_comercial.dto.caja;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.restobar.gaira.modulo_comercial.entity.Caja.Estado;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CajaResponse {

    private Long idCaja;

    private Long idSucursal;

    private String nombreSucursal;

    private Estado estado;

    private BigDecimal montoInicial;

    private Long idEmpleadoApertura;

    private String empleadoApertura;

    private LocalDateTime fechaApertura;

    private String observacionApertura;

    // ─── Totales en vivo / arqueo ──────────────────────────────
    private BigDecimal totalIngresos;

    private BigDecimal totalEgresos;

    /** montoInicial + totalIngresos - totalEgresos */
    private BigDecimal saldoEsperado;

    private Integer cantidadMovimientos;

    // ─── Datos de cierre (nulos mientras esté ABIERTA) ─────────
    private BigDecimal montoFinal;

    private BigDecimal diferencia;

    private Long idEmpleadoCierre;

    private String empleadoCierre;

    private LocalDateTime fechaCierre;

    private String observacionCierre;

    private List<MovimientoCajaResponse> movimientos;
}
