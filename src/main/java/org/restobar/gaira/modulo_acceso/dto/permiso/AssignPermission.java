package org.restobar.gaira.modulo_acceso.dto.permiso;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AssignPermission(
                @NotNull(message = "idPermiso es requerido") Long idPermiso) {
}
