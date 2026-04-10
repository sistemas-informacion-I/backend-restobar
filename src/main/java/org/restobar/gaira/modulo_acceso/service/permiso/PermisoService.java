package org.restobar.gaira.modulo_acceso.service.permiso;

import java.util.List;

import org.restobar.gaira.modulo_acceso.dto.permiso.PermisoRequest;
import org.restobar.gaira.modulo_acceso.dto.permiso.PermisoResponse;
import org.restobar.gaira.modulo_acceso.entity.Permiso;
import org.restobar.gaira.modulo_acceso.repository.PermisoRepository;
import org.restobar.gaira.modulo_acceso.mapper.AutenticacionMapper;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@SuppressWarnings("null")
public class PermisoService {

    private final PermisoRepository permisoRepository;

    public PermisoService(PermisoRepository permisoRepository) {
        this.permisoRepository = permisoRepository;
    }

    @Transactional(readOnly = true)
    public List<PermisoResponse> findAll() {
        return permisoRepository.findAll().stream()
                .map(AutenticacionMapper::toPermisoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PermisoResponse findById(Long id) {
        Permiso permiso = permisoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Permiso no encontrado"));
        return AutenticacionMapper.toPermisoResponse(permiso);
    }

    @Transactional
    public PermisoResponse create(PermisoRequest request) {
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

        return AutenticacionMapper.toPermisoResponse(permisoRepository.save(permiso));
    }

    @Transactional
    public PermisoResponse update(Long id, PermisoRequest request) {
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

        return AutenticacionMapper.toPermisoResponse(permisoRepository.save(permiso));
    }

    @Transactional
    public void delete(Long id) {
        Permiso permiso = permisoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Permiso no encontrado"));
        permisoRepository.delete(permiso);
    }
}
