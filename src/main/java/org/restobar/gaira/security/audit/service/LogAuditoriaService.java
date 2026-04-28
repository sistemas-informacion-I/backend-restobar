package org.restobar.gaira.security.audit.service;

import java.util.Map;

import org.restobar.gaira.modulo_acceso.dto.auditoria.AuditoriaFilter;
import org.restobar.gaira.modulo_acceso.dto.auditoria.LogAuditoriaResponse;
import org.restobar.gaira.modulo_acceso.entity.LogAuditoria;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_acceso.mapper.auditoria.LogAuditoriaMapper;
import org.restobar.gaira.modulo_acceso.repository.LogAuditoriaRepository;
import org.restobar.gaira.modulo_acceso.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogAuditoriaService {

    private final LogAuditoriaRepository logAuditoriaRepository;
    private final LogAuditoriaMapper logAuditoriaMapper;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public Page<LogAuditoriaResponse> findAll(AuditoriaFilter filter) {
        var spec = LogAuditoriaRepository.buildFrom(
                filter.tabla(), filter.operacion(), filter.idUsuario(),
                filter.desde(), filter.hasta());

        PageRequest pageable = PageRequest.of(
                filter.page(), filter.size(),
                Sort.by(Sort.Direction.DESC, "fechaOperacion"));

        return logAuditoriaRepository.findAll(spec, pageable)
                .map(logAuditoriaMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public LogAuditoriaResponse findById(Long id) {
        return logAuditoriaRepository.findById(id)
                .map(logAuditoriaMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Registro de auditoría no encontrado: " + id));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAcceso(Usuario usuario, String tabla, String operacion,
            HttpServletRequest request, String descripcion) {
        try {
            Usuario usuarioReferencia = resolveUsuario(usuario);
            LogAuditoria auditLog = LogAuditoria.builder()
                    .tabla(tabla)
                    .operacion(operacion)
                    .idRegistro(usuarioReferencia != null ? String.valueOf(usuarioReferencia.getIdUsuario()) : null)
                    .usuario(usuarioReferencia)
                    .ipOrigen(request != null ? extractIp(request) : null)
                    .userAgent(request != null ? request.getHeader("User-Agent") : null)
                    .datosNuevos(Map.of("resultado", descripcion))
                    .build();
            logAuditoriaRepository.save(auditLog);
        } catch (Exception e) {
            log.warn("No se pudo registrar log de acceso para tabla={} operacion={}", tabla, operacion, e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logOperacion(Usuario usuario, String tabla, String operacion,
            String idRegistro, Map<String, Object> datosAnteriores,
            Map<String, Object> datosNuevos, HttpServletRequest request) {
        try {
            Usuario usuarioReferencia = resolveUsuario(usuario);
            LogAuditoria auditLog = LogAuditoria.builder()
                    .tabla(tabla)
                    .operacion(operacion)
                    .idRegistro(idRegistro)
                    .datosAnteriores(datosAnteriores)
                    .datosNuevos(datosNuevos)
                    .usuario(usuarioReferencia)
                    .ipOrigen(request != null ? extractIp(request) : null)
                    .userAgent(request != null ? request.getHeader("User-Agent") : null)
                    .build();
            logAuditoriaRepository.save(auditLog);
        } catch (Exception e) {
            log.warn("No se pudo registrar log de operación para tabla={} operacion={} id={}",
                    tabla, operacion, idRegistro, e);
        }
    }

    private String extractIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private Usuario resolveUsuario(Usuario usuario) {
        if (usuario == null || usuario.getIdUsuario() == null) {
            return null;
        }
        return usuarioRepository.findById(usuario.getIdUsuario()).orElse(null);
    }
}