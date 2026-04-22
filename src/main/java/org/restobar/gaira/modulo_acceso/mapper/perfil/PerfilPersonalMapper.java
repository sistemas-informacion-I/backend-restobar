package org.restobar.gaira.modulo_acceso.mapper.perfil;

import lombok.RequiredArgsConstructor;
import org.restobar.gaira.modulo_acceso.dto.perfil.PerfilPersonalResponse;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_acceso.mapper.usuario.ClienteMapper;
import org.restobar.gaira.modulo_acceso.mapper.usuario.EmpleadoMapper;
import org.restobar.gaira.modulo_acceso.mapper.usuario.ProveedorMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PerfilPersonalMapper {

    private final ClienteMapper clienteMapper;
    private final EmpleadoMapper empleadoMapper;
    private final ProveedorMapper proveedorMapper;

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
            usuario.getCliente() != null ? clienteMapper.toResponse(usuario.getCliente()) : null,
            usuario.getEmpleado() != null ? empleadoMapper.toResponse(usuario.getEmpleado()) : null,
            usuario.getProveedor() != null ? proveedorMapper.toResponse(usuario.getProveedor()) : null
        );
    }
}
