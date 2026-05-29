package org.restobar.gaira.modulo_electronico.dto.pago;

import java.math.BigDecimal;

public record MetodoPagoResponse(
        Long idMetodoPago,
        String nombre,
        String descripcion,
        BigDecimal comisionPorcentaje,
        BigDecimal comisionFija,
        Boolean activo) {
}