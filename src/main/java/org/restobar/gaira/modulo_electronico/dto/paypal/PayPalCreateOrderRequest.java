package org.restobar.gaira.modulo_electronico.dto.paypal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PayPalCreateOrderRequest(
        @NotNull Long idNotaVenta,
        @NotNull Long idMetodoPago,
        @NotNull @DecimalMin(value = "0.01", inclusive = true) BigDecimal monto,
        String moneda,
        @NotBlank String referencia,
        List<ItemPedido> items,
        String returnUrl,
        String cancelUrl,
        Map<String, Object> datosAdicionales,
        String customerName,
        String customerEmail,
        String customerPhone,
        String nitCliente,
        String shippingAddress,
        String shippingCity,
        String shippingState,
        String shippingZip,
        String shippingNotes) {

    public record ItemPedido(
            String name,
            Integer quantity,
            BigDecimal unitAmount,
            String sku) {
    }
}