package org.restobar.gaira.acceso.dto.rol;

import jakarta.validation.constraints.NotNull;

public record AssignRoleRequest(
        @NotNull(message = "idRol es requerido")
        Long idRol
) {
}
