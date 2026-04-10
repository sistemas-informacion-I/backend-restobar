package org.restobar.gaira.modulo_acceso.dto.rol;

import java.time.LocalDateTime;

public record RolResponse(
        Long idRol,
        String nombre,
        String descripcion,
        Integer nivelAcceso,
        Boolean activo,
        LocalDateTime fechaCreacion,
        java.util.List<org.restobar.gaira.modulo_acceso.dto.permiso.PermisoResponse> permisos
) {
}
