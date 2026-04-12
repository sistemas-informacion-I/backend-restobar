package org.restobar.gaira.modulo_acceso.dto.permiso;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record PermisoResponse(
                Long idPermiso,
                String nombre,
                String modulo,
                String accion,
                String descripcion,
                Boolean activo,
                LocalDateTime fechaCreacion) {
}
