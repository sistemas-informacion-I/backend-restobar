package org.restobar.gaira.modulo_acceso.dto.auditoria;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogAuditoriaResponse {
    private Long idLog;
    private String tabla;
    private String operacion;
    private String idRegistro;
    private Map<String, Object> datosAnteriores;
    private Map<String, Object> datosNuevos;
    private Long idUsuario;
    private String username;
    private Long idSucursal;
    private String ipOrigen;
    private String userAgent;
    private LocalDateTime fechaOperacion;
}
