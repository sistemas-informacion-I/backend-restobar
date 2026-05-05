package org.restobar.gaira.modulo_acceso.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record UsuarioCreate(
                @NotBlank(message = "CI no puede estar vacío") String ci,
                @NotBlank(message = "Nombre no puede estar vacío") String nombre,
                @NotBlank(message = "Apellido no puede estar vacío") String apellido,
                String username,
                String password,
                String telefono,
                @NotNull(message = "Sexo no puede ser nulo") String sexo,
                @Email(message = "Correo debe ser válido") String correo,
                String direccion,
                @NotBlank(message = "Tipo de usuario no puede estar vacío") String tipoUsuario,
                Boolean activo,
                String estadoAcceso,
                List<Long> roles) {
}
