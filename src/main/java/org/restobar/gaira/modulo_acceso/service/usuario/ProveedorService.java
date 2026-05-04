package org.restobar.gaira.modulo_acceso.service.usuario;

import java.util.List;
import java.util.Map;

import org.restobar.gaira.modulo_acceso.dto.usuario.ProveedorRequest;
import org.restobar.gaira.modulo_acceso.dto.usuario.ProveedorResponse;
import org.restobar.gaira.modulo_acceso.entity.Proveedor;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_acceso.mapper.usuario.ProveedorMapper;
import org.restobar.gaira.modulo_acceso.repository.ProveedorRepository;
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
        return proveedorRepository.findByFiltros(empresa, nit, categoria).stream()
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
    public ProveedorResponse create(ProveedorRequest request, Long idUsuarioActual) {
        validateUniqueFields(request, null);

        Usuario creadoPor = usuarioRepository.findById(idUsuarioActual)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario autenticado no encontrado"));

        Proveedor proveedor = Proveedor.builder()
                .empresa(request.empresa().trim())
                .nit(request.nit() != null ? request.nit().trim() : null)
                .nombreContacto(request.nombreContacto().trim())
                .telefono(request.telefono().trim())
                .correo(request.correo() != null ? request.correo().trim() : null)
                .direccion(request.direccion())
                .categoriaProductos(request.categoriaProductos())
                .activo(request.activo() == null || request.activo())
                .creadoPor(creadoPor)
                .build();

        proveedor = proveedorRepository.save(proveedor);
        return proveedorMapper.toResponse(proveedor);
    }

    @Transactional
    @Auditable(tabla = "proveedor", operacion = "UPDATE", idParamName = "id")
    public ProveedorResponse update(Long id, ProveedorRequest request) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Proveedor no encontrado"));

        validateUniqueFields(request, proveedor);

        proveedor.setEmpresa(request.empresa().trim());
        proveedor.setNit(request.nit() != null ? request.nit().trim() : null);
        proveedor.setNombreContacto(request.nombreContacto().trim());
        proveedor.setTelefono(request.telefono().trim());
        proveedor.setCorreo(request.correo() != null ? request.correo().trim() : null);
        proveedor.setDireccion(request.direccion());
        proveedor.setCategoriaProductos(request.categoriaProductos());
        if (request.activo() != null) {
            proveedor.setActivo(request.activo());
        }

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

    // ─── Validaciones privadas ────────────────────────────────────────────────

    private void validateUniqueFields(ProveedorRequest request, Proveedor actual) {
        // NIT único
        if (request.nit() != null && !request.nit().isBlank()) {
            boolean nitCambio = actual == null || !request.nit().equalsIgnoreCase(actual.getNit());
            if (nitCambio && proveedorRepository.existsByNit(request.nit())) {
                throw new ResponseStatusException(CONFLICT, "NIT ya registrado");
            }
        }
        // Correo único
        if (request.correo() != null && !request.correo().isBlank()) {
            boolean correoCambio = actual == null || !request.correo().equalsIgnoreCase(actual.getCorreo());
            if (correoCambio && proveedorRepository.existsByCorreo(request.correo())) {
                throw new ResponseStatusException(CONFLICT, "Correo ya registrado");
            }
        }
    }
}
