package org.restobar.gaira.acceso.dto.usuario;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmpleadoResponse(
        Long idEmpleado,
        Long idUsuario,
        String codigoEmpleado,
        BigDecimal salario,
        LocalDate fechaContratacion,
        LocalDate fechaFinalizacion
) {
}
