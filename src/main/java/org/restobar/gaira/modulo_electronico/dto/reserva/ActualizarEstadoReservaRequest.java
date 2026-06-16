package org.restobar.gaira.modulo_electronico.dto.reserva;

public record ActualizarEstadoReservaRequest(
        Long idEmpleado,
        String motivo) {
}
