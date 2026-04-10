package org.restobar.gaira.modulo_acceso.service.rol;

import java.util.List;

import org.restobar.gaira.modulo_acceso.dto.permiso.AssignPermissionRequest;
import org.restobar.gaira.modulo_acceso.dto.rol.AssignRoleRequest;
import org.restobar.gaira.modulo_acceso.dto.rol.RolRequest;
import org.restobar.gaira.modulo_acceso.dto.rol.RolResponse;
import org.restobar.gaira.modulo_acceso.entity.Permiso;
import org.restobar.gaira.modulo_acceso.entity.Rol;
import org.restobar.gaira.modulo_acceso.entity.RolPermiso;
import org.restobar.gaira.modulo_acceso.entity.RolUsuario;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_acceso.repository.PermisoRepository;
import org.restobar.gaira.modulo_acceso.repository.RolPermisoRepository;
import org.restobar.gaira.modulo_acceso.repository.RolRepository;
import org.restobar.gaira.modulo_acceso.repository.RolUsuarioRepository;
import org.restobar.gaira.modulo_acceso.repository.UsuarioRepository;
import org.restobar.gaira.modulo_acceso.mapper.AutenticacionMapper;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@SuppressWarnings("null")
public class RolService {

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final RolPermisoRepository rolPermisoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolUsuarioRepository rolUsuarioRepository;

    public RolService(RolRepository rolRepository,
            PermisoRepository permisoRepository,
            RolPermisoRepository rolPermisoRepository,
            UsuarioRepository usuarioRepository,
            RolUsuarioRepository rolUsuarioRepository) {
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
        this.rolPermisoRepository = rolPermisoRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolUsuarioRepository = rolUsuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<RolResponse> findAll() {
        return rolRepository.findAll().stream()
                .map(AutenticacionMapper::toRolResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RolResponse findById(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Rol no encontrado"));
        return AutenticacionMapper.toRolResponse(rol);
    }

    @Transactional
    public RolResponse create(RolRequest request) {
        if (rolRepository.existsByNombre(request.nombre())) {
            throw new ResponseStatusException(CONFLICT, "Ya existe un rol con ese nombre");
        }

        Integer nivelAcceso = request.nivelAcceso();

        Rol rol = Rol.builder()
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .nivelAcceso(nivelAcceso != null ? nivelAcceso : 1)
                .activo(request.activo() == null || request.activo())
                .build();

        Rol savedRol = rolRepository.save(rol);
        
        if (request.permisos() != null && !request.permisos().isEmpty()) {
            for (Long idPermiso : request.permisos()) {
                Permiso permiso = permisoRepository.findById(idPermiso)
                        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Permiso no encontrado: " + idPermiso));
                RolPermiso rolPermiso = RolPermiso.builder()
                        .rol(savedRol)
                        .permiso(permiso)
                        .activo(true)
                        .build();
                rolPermisoRepository.save(rolPermiso);
            }
        }
        
        return AutenticacionMapper.toRolResponse(savedRol);
    }

    @Transactional
    public RolResponse update(Long id, RolRequest request) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Rol no encontrado"));

        if (!rol.getNombre().equals(request.nombre()) && rolRepository.existsByNombre(request.nombre())) {
            throw new ResponseStatusException(CONFLICT, "Ya existe un rol con ese nombre");
        }

        rol.setNombre(request.nombre());
        rol.setDescripcion(request.descripcion());
        if (request.nivelAcceso() != null) {
            rol.setNivelAcceso(request.nivelAcceso());
        }
        if (request.activo() != null) {
            rol.setActivo(request.activo());
        }

        Rol savedRol = rolRepository.save(rol);

        if (request.permisos() != null) {
            // Eliminar previos e insertar nuevos para actualizar
            List<RolPermiso> actuales = rolPermisoRepository.findByRol_IdRol(savedRol.getIdRol());
            rolPermisoRepository.deleteAll(actuales);

            for (Long idPermiso : request.permisos()) {
                Permiso permiso = permisoRepository.findById(idPermiso)
                        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Permiso no encontrado: " + idPermiso));
                RolPermiso rolPermiso = RolPermiso.builder()
                        .rol(savedRol)
                        .permiso(permiso)
                        .activo(true)
                        .build();
                rolPermisoRepository.save(rolPermiso);
            }
        }

        return AutenticacionMapper.toRolResponse(savedRol);
    }

    @Transactional
    public void delete(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Rol no encontrado"));
        rolRepository.delete(rol);
    }

    @Transactional
    public void assignPermission(Long idRol, AssignPermissionRequest request) {
        Rol rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Rol no encontrado"));
        Permiso permiso = permisoRepository.findById(request.idPermiso())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Permiso no encontrado"));

        if (rolPermisoRepository.existsByRol_IdRolAndPermiso_IdPermiso(rol.getIdRol(), permiso.getIdPermiso())) {
            return;
        }

        RolPermiso rolPermiso = RolPermiso.builder()
                .rol(rol)
                .permiso(permiso)
                .activo(true)
                .build();
        rolPermisoRepository.save(rolPermiso);
    }

    @Transactional
    public void assignRoleToUser(Long idUsuario, AssignRoleRequest request) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));
        Rol rol = rolRepository.findById(request.idRol())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Rol no encontrado"));

        if (rolUsuarioRepository.existsByUsuario_IdUsuarioAndRol_IdRol(idUsuario, request.idRol())) {
            return;
        }

        RolUsuario rolUsuario = RolUsuario.builder()
                .usuario(usuario)
                .rol(rol)
                .activo(true)
                .build();
        rolUsuarioRepository.save(rolUsuario);
    }
}
