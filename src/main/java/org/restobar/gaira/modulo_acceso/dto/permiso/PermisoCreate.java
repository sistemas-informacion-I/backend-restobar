package org.restobar.gaira.modulo_acceso.dto.permiso;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record PermisoCreate(
                @NotBlank(message = "Nombre es requerido") String nombre,
                @NotBlank(message = "Modulo es requerido") String modulo,
                @NotBlank(message = "Accion es requerida") String accion,
                String descripcion,
                Boolean activo) {
}
