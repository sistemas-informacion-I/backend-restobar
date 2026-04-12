package org.restobar.gaira.modulo_acceso.service.rol;

import java.util.List;
import java.util.Map;

import org.restobar.gaira.modulo_acceso.dto.permiso.AssignPermission;
import org.restobar.gaira.modulo_acceso.dto.rol.AssignRole;
import org.restobar.gaira.modulo_acceso.dto.rol.RolCreate;
import org.restobar.gaira.modulo_acceso.dto.rol.RolResponse;
import org.restobar.gaira.modulo_acceso.dto.rol.RolUpdate;
import org.restobar.gaira.modulo_acceso.entity.Permiso;
import org.restobar.gaira.modulo_acceso.entity.Rol;
import org.restobar.gaira.modulo_acceso.entity.RolPermiso;
import org.restobar.gaira.modulo_acceso.entity.RolUsuario;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_acceso.mapper.rol.RolMapper;
import org.restobar.gaira.modulo_acceso.mapper.usuario.UsuarioMapper;
import org.restobar.gaira.modulo_acceso.repository.PermisoRepository;
import org.restobar.gaira.modulo_acceso.repository.RolRepository;
import org.restobar.gaira.modulo_acceso.repository.RolUsuarioRepository;
import org.restobar.gaira.modulo_acceso.repository.UsuarioRepository;
import org.restobar.gaira.security.audit.annotation.Auditable;
import org.restobar.gaira.security.audit.util.AuditableService;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class RolService implements AuditableService<Long, Object> {

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final RolMapper rolMapper;
    private final UsuarioMapper usuarioMapper;

    @Override
    public Object getEntity(Long id) {
        var rol = rolRepository.findByIdWithPermisos(id);
        if (rol.isPresent())
            return rol.get();
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof Rol r) {
            return rolMapper.toAuditMap(r);
        } else if (entity instanceof RolResponse rr) {
            return rolMapper.toAuditMap(rr);
        } else if (entity instanceof Usuario u) {
            return usuarioMapper.toAuditMapLite(u, rolUsuarioRepository.findByUsuario_IdUsuario(u.getIdUsuario()));
        }
        return Map.of();
    }

    @Transactional(readOnly = true)
    public List<RolResponse> findAll() {
        return rolRepository.findAll().stream()
                .map(rolMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RolResponse findById(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Rol no encontrado"));
        return rolMapper.toResponse(rol);
    }

    @Transactional
    @Auditable(tabla = "rol", operacion = "INSERT")
    public RolResponse create(RolCreate request) {
        if (rolRepository.existsByNombre(request.nombre())) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT,
                    "Ya existe un rol con ese nombre");
        }

        Rol rol = rolMapper.toEntity(request);

        if (request.permisos() != null && !request.permisos().isEmpty()) {
            for (Long idPermiso : request.permisos()) {
                Permiso permiso = permisoRepository.findById(idPermiso)
                        .orElseThrow(
                                () -> new ResponseStatusException(NOT_FOUND, "Permiso no encontrado: " + idPermiso));

                RolPermiso rolPermiso = RolPermiso.builder()
                        .rol(rol)
                        .permiso(permiso)
                        .activo(true)
                        .build();
                rol.getRolesPermiso().add(rolPermiso);
            }
        }

        Rol savedRol = rolRepository.save(rol);
        return rolMapper.toResponse(savedRol);
    }

    @Transactional
    @Auditable(tabla = "rol", operacion = "UPDATE", idParamName = "id")
    public RolResponse update(Long id, RolUpdate request) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Rol no encontrado"));

        if (!rol.getNombre().equals(request.nombre()) && rolRepository.existsByNombre(request.nombre())) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT,
                    "Ya existe un rol con ese nombre");
        }

        rolMapper.updateEntityFromDto(rol, request);

        if (request.permisos() != null) {
            rol.getRolesPermiso().removeIf(rp -> !request.permisos().contains(rp.getPermiso().getIdPermiso()));

            for (Long idPermiso : request.permisos()) {
                boolean exists = rol.getRolesPermiso().stream()
                        .anyMatch(rp -> rp.getPermiso().getIdPermiso().equals(idPermiso));

                if (!exists) {
                    Permiso permiso = permisoRepository.findById(idPermiso)
                            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                                    "Permiso no encontrado: " + idPermiso));

                    RolPermiso rolPermiso = RolPermiso.builder()
                            .rol(rol)
                            .permiso(permiso)
                            .activo(true)
                            .build();
                    rol.getRolesPermiso().add(rolPermiso);
                }
            }
        }

        Rol savedRol = rolRepository.save(rol);
        rolRepository.flush();
        return rolMapper.toResponse(savedRol);
    }

    @Transactional
    @Auditable(tabla = "rol", operacion = "DELETE", idParamName = "id")
    public void delete(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Rol no encontrado"));
        rolRepository.delete(rol);
    }

    @Transactional
    @Auditable(tabla = "rol", operacion = "UPDATE", idParamName = "idRol")
    public void assignPermission(Long idRol, AssignPermission request) {
        Rol rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Rol no encontrado"));
        Permiso permiso = permisoRepository.findById(request.idPermiso())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Permiso no encontrado"));

        RolPermiso existing = rol.getRolesPermiso().stream()
                .filter(rp -> rp.getPermiso().getIdPermiso().equals(permiso.getIdPermiso()))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            if (Boolean.FALSE.equals(existing.getActivo())) {
                existing.setActivo(true);
                rolRepository.save(rol);
            }
            return;
        }

        RolPermiso rolPermiso = RolPermiso.builder()
                .rol(rol)
                .permiso(permiso)
                .activo(true)
                .build();

        rol.getRolesPermiso().add(rolPermiso);
        rolRepository.save(rol);
        rolRepository.flush();
    }

    @Transactional
    @Auditable(tabla = "usuario", operacion = "UPDATE", idParamName = "idUsuario")
    public void assignRoleToUser(Long idUsuario, AssignRole request) {
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
        rolUsuarioRepository.flush();
    }
}
