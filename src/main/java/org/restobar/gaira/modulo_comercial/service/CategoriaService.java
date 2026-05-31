package org.restobar.gaira.modulo_comercial.service;

import java.util.List;
import java.util.Map;

import org.restobar.gaira.modulo_comercial.dto.categoria.CategoriaCreate;
import org.restobar.gaira.modulo_comercial.dto.categoria.CategoriaResponse;
import org.restobar.gaira.modulo_comercial.dto.categoria.CategoriaUpdate;
import org.restobar.gaira.modulo_comercial.entity.Categoria;
import org.restobar.gaira.modulo_comercial.mapper.CategoriaMapper;
import org.restobar.gaira.modulo_comercial.repository.CategoriaRepository;
import org.restobar.gaira.security.audit.annotation.Auditable;
import org.restobar.gaira.security.audit.util.AuditableService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CategoriaService implements AuditableService<Long, Object> {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    // ─── AuditableService ────────────────────────────────────────────────────

    @Override
    public Object getEntity(Long id) {
        return categoriaRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof Categoria c) {
            return categoriaMapper.toAuditMap(c);
        } else if (entity instanceof CategoriaResponse cr) {
            return categoriaMapper.toAuditMap(cr);
        }
        return Map.of();
    }

    // ─── Consultas ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<CategoriaResponse> findAll(String nombre) {
        String nombrePattern = (nombre != null && !nombre.isBlank())
                ? "%" + nombre.toLowerCase() + "%"
                : null;
        return categoriaRepository.findByFiltros(nombrePattern).stream()
                .map(categoriaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse findById(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Categoría no encontrada"));
        return categoriaMapper.toResponse(categoria);
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> findRoots() {
        return categoriaRepository.findByCategoriaPadreIsNullOrderByNombreAsc().stream()
                .map(categoriaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> findChildren(Long idPadre) {
        categoriaRepository.findById(idPadre)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Categoría padre no encontrada"));
        return categoriaRepository.findByCategoriaPadreId(idPadre).stream()
                .map(categoriaMapper::toResponse)
                .toList();
    }

    // ─── Escritura ────────────────────────────────────────────────────────────

    @Transactional
    @Auditable(tabla = "categoria", operacion = "INSERT")
    public CategoriaResponse create(CategoriaCreate request) {
        if (categoriaRepository.existsByNombre(request.nombre())) {
            throw new ResponseStatusException(CONFLICT, "Ya existe una categoría con ese nombre");
        }

        Categoria padre = resolverPadre(request.idCategoriaPadre(), null);

        Categoria categoria = categoriaMapper.toEntity(request, padre);
        categoria = categoriaRepository.save(categoria);
        return categoriaMapper.toResponse(categoria);
    }

    @Transactional
    @Auditable(tabla = "categoria", operacion = "UPDATE", idParamName = "id")
    public CategoriaResponse update(Long id, CategoriaUpdate request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Categoría no encontrada"));

        if (categoriaRepository.existsByNombreAndIdNot(request.nombre(), id)) {
            throw new ResponseStatusException(CONFLICT, "Ya existe una categoría con ese nombre");
        }

        Categoria padre = resolverPadre(request.idCategoriaPadre(), id);

        categoria.setNombre(request.nombre().trim());
        categoria.setDescripcion(request.descripcion() != null ? request.descripcion().trim() : null);
        categoria.setCategoriaPadre(padre);
        categoria.setNivel(padre == null ? 1 : padre.getNivel() + 1);

        categoriaRepository.save(categoria);
        return categoriaMapper.toResponse(categoria);
    }

    @Transactional
    @Auditable(tabla = "categoria", operacion = "UPDATE", idParamName = "id")
    public CategoriaResponse desactivar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Categoría no encontrada"));

        if (categoriaRepository.existsByCategoriaPadreId(id)) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "No se puede desactivar una categoría que tiene subcategorías");
        }

        categoria.setActivo(false);
        categoriaRepository.save(categoria);
        return categoriaMapper.toResponse(categoria);
    }

    @Transactional
    @Auditable(tabla = "categoria", operacion = "UPDATE", idParamName = "id")
    public CategoriaResponse activar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Categoría no encontrada"));

        categoria.setActivo(true);
        categoriaRepository.save(categoria);
        return categoriaMapper.toResponse(categoria);
    }
    // ─── Helpers privados ────────────────────────────────────────────────────

    /**
     * Resuelve la categoría padre a partir del ID recibido.
     * Si {@code idCategoriaPadre} es null, devuelve null (la categoría será raíz).
     * Valida que el padre exista y que no se genere un ciclo en el árbol.
     *
     * @param idCategoriaPadre ID del padre recibido en el request (puede ser null)
     * @param idCategoriaActual ID de la categoría que se está editando (null en creación)
     */
    private Categoria resolverPadre(Long idCategoriaPadre, Long idCategoriaActual) {
        if (idCategoriaPadre == null) {
            return null;
        }

        
        Categoria padre = categoriaRepository.findById(idCategoriaPadre)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Categoría padre no encontrada"));

        // En edición: evitar que una categoría sea su propio padre
        if (idCategoriaActual != null && idCategoriaPadre.equals(idCategoriaActual)) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Una categoría no puede ser su propio padre");
        }

        // En edición: evitar ciclos (el padre no puede ser un descendiente de la categoría actual)
        if (idCategoriaActual != null) {
            List<Long> descendientes = categoriaRepository.findAllDescendantIds(idCategoriaActual);
            if (descendientes.contains(idCategoriaPadre)) {
                throw new ResponseStatusException(BAD_REQUEST,
                        "El padre seleccionado es un descendiente de esta categoría; se generaría un ciclo");
            }
        }

        return padre;
    }
}
