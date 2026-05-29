package org.restobar.gaira.modulo_electronico.dto.pago;

import java.math.BigDecimal;
import java.util.Map;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IniciarTransaccionOnlineRequest(
        @NotNull Long idNotaVenta,
        @NotNull Long idMetodoPago,
        @NotBlank String numeroTransaccion,
        @NotNull @DecimalMin(value = "0.01", inclusive = true) BigDecimal monto,
        String moneda,
        Map<String, Object> datosAdicionales) {
}