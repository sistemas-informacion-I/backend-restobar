package org.restobar.gaira.modulo_acceso.service.usuario;

import java.util.List;
import java.util.Map;

import org.restobar.gaira.modulo_acceso.dto.usuario.ProveedorResponse;
import org.restobar.gaira.modulo_acceso.entity.Proveedor;
import org.restobar.gaira.modulo_acceso.mapper.usuario.ProveedorMapper;
import org.restobar.gaira.modulo_acceso.repository.ProveedorRepository;
import org.restobar.gaira.security.audit.util.AuditableService;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ProveedorService implements AuditableService<Long, Object> {

    private final ProveedorRepository proveedorRepository;
    private final ProveedorMapper proveedorMapper;

    @Override
    public Object getEntity(Long id) {
        return proveedorRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof Proveedor p) {
            return proveedorMapper.toAuditMap(p);
        } else if (entity instanceof ProveedorResponse pr) {
            return proveedorMapper.toAuditMap(pr);
        }
        return Map.of();
    }

    @Transactional(readOnly = true)
    public List<ProveedorResponse> findAll() {
        return proveedorRepository.findAll().stream()
                .map(proveedorMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProveedorResponse findById(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Proveedor no encontrado"));
        return proveedorMapper.toResponse(proveedor);
    }
}
