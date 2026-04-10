package org.restobar.gaira.modulo_acceso.dto.rol;

import jakarta.validation.constraints.NotNull;

public record AssignRoleRequest(
        @NotNull(message = "idRol es requerido")
        Long idRol
) {
}
