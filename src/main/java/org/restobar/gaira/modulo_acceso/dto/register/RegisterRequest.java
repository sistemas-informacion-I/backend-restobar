package org.restobar.gaira.modulo_acceso.dto.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RegisterRequest(
                @NotBlank(message = "CI no puede estar vacío") String ci,
                @NotBlank(message = "Nombre no puede estar vacío") String nombre,
                @NotBlank(message = "Apellido no puede estar vacío") String apellido,
                @NotBlank(message = "Username no puede estar vacío") String username,
                @NotBlank(message = "Password no puede estar vacío")
                @jakarta.validation.constraints.Pattern(
                    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
                    message = "La contraseña debe tener al menos 8 caracteres e incluir mayúsculas, minúsculas y números"
                )
                String password,
                String telefono,
                @NotNull(message = "Sexo no puede ser nulo") String sexo,
                @Email(message = "Correo debe ser válido") String correo,
                String direccion,
                String rol) {
}
