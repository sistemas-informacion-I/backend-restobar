package org.restobar.gaira.modulo_acceso.mapper.perfil;

import lombok.RequiredArgsConstructor;
import org.restobar.gaira.modulo_acceso.dto.perfil.PerfilPersonalResponse;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_acceso.mapper.usuario.ClienteMapper;
import org.restobar.gaira.modulo_acceso.mapper.usuario.EmpleadoMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PerfilPersonalMapper {

    private final ClienteMapper clienteMapper;
    private final EmpleadoMapper empleadoMapper;

    public PerfilPersonalResponse toResponse(Usuario usuario) {
        if (usuario == null) return null;

        return new PerfilPersonalResponse(
            usuario.getIdUsuario(),
            usuario.getCi(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getUsername(),
            usuario.getTelefono(),
            usuario.getSexo(),
            usuario.getCorreo(),
            usuario.getDireccion(),
            usuario.getFechaRegistro(),
            usuario.getTipoUsuario(),
            usuario.getCliente() != null ? clienteMapper.toResponse(usuario.getCliente()) : null,
            usuario.getEmpleado() != null ? empleadoMapper.toResponse(usuario.getEmpleado()) : null
        );
    }

    public Map<String, Object> toAuditMap(Usuario usuario) {
        if (usuario == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idUsuario", usuario.getIdUsuario());
        map.put("ci", usuario.getCi());
        map.put("username", usuario.getUsername());
        map.put("nombre", usuario.getNombre());
        map.put("apellido", usuario.getApellido());
        map.put("telefono", usuario.getTelefono());
        map.put("sexo", usuario.getSexo());
        map.put("correo", usuario.getCorreo());
        map.put("direccion", usuario.getDireccion());
        map.put("activo", usuario.getActivo());
        return map;
    }

    public Map<String, Object> toAuditMap(PerfilPersonalResponse dto) {
        if (dto == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idUsuario", dto.idUsuario());
        map.put("ci", dto.ci());
        map.put("username", dto.username());
        map.put("nombre", dto.nombre());
        map.put("apellido", dto.apellido());
        map.put("telefono", dto.telefono());
        map.put("sexo", dto.sexo());
        map.put("correo", dto.correo());
        map.put("direccion", dto.direccion());
        return map;
    }
}
