package org.restobar.gaira.modulo_electronico.dto.paypal;

import org.restobar.gaira.modulo_electronico.dto.pago.TransaccionOnlineResponse;

public record PayPalCaptureResponse(
        String paypalOrderId,
        String paypalCaptureId,
        String paypalStatus,
        TransaccionOnlineResponse transaccion) {
}