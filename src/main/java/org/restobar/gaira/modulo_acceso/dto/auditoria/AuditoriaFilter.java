package org.restobar.gaira.modulo_acceso.dto.auditoria;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record AuditoriaFilter(
        String tabla,
        String operacion,
        Long idUsuario,
        Long idSucursal,
        LocalDateTime desde,
        LocalDateTime hasta,
        int page,
        int size) {
    public AuditoriaFilter {
        if (size <= 0 || size > 100)
            size = 20;
        if (page < 0)
            page = 0;
    }
}
