package org.restobar.gaira.modulo_electronico.mapper.reserva;

import org.restobar.gaira.modulo_electronico.dto.reserva.MesaReservaResponse;
import org.restobar.gaira.modulo_electronico.dto.reserva.ReservaResponse;
import org.restobar.gaira.modulo_electronico.entity.Reserva;
import org.restobar.gaira.modulo_electronico.entity.ReservaMesa;
import org.restobar.gaira.modulo_operaciones.entity.Mesa;
import org.springframework.stereotype.Component;

@Component
public class ReservaMapper {

    public ReservaResponse toResponse(Reserva reserva) {
        if (reserva == null) return null;

        return new ReservaResponse(
                reserva.getIdReserva(),
                reserva.getIdSucursal(),
                reserva.getIdCliente(),
                reserva.getIdEmpleadoConfirmacion(),
                reserva.getIdEmpleadoCheckIn(),
                reserva.getIdComanda(),
                reserva.getClienteNombre(),
                reserva.getClienteTelefono(),
                reserva.getClienteCorreo(),
                reserva.getFechaReserva(),
                reserva.getHoraInicio(),
                reserva.getHoraFin(),
                reserva.getCantidadPersonas(),
                reserva.getEstado(),
                reserva.getFechaCreacion(),
                reserva.getFechaConfirmacion(),
                reserva.getFechaCheckIn(),
                reserva.getFechaCancelacion(),
                reserva.getMotivoCancelacion(),
                reserva.getObservaciones(),
                reserva.getMesas().stream()
                        .map(ReservaMesa::getMesa)
                        .map(this::toMesaResponse)
                        .toList());
    }

    public MesaReservaResponse toMesaResponse(Mesa mesa) {
        if (mesa == null) return null;

        return new MesaReservaResponse(
                mesa.getIdMesa(),
                mesa.getSector() != null ? mesa.getSector().getIdSector() : null,
                mesa.getNumeroMesa(),
                mesa.getCapacidadPersonas(),
                mesa.getDisponibilidad());
    }
}
