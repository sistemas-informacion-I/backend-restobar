package org.restobar.gaira.modulo_acceso.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AuthLogin(
                @NotBlank(message = "Username es requerido") String username,
                @NotBlank(message = "Password es requerido") String password) {
}
