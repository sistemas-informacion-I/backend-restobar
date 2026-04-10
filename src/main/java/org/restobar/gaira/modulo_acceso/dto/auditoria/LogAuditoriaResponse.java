package org.restobar.gaira.acceso.dto.auditoria;

import java.time.LocalDateTime;
import java.util.Map;

public record LogAuditoriaResponse(
        Long idLog,
        String tabla,
        String operacion,
        String idRegistro,
        Map<String, Object> datosAnteriores,
        Map<String, Object> datosNuevos,
        Long idUsuario,
        Long idSucursal,
        String ipOrigen,
        String userAgent,
        LocalDateTime fechaOperacion
) {
}
