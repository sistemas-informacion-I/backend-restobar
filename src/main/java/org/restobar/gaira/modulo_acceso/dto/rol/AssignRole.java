package org.restobar.gaira.modulo_acceso.dto.rol;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AssignRole(
                @NotNull(message = "idRol es requerido") Long idRol) {
}
