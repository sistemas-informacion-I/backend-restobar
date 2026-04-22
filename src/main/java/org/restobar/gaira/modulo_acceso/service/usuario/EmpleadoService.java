package org.restobar.gaira.modulo_acceso.service.usuario;

import java.util.List;
import java.util.Map;

import org.restobar.gaira.modulo_acceso.dto.usuario.EmpleadoResponse;
import org.restobar.gaira.modulo_acceso.entity.Empleado;
import org.restobar.gaira.modulo_acceso.mapper.usuario.EmpleadoMapper;
import org.restobar.gaira.modulo_acceso.repository.EmpleadoRepository;
import org.restobar.gaira.security.audit.util.AuditableService;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class EmpleadoService implements AuditableService<Long, Object> {

    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoMapper empleadoMapper;

    @Override
    public Object getEntity(Long id) {
        return empleadoRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof Empleado e) {
            return empleadoMapper.toAuditMap(e);
        } else if (entity instanceof EmpleadoResponse er) {
            return empleadoMapper.toAuditMap(er);
        }
        return Map.of();
    }

    @Transactional(readOnly = true)
    public List<EmpleadoResponse> findAll() {
        return empleadoRepository.findAll().stream()
                .map(empleadoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmpleadoResponse findById(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Empleado no encontrado"));
        return empleadoMapper.toResponse(empleado);
    }
}
