package org.restobar.gaira.modulo_acceso.mapper.auditoria;

import org.restobar.gaira.modulo_acceso.dto.auditoria.LogAuditoriaResponse;
import org.restobar.gaira.modulo_acceso.entity.LogAuditoria;
import org.springframework.stereotype.Component;

@Component
public class LogAuditoriaMapper {

    public LogAuditoriaResponse toResponse(LogAuditoria log) {
        if (log == null) return null;

        return LogAuditoriaResponse.builder()
                .idLog(log.getIdLog())
                .tabla(log.getTabla())
                .operacion(log.getOperacion())
                .idRegistro(log.getIdRegistro())
                .datosAnteriores(log.getDatosAnteriores())
                .datosNuevos(log.getDatosNuevos())
                .idUsuario(log.getUsuario() != null ? log.getUsuario().getIdUsuario() : null)
                .username(log.getUsuario() != null ? log.getUsuario().getUsername() : "SISTEMA")
                .idSucursal(log.getIdSucursal())
                .ipOrigen(log.getIpOrigen())
                .userAgent(log.getUserAgent())
                .fechaOperacion(log.getFechaOperacion())
                .build();
    }
}
