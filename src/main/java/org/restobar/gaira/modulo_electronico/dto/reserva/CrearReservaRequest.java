package org.restobar.gaira.modulo_electronico.dto.reserva;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CrearReservaRequest(
        @NotNull Long idSucursal,
        @NotNull LocalDate fechaReserva,
        @NotNull LocalTime horaInicio,
        LocalTime horaFin,
        @NotNull @Positive Integer cantidadPersonas,
        @NotEmpty List<Long> idsMesa,
        String clienteNombre,
        String clienteTelefono,
        @Email String clienteCorreo,
        String observaciones) {
}
