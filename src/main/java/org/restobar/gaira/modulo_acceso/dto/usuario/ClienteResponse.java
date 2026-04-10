package org.restobar.gaira.acceso.dto.usuario;

import java.time.LocalDate;

public record ClienteResponse(
        Long idCliente,
        Long idUsuario,
        String nit,
        String razonSocial,
        LocalDate fechaNacimiento,
        Integer puntosFidelidad,
        String nivelCliente,
        String observaciones
) {
}
