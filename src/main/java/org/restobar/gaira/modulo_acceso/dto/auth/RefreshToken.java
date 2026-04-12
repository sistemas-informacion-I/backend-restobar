package org.restobar.gaira.modulo_acceso.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RefreshToken(
                @NotBlank(message = "Refresh token es requerido") String refreshToken) {
}
