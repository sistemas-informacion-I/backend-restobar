package org.restobar.gaira.acceso.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

/**
 * DTO para crear o actualizar un usuario.
 * En creación, username y password son requeridos.
 * En actualización, si username/password son null se ignoran.
 */
public record UsuarioRequest(
        String ci,
        String nombre,
        String apellido,
        String username,
        String password,
        String telefono,
        @Pattern(regexp = "[MFO]", message = "Sexo debe ser M, F u O")
        String sexo,
        @Email(message = "Correo inválido")
        String correo,
        String direccion,
        Boolean activo,
        /**
         * Estado de la cuenta: HABILITADO, SUSPENDIDO, BLOQUEADO.
         * Solo puede ser establecido por administradores.
         */
        String estadoAcceso,
        /**
         * IDs de roles a asignar al usuario.
         * Si es null, no se modifica la asignación actual.
         */
        java.util.List<Long> roles
) {
}
