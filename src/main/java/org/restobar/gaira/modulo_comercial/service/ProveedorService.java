package org.restobar.gaira.modulo_comercial.service;

import java.util.List;
import java.util.Map;

import org.restobar.gaira.modulo_comercial.dto.ProveedorCreate;
import org.restobar.gaira.modulo_comercial.dto.ProveedorResponse;
import org.restobar.gaira.modulo_comercial.dto.ProveedorUpdate;
import org.restobar.gaira.modulo_comercial.entity.Proveedor;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_comercial.mapper.ProveedorMapper;
import org.restobar.gaira.modulo_comercial.repository.ProveedorRepository;
import org.restobar.gaira.modulo_acceso.repository.UsuarioRepository;
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
public class ProveedorService implements AuditableService<Long, Object> {

    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProveedorMapper proveedorMapper;

    // ─── AuditableService ────────────────────────────────────────────────────

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

    // ─── Consultas ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ProveedorResponse> findAll(String empresa, String nit, String categoria) {
        String empresaPattern = (empresa != null && !empresa.isBlank()) ? "%" + empresa.toLowerCase() + "%" : null;
        String nitPattern = (nit != null && !nit.isBlank()) ? "%" + nit.toLowerCase() + "%" : null;
        String categoriaPattern = (categoria != null && !categoria.isBlank()) ? "%" + categoria.toLowerCase() + "%" : null;

        return proveedorRepository.findByFiltros(empresaPattern, nitPattern, categoriaPattern).stream()
                .map(proveedorMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProveedorResponse findById(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Proveedor no encontrado"));
        return proveedorMapper.toResponse(proveedor);
    }

    // ─── Escritura ────────────────────────────────────────────────────────────

    @Transactional
    @Auditable(tabla = "proveedor", operacion = "INSERT")
    public ProveedorResponse create(ProveedorCreate request, Long idUsuarioActual) {
        validateUniqueFieldsCreate(request);

        Usuario creadoPor = usuarioRepository.findById(idUsuarioActual)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario autenticado no encontrado"));

        Proveedor proveedor = proveedorMapper.toEntity(request, creadoPor);
        proveedor = proveedorRepository.save(proveedor);
        return proveedorMapper.toResponse(proveedor);
    }

    @Transactional
    @Auditable(tabla = "proveedor", operacion = "UPDATE", idParamName = "id")
    public ProveedorResponse update(Long id, ProveedorUpdate request) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Proveedor no encontrado"));

        validateUniqueFieldsUpdate(request, proveedor);

        proveedorMapper.updateEntityFromDto(proveedor, request);
        proveedorRepository.save(proveedor);
        return proveedorMapper.toResponse(proveedor);
    }

    @Transactional
    @Auditable(tabla = "proveedor", operacion = "UPDATE", idParamName = "id")
    public ProveedorResponse desactivar(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Proveedor no encontrado"));

        proveedor.setActivo(false);
        proveedorRepository.save(proveedor);
        return proveedorMapper.toResponse(proveedor);
    }

    @Transactional
    @Auditable(tabla = "proveedor", operacion = "DELETE", idParamName = "id")
    public void delete(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Proveedor no encontrado"));
        proveedorRepository.delete(proveedor);
    }

    // ─── Validaciones privadas ────────────────────────────────────────────────

    private void validateUniqueFieldsCreate(ProveedorCreate request) {
        if (request.nit() != null && !request.nit().isBlank() && proveedorRepository.existsByNit(request.nit())) {
            throw new ResponseStatusException(CONFLICT, "NIT ya registrado");
        }
        if (request.correo() != null && !request.correo().isBlank() && proveedorRepository.existsByCorreo(request.correo())) {
            throw new ResponseStatusException(CONFLICT, "Correo ya registrado");
        }
    }

    private void validateUniqueFieldsUpdate(ProveedorUpdate request, Proveedor actual) {
        if (request.nit() != null && !request.nit().isBlank()) {
            boolean nitCambio = !request.nit().equalsIgnoreCase(actual.getNit());
            if (nitCambio && proveedorRepository.existsByNit(request.nit())) {
                throw new ResponseStatusException(CONFLICT, "NIT ya registrado");
            }
        }
        if (request.correo() != null && !request.correo().isBlank()) {
            boolean correoCambio = !request.correo().equalsIgnoreCase(actual.getCorreo());
            if (correoCambio && proveedorRepository.existsByCorreo(request.correo())) {
                throw new ResponseStatusException(CONFLICT, "Correo ya registrado");
            }
        }
    }
}
