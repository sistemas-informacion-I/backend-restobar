package org.restobar.gaira.modulo_electronico.dto.reserva;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record ReservaResponse(
        Long idReserva,
        Long idSucursal,
        Long idCliente,
        Long idEmpleadoConfirmacion,
        Long idEmpleadoCheckIn,
        Long idComanda,
        String clienteNombre,
        String clienteTelefono,
        String clienteCorreo,
        LocalDate fechaReserva,
        LocalTime horaInicio,
        LocalTime horaFin,
        Integer cantidadPersonas,
        String estado,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaConfirmacion,
        LocalDateTime fechaCheckIn,
        LocalDateTime fechaCancelacion,
        String motivoCancelacion,
        String observaciones,
        List<MesaReservaResponse> mesas) {
}
