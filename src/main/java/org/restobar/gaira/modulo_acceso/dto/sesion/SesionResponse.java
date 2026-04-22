package org.restobar.gaira.modulo_acceso.dto.sesion;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record SesionResponse(
                Long idSesion,
                Long idUsuario,
                LocalDateTime fechaInicio,
                LocalDateTime fechaExpiracion,
                String ipOrigen,
                String userAgent,
                LocalDateTime fechaCierre,
                boolean activa) {
}
