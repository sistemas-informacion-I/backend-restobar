package org.restobar.gaira.modulo_acceso.dto.login;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequest(
                @NotBlank(message = "Username es requerido") String username,
                @NotBlank(message = "Password es requerido") String password) {
}
