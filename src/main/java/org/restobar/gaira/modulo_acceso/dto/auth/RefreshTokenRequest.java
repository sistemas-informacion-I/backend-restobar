package org.restobar.gaira.modulo_acceso.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token es requerido")
        String refreshToken
) {
}
