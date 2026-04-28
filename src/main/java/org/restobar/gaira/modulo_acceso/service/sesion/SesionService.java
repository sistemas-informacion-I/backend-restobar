package org.restobar.gaira.modulo_acceso.service.sesion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.restobar.gaira.modulo_acceso.dto.sesion.SesionResponse;
import org.restobar.gaira.modulo_acceso.entity.Sesion;
import org.restobar.gaira.modulo_acceso.mapper.sesion.SesionMapper;
import org.restobar.gaira.modulo_acceso.repository.login.SesionRepository;
import org.restobar.gaira.security.audit.util.AuditableService;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class SesionService implements AuditableService<Long, Object> {

    private final SesionRepository sesionRepository;
    private final SesionMapper sesionMapper;

    @Override
    public Object getEntity(Long id) {
        return sesionRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof Sesion s) {
            return sesionMapper.toAuditMap(s);
        } else if (entity instanceof SesionResponse sr) {
            return sesionMapper.toAuditMap(sr);
        }
        return Map.of();
    }

    @Transactional(readOnly = true)
    public List<SesionResponse> findByUsuario(Long idUsuario) {
        return sesionRepository.findByUsuario_IdUsuarioOrderByFechaInicioDesc(idUsuario).stream()
                .map(sesionMapper::toResponse)
                .toList();
    }

    /**
     * Cierra/revoca una sesión estableciendo fecha_cierre.
     * Una sesión cerrada (fecha_cierre != NULL) ya no puede ser usada.
     */
    @Transactional
    @org.restobar.gaira.security.audit.annotation.Auditable(tabla = "sesion", operacion = "UPDATE", idParamName = "idSesion")
    public void revoke(Long idSesion) {
        Sesion sesion = sesionRepository.findById(idSesion)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Sesión no encontrada"));
        sesion.setFechaCierre(LocalDateTime.now());
        sesionRepository.save(sesion);
    }
}
