package org.restobar.gaira.acceso.dto.permiso;

import java.time.LocalDateTime;

public record PermisoResponse(
        Long idPermiso,
        String nombre,
        String modulo,
        String accion,
        String descripcion,
        Boolean activo,
        LocalDateTime fechaCreacion
) {
}
