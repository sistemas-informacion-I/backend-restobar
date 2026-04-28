package org.restobar.gaira.modulo_acceso.dto.perfil;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PerfilPersonalUpdate(
    @NotBlank(message = "Nombre no puede estar vacío") String nombre,
    @NotBlank(message = "Apellido no puede estar vacío") String apellido,
    String telefono,
    @NotBlank(message = "Sexo no puede ser nulo") 
    @Pattern(regexp = "[MFO]", message = "Sexo debe ser M, F u O") String sexo,
    @Email(message = "Correo debe ser válido") String correo,
    String direccion
) {}
