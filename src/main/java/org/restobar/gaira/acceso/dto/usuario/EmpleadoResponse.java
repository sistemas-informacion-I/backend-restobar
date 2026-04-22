package org.restobar.gaira.acceso.dto.usuario;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmpleadoResponse(
        Long idEmpleado,
        Long idUsuario,
        String ci,
        String nombre,
        String apellido,
        String username,
        String telefono,
        String sexo,
        String correo,
        String direccion,
        Boolean activo,
        String estadoAcceso,
        String codigoEmpleado,
        BigDecimal salario,
        String turno,
        LocalDate fechaContratacion,
        LocalDate fechaFinalizacion
) {
}
