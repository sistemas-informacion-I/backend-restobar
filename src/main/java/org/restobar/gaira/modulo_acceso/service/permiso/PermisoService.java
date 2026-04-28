package org.restobar.gaira.modulo_acceso.service.permiso;

import java.util.List;
import java.util.Map;

import org.restobar.gaira.modulo_acceso.dto.permiso.PermisoCreate;
import org.restobar.gaira.modulo_acceso.dto.permiso.PermisoResponse;
import org.restobar.gaira.modulo_acceso.dto.permiso.PermisoUpdate;
import org.restobar.gaira.modulo_acceso.entity.Permiso;
import org.restobar.gaira.modulo_acceso.mapper.permiso.PermissionMapper;
import org.restobar.gaira.modulo_acceso.repository.PermisoRepository;
import org.restobar.gaira.security.audit.annotation.Auditable;
import org.restobar.gaira.security.audit.util.AuditableService;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class PermisoService implements AuditableService<Long, Object> {

    private final PermisoRepository permisoRepository;
    private final PermissionMapper permissionMapper;

    @Override
    public Object getEntity(Long id) {
        return permisoRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof Permiso p) {
            return permissionMapper.toAuditMap(p);
        } else if (entity instanceof PermisoResponse pr) {
            return permissionMapper.toAuditMap(pr);
        }
        return Map.of();
    }

    @Transactional(readOnly = true)
    public List<PermisoResponse> findAll() {
        return permisoRepository.findAll().stream()
                .map(permissionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PermisoResponse findById(Long id) {
        Permiso permiso = permisoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Permiso no encontrado"));
        return permissionMapper.toResponse(permiso);
    }

    @Transactional
    @Auditable(tabla = "permiso", operacion = "INSERT")
    public PermisoResponse create(PermisoCreate request) {
        if (permisoRepository.existsByNombre(request.nombre())) {
            throw new ResponseStatusException(CONFLICT, "Ya existe un permiso con ese nombre");
        }

        Permiso permiso = Permiso.builder()
                .nombre(request.nombre())
                .modulo(request.modulo())
                .accion(request.accion())
                .descripcion(request.descripcion())
                .activo(request.activo() == null || request.activo())
                .build();

        Permiso savedPermiso = permisoRepository.save(permiso);
        return permissionMapper.toResponse(savedPermiso);
    }

    @Transactional
    @Auditable(tabla = "permiso", operacion = "UPDATE", idParamName = "id")
    public PermisoResponse update(Long id, PermisoUpdate request) {
        Permiso permiso = permisoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Permiso no encontrado"));

        if (!permiso.getNombre().equals(request.nombre()) && permisoRepository.existsByNombre(request.nombre())) {
            throw new ResponseStatusException(CONFLICT, "Ya existe un permiso con ese nombre");
        }

        permiso.setNombre(request.nombre());
        permiso.setModulo(request.modulo());
        permiso.setAccion(request.accion());
        permiso.setDescripcion(request.descripcion());
        if (request.activo() != null) {
            permiso.setActivo(request.activo());
        }

        Permiso savedPermiso = permisoRepository.save(permiso);
        return permissionMapper.toResponse(savedPermiso);
    }

    @Transactional
    @Auditable(tabla = "permiso", operacion = "DELETE", idParamName = "id")
    public void delete(Long id) {
        Permiso permiso = permisoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Permiso no encontrado"));
        permisoRepository.delete(permiso);
    }
}
