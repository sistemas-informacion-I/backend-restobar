package org.restobar.gaira.acceso.dto.usuario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record EmpleadoRequest(
        @NotBlank(message = "CI es requerido")
        String ci,

        @NotBlank(message = "Nombre es requerido")
        String nombre,

        @NotBlank(message = "Apellido es requerido")
        String apellido,

        String username,
        String password,
        String telefono,

        @NotBlank(message = "Sexo es requerido")
        @Pattern(regexp = "[MFO]", message = "Sexo debe ser M, F u O")
        String sexo,

        @Email(message = "Correo inválido")
        String correo,

        String direccion,
        Boolean activo,
        List<Long> roles,
        String codigoEmpleado,

        @NotNull(message = "Salario es requerido")
        @DecimalMin(value = "0.01", message = "Salario debe ser mayor a 0")
        BigDecimal salario,

        @Pattern(regexp = "^(AM|PM)$", message = "Turno debe ser AM o PM")
        String turno,

        LocalDate fechaContratacion,
        LocalDate fechaFinalizacion
) {
}
