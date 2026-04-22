package org.restobar.gaira.modulo_acceso.mapper.usuario;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.restobar.gaira.modulo_acceso.dto.usuario.UsuarioCreate;
import org.restobar.gaira.modulo_acceso.dto.usuario.UsuarioResponse;
import org.restobar.gaira.modulo_acceso.dto.usuario.UsuarioUpdate;
import org.restobar.gaira.modulo_acceso.entity.RolUsuario;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_acceso.mapper.rol.RolMapper;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UsuarioMapper {

    private final RolMapper rolMapper;

    public UsuarioResponse toResponse(Usuario usuario) {
        if (usuario == null)
            return null;

        return UsuarioResponse.builder()
                .idUsuario(usuario.getIdUsuario())
                .ci(usuario.getCi())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .username(usuario.getUsername())
                .telefono(usuario.getTelefono())
                .sexo(usuario.getSexo())
                .correo(usuario.getCorreo())
                .direccion(usuario.getDireccion())
                .intentosFallidos(usuario.getIntentosFallidos())
                .estadoAcceso(usuario.getEstadoAcceso())
                .fechaRegistro(usuario.getFechaRegistro())
                .activo(usuario.getActivo())
                .roles(usuario.getRolesUsuario().stream()
                        .filter(ru -> Boolean.TRUE.equals(ru.getActivo()))
                        .map(ru -> rolMapper.toResponse(ru.getRol()))
                        .collect(Collectors.toList()))
                .build();
    }

    public Usuario toEntity(UsuarioCreate create, String passwordHash) {
        if (create == null)
            return null;

        return Usuario.builder()
                .ci(create.ci())
                .nombre(create.nombre())
                .apellido(create.apellido())
                .username(create.username())
                .passwordHash(passwordHash)
                .telefono(create.telefono())
                .sexo(create.sexo())
                .correo(create.correo())
                .direccion(create.direccion())
                .activo(create.activo() == null || create.activo())
                .estadoAcceso(create.estadoAcceso() != null ? create.estadoAcceso() : "HABILITADO")
                .intentosFallidos(0)
                .build();
    }

    public void updateEntityFromDto(Usuario usuario, UsuarioUpdate update) {
        if (update == null || usuario == null)
            return;

        usuario.setCi(update.ci());
        usuario.setNombre(update.nombre());
        usuario.setApellido(update.apellido());
        usuario.setTelefono(update.telefono());
        usuario.setSexo(update.sexo());
        usuario.setCorreo(update.correo());
        usuario.setDireccion(update.direccion());
        if (update.activo() != null) {
            usuario.setActivo(update.activo());
        }
        if (update.estadoAcceso() != null) {
            usuario.setEstadoAcceso(update.estadoAcceso());
        }
    }

    public Map<String, Object> toAuditMap(Usuario usuario) {
        if (usuario == null)
            return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idUsuario", usuario.getIdUsuario());
        map.put("ci", usuario.getCi());
        map.put("username", usuario.getUsername());
        map.put("nombre_completo", usuario.getNombre() + " " + usuario.getApellido());
        map.put("correo", usuario.getCorreo());
        map.put("estado", usuario.getEstadoAcceso());
        map.put("activo", usuario.getActivo());
        return map;
    }

    public Map<String, Object> toAuditMapLite(Usuario usuario, List<RolUsuario> roles) {
        if (usuario == null)
            return Map.of();
        Map<String, Object> map = new HashMap<>(toAuditMap(usuario));
        if (roles != null) {
            map.put("roles", roles.stream()
                    .filter(ru -> Boolean.TRUE.equals(ru.getActivo()))
                    .map(ru -> ru.getRol().getNombre())
                    .collect(Collectors.toList()));
        }
        return map;
    }

    public Map<String, Object> toAuditMap(UsuarioResponse response) {
        if (response == null)
            return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idUsuario", response.idUsuario());
        map.put("ci", response.ci());
        map.put("username", response.username());
        map.put("nombre_completo", (response.nombre() != null ? response.nombre() : "") +
                " " + (response.apellido() != null ? response.apellido() : ""));
        map.put("correo", response.correo());
        map.put("estado", response.estadoAcceso());
        map.put("activo", response.activo());
        map.put("roles", response.roles() != null
                ? response.roles().stream().map(r -> r.nombre()).collect(Collectors.toList())
                : java.util.List.of());
        return map;
    }
}
