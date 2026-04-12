package org.restobar.gaira.modulo_acceso.mapper.permiso;

import org.restobar.gaira.modulo_acceso.dto.permiso.PermisoCreate;
import org.restobar.gaira.modulo_acceso.dto.permiso.PermisoResponse;
import org.restobar.gaira.modulo_acceso.dto.permiso.PermisoUpdate;
import org.restobar.gaira.modulo_acceso.entity.Permiso;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class PermissionMapper {

    public PermisoResponse toResponse(Permiso p) {
        if (p == null) return null;

        return PermisoResponse.builder()
                .idPermiso(p.getIdPermiso())
                .nombre(p.getNombre())
                .modulo(p.getModulo())
                .accion(p.getAccion())
                .descripcion(p.getDescripcion())
                .fechaCreacion(p.getFechaCreacion())
                .activo(p.getActivo())
                .build();
    }

    public Permiso toEntity(PermisoCreate create) {
        if (create == null) return null;

        return Permiso.builder()
                .nombre(create.nombre())
                .modulo(create.modulo())
                .accion(create.accion())
                .descripcion(create.descripcion())
                .activo(create.activo() == null || create.activo())
                .build();
    }

    public void updateEntityFromDto(Permiso p, PermisoUpdate update) {
        if (p == null || update == null) return;

        p.setNombre(update.nombre());
        p.setModulo(update.modulo());
        p.setAccion(update.accion());
        p.setDescripcion(update.descripcion());
        if (update.activo() != null) {
            p.setActivo(update.activo());
        }
    }

    public Map<String, Object> toAuditMap(Permiso p) {
        if (p == null) return java.util.Map.of();
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("idPermiso", p.getIdPermiso());
        map.put("nombre", p.getNombre());
        map.put("modulo", p.getModulo());
        map.put("accion", p.getAccion());
        map.put("activo", p.getActivo());
        return map;
    }

    public Map<String, Object> toAuditMap(PermisoResponse pr) {
        if (pr == null) return java.util.Map.of();
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("idPermiso", pr.idPermiso());
        map.put("nombre", pr.nombre());
        map.put("modulo", pr.modulo());
        map.put("accion", pr.accion());
        map.put("activo", pr.activo());
        return map;
    }
}
