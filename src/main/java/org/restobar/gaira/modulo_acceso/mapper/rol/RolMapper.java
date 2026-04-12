package org.restobar.gaira.modulo_acceso.mapper.rol;

import org.restobar.gaira.modulo_acceso.dto.rol.RolCreate;
import org.restobar.gaira.modulo_acceso.dto.rol.RolResponse;
import org.restobar.gaira.modulo_acceso.dto.rol.RolUpdate;
import org.restobar.gaira.modulo_acceso.entity.Rol;
import org.restobar.gaira.modulo_acceso.entity.RolPermiso;
import org.restobar.gaira.modulo_acceso.mapper.permiso.PermissionMapper;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RolMapper {

    private final PermissionMapper permissionMapper;

    public RolResponse toResponse(Rol rol) {
        if (rol == null) return null;

        return RolResponse.builder()
                .idRol(rol.getIdRol())
                .nombre(rol.getNombre())
                .descripcion(rol.getDescripcion())
                .nivelAcceso(rol.getNivelAcceso())
                .fechaCreacion(rol.getFechaCreacion())
                .activo(rol.getActivo())
                .permisos(rol.getRolesPermiso().stream()
                        .filter(rp -> Boolean.TRUE.equals(rp.getActivo()))
                        .map(rp -> permissionMapper.toResponse(rp.getPermiso()))
                        .collect(Collectors.toList()))
                .build();
    }

    public Rol toEntity(RolCreate create) {
        if (create == null) return null;

        return Rol.builder()
                .nombre(create.nombre())
                .descripcion(create.descripcion())
                .nivelAcceso(create.nivelAcceso() != null ? create.nivelAcceso() : 1)
                .activo(create.activo() == null || create.activo())
                .build();
    }

    public void updateEntityFromDto(Rol rol, RolUpdate update) {
        if (update == null || rol == null) return;

        rol.setNombre(update.nombre());
        rol.setDescripcion(update.descripcion());
        if (update.nivelAcceso() != null) {
            rol.setNivelAcceso(update.nivelAcceso());
        }
        if (update.activo() != null) {
            rol.setActivo(update.activo());
        }
    }

    public Map<String, Object> toAuditMap(Rol rol) {
        if (rol == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idRol", rol.getIdRol());
        map.put("nombre", rol.getNombre());
        map.put("nivelAcceso", rol.getNivelAcceso());
        map.put("activo", rol.getActivo());
        map.put("permisos", rol.getRolesPermiso().stream()
                .filter(rp -> Boolean.TRUE.equals(rp.getActivo()))
                .map(rp -> rp.getPermiso().getNombre())
                .collect(Collectors.toList()));
        return map;
    }

    public Map<String, Object> toAuditMap(RolResponse rol) {
        if (rol == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idRol", rol.idRol());
        map.put("nombre", rol.nombre());
        map.put("nivelAcceso", rol.nivelAcceso());
        map.put("activo", rol.activo());
        map.put("permisos", rol.permisos() != null ? rol.permisos().stream().map(p -> p.nombre()).collect(Collectors.toList()) : java.util.List.of());
        return map;
    }
}
