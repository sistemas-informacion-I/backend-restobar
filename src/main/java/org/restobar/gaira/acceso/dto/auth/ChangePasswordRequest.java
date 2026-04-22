package org.restobar.gaira.acceso.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para cambiar la contraseña del usuario autenticado.
 */
public record ChangePasswordRequest(
        @NotBlank(message = "Contraseña actual es requerida")
        String currentPassword,

        @NotBlank(message = "Nueva contraseña es requerida")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String newPassword
) {
}
