package org.restobar.gaira.modulo_acceso.dto.usuario;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record EmpleadoRequest(
                @NotBlank(message = "CI es obligatorio") String ci,
                @NotBlank(message = "Nombre es obligatorio") String nombre,
                @NotBlank(message = "Apellido es obligatorio") String apellido,
                String username,
                String password,
                String telefono,
                @NotNull(message = "Sexo es obligatorio") String sexo,
                @Email(message = "Email inválido") String correo,
                String direccion,
                Boolean activo,
                String codigoEmpleado,
                @NotNull(message = "Salario es obligatorio") BigDecimal salario,
                String turno,
                LocalDate fechaContratacion,
                LocalDate fechaFinalizacion,
                List<Long> roles,
                Long idSucursal) {
}
