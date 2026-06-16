package org.restobar.gaira.modulo_electronico.dto.reserva;

public record DisponibilidadMesaResponse(
        Long idMesa,
        Long idSector,
        String numeroMesa,
        Integer capacidadPersonas,
        String estadoPlano,
        Boolean disponible,
        String motivo) {
}
