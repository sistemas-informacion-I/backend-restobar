package org.restobar.gaira.modulo_acceso.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequest(
        @NotBlank(message = "Username es requerido")
        String username,
        @NotBlank(message = "Password es requerido")
        String password
) {
}
