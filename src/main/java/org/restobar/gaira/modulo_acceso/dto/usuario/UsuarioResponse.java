package org.restobar.gaira.modulo_acceso.dto.usuario;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
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
                java.util.List<org.restobar.gaira.modulo_acceso.dto.rol.RolResponse> roles) {
}
