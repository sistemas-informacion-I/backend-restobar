package org.restobar.gaira.modulo_electronico.dto.pago;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;

public record WebhookTransaccionOnlineRequest(
        @NotBlank String estado,
        String codigoAutorizacion,
        String codigoError,
        Map<String, Object> datosAdicionales) {
}