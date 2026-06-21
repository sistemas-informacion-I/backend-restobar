package org.restobar.gaira.modulo_comercial.dto.caja;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resumen del arqueo de una caja: totales calculados en el momento de la
 * consulta. El frontend lo usa para mostrar el saldo esperado antes del cierre.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArqueoResponse {

    private Long idCaja;

    private BigDecimal montoInicial;

    private BigDecimal totalIngresos;

    private BigDecimal totalEgresos;

    private BigDecimal saldoEsperado;

    private Integer cantidadMovimientos;
}
