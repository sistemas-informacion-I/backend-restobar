package org.restobar.gaira.modulo_electronico.dto.reserva;

public record MesaReservaResponse(
        Long idMesa,
        Long idSector,
        String numeroMesa,
        Integer capacidadPersonas,
        String disponibilidad) {
}
