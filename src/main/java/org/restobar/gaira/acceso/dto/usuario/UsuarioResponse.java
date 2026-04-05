package org.restobar.gaira.acceso.dto.usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long idUsuario,
        String ci,
        String nombre,
        String apellido,
        String username,
        String telefono,
        String sexo,
        String correo,
        String direccion,
        Integer intentosFallidos,
        String estadoAcceso,
        LocalDateTime fechaRegistro,
        Boolean activo,
        java.util.List<org.restobar.gaira.acceso.dto.rol.RolResponse> roles
) {
}
