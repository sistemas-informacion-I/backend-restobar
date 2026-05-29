package org.restobar.gaira.modulo_electronico.dto.paypal;

import java.math.BigDecimal;

public record PayPalCreateOrderResponse(
        Long idTransaccion,
        String paypalOrderId,
        String approvalUrl,
        String status,
        String mode,
        String currency,
        BigDecimal amount,
        String invoiceNumber) {
}