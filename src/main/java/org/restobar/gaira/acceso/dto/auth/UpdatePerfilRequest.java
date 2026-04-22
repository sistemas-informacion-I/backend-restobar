package org.restobar.gaira.acceso.dto.auth;

import jakarta.validation.constraints.Email;

/**
 * DTO para actualizar el perfil del usuario autenticado.
 * Permite actualizar solo ciertos campos sin requerir la información sensible.
 */
public record UpdatePerfilRequest(
        String nombre,
        String apellido,
        String telefono,
        @Email(message = "Correo inválido")
        String correo,
        String direccion
) {
}
