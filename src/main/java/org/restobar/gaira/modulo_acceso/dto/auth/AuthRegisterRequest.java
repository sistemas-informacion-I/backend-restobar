package org.restobar.gaira.modulo_acceso.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AuthRegisterRequest(
        @NotBlank(message = "CI es requerido")
        String ci,
        @NotBlank(message = "Nombre es requerido")
        String nombre,
        @NotBlank(message = "Apellido es requerido")
        String apellido,
        String telefono,
        @Pattern(regexp = "[MFO]", message = "Sexo debe ser M, F u O")
        String sexo,
        @Email(message = "Correo inválido")
        @NotBlank(message = "Correo es requerido")
        String correo,
        String direccion,
        @NotBlank(message = "Username es requerido")
        String username,
        @NotBlank(message = "Password es requerido")
        String password,
        String rol
) {
}
