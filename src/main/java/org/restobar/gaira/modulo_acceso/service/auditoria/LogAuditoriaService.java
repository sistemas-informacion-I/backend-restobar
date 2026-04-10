package org.restobar.gaira.modulo_acceso.service.auditoria;

import java.util.List;
import java.util.Map;

import org.restobar.gaira.modulo_acceso.dto.auditoria.LogAuditoriaResponse;
import org.restobar.gaira.modulo_acceso.entity.LogAuditoria;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_acceso.mapper.AutenticacionMapper;
import org.restobar.gaira.modulo_acceso.repository.LogAuditoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogAuditoriaService {

    private final LogAuditoriaRepository logAuditoriaRepository;

    @Transactional(readOnly = true)
    public List<LogAuditoriaResponse> latest() {
        return logAuditoriaRepository.findTop100ByOrderByFechaOperacionDesc().stream()
                .map(AutenticacionMapper::toLogAuditoriaResponse)
                .toList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAcceso(Usuario usuario, String tabla, String operacion,
            HttpServletRequest request, String descripcion) {
        try {
            LogAuditoria log = LogAuditoria.builder()
                    .tabla(tabla)
                    .operacion(operacion)
                    .idRegistro(usuario != null ? String.valueOf(usuario.getIdUsuario()) : null)
                    .usuario(usuario)
                    .ipOrigen(extractIp(request))
                    .userAgent(request.getHeader("User-Agent"))
                    .datosNuevos(Map.of("resultado", descripcion))
                    .build();
            logAuditoriaRepository.save(log);
        } catch (Exception e) {
            // No romper el flujo principal si el log falla
        }
    }

    private String extractIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
