package org.restobar.gaira.modulo_acceso.dto.permiso;

import jakarta.validation.constraints.NotNull;

public record AssignPermissionRequest(
        @NotNull(message = "idPermiso es requerido")
        Long idPermiso
) {
}
