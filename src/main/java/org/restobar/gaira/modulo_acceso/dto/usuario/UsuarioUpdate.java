package org.restobar.gaira.modulo_acceso.dto.usuario;

import jakarta.validation.constraints.Email;
import lombok.Builder;

import java.util.List;

@Builder
public record UsuarioUpdate(
                String ci,
                String nombre,
                String apellido,
                String telefono,
                String sexo,
                @Email(message = "Correo debe ser válido") String correo,
                String direccion,
                String tipoUsuario,
                Boolean activo,
                String estadoAcceso,
                List<Long> roles) {
}
