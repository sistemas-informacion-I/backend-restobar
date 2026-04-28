package org.restobar.gaira.modulo_acceso.dto.login;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RefreshTokenRequest(
                @NotBlank(message = "Refresh token es requerido") String refreshToken) {
}
