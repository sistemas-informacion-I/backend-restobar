package org.restobar.gaira.modulo_acceso.service.usuario;

import java.util.List;
import java.util.Map;

import org.restobar.gaira.modulo_acceso.dto.usuario.UsuarioCreate;
import org.restobar.gaira.modulo_acceso.dto.usuario.UsuarioResponse;
import org.restobar.gaira.modulo_acceso.dto.usuario.UsuarioUpdate;
import org.restobar.gaira.modulo_acceso.entity.Rol;
import org.restobar.gaira.modulo_acceso.entity.RolUsuario;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_acceso.mapper.usuario.UsuarioMapper;
import org.restobar.gaira.modulo_acceso.repository.RolRepository;
import org.restobar.gaira.modulo_acceso.repository.RolUsuarioRepository;
import org.restobar.gaira.modulo_acceso.repository.UsuarioRepository;
import org.restobar.gaira.security.audit.annotation.Auditable;
import org.restobar.gaira.security.audit.util.AuditableService;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class UsuarioService implements AuditableService<Long, Object> {

    private static final String ESTADO_HABILITADO = "HABILITADO";
    private static final String ESTADO_BLOQUEADO = "BLOQUEADO";
    private static final String ESTADO_SUSPENDIDO = "SUSPENDIDO";

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    @Override
    public Object getEntity(Long id) {
        return usuarioRepository.findByIdWithRoles(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof Usuario u) {
            return usuarioMapper.toAuditMapLite(u,
                    u.getRolesUsuario() != null ? u.getRolesUsuario().stream().toList() : List.of());
        } else if (entity instanceof UsuarioResponse ur) {
            return usuarioMapper.toAuditMap(ur);
        }
        return Map.of();
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> findAll() {
        return usuarioRepository.findAllWithRoles().stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse findById(Long id) {
        Usuario usuario = usuarioRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));
        return usuarioMapper.toResponse(usuario);
    }

    @Transactional
    @Auditable(tabla = "usuario", operacion = "INSERT")
    public UsuarioResponse create(UsuarioCreate request) {
        if (usuarioRepository.existsByCi(request.ci())) {
            throw new ResponseStatusException(CONFLICT, "CI ya registrado");
        }
        if (request.correo() != null && !request.correo().isBlank()
                && usuarioRepository.existsByCorreo(request.correo())) {
            throw new ResponseStatusException(CONFLICT, "Correo ya registrado");
        }
        if (request.username() != null && !request.username().isBlank() &&
                usuarioRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(CONFLICT, "Username ya registrado");
        }

        String passwordHash = request.password() != null ? passwordEncoder.encode(request.password())
                : passwordEncoder.encode("CHANGE_ME");

        Usuario usuario = usuarioMapper.toEntity(request, passwordHash);
        if (usuario.getUsername() == null || usuario.getUsername().isBlank()) {
            usuario.setUsername(request.ci());
        }

        validarTipoUsuario(request.tipoUsuario());
        usuarioRepository.save(usuario);

        if (request.roles() != null && !request.roles().isEmpty()) {
            assignRoles(usuario, request.roles());
        }

        return findById(usuario.getIdUsuario());
    }

    @Transactional
    @Auditable(tabla = "usuario", operacion = "UPDATE", idParamName = "id")
    public UsuarioResponse update(Long id, UsuarioUpdate request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));

        // Protección de Superusuario: Nadie puede tocar a un Superusuario
        if ("S".equals(usuario.getTipoUsuario())) {
            throw new ResponseStatusException(BAD_REQUEST, "No se puede modificar una cuenta de nivel Superusuario");
        }

        if (!usuario.getCi().equals(request.ci()) && usuarioRepository.existsByCi(request.ci())) {
            throw new ResponseStatusException(CONFLICT, "CI ya registrado");
        }
        if (request.correo() != null && !request.correo().isBlank() && !request.correo().equals(usuario.getCorreo())
                && usuarioRepository.existsByCorreo(request.correo())) {
            throw new ResponseStatusException(CONFLICT, "Correo ya registrado");
        }

        usuarioMapper.updateEntityFromDto(usuario, request);

        if (request.estadoAcceso() != null) {
            validarEstadoAcceso(request.estadoAcceso());
        }

        usuarioRepository.save(usuario);

        if (request.roles() != null) {
            List<RolUsuario> existing = rolUsuarioRepository.findByUsuario_IdUsuario(id);
            rolUsuarioRepository.deleteAll(existing);
            if (!request.roles().isEmpty()) {
                assignRoles(usuario, request.roles());
            }
        }

        return findById(id);
    }

    @Transactional
    @Auditable(tabla = "usuario", operacion = "UPDATE", idParamName = "id")
    public UsuarioResponse bloquear(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));
        usuario.setEstadoAcceso(ESTADO_BLOQUEADO);
        Usuario savedUser = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(savedUser);
    }

    @Transactional
    @Auditable(tabla = "usuario", operacion = "UPDATE", idParamName = "id")
    public UsuarioResponse desbloquear(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));
        usuario.setEstadoAcceso(ESTADO_HABILITADO);
        usuario.setIntentosFallidos(0);
        Usuario savedUser = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(savedUser);
    }

    @Transactional
    @Auditable(tabla = "usuario", operacion = "UPDATE", idParamName = "id")
    public UsuarioResponse suspender(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));
        usuario.setEstadoAcceso(ESTADO_SUSPENDIDO);
        Usuario savedUser = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(savedUser);
    }

    @Transactional
    @Auditable(tabla = "usuario", operacion = "DELETE", idParamName = "id")
    public void delete(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));

        if ("S".equals(usuario.getTipoUsuario())) {
            throw new ResponseStatusException(BAD_REQUEST, "No se puede eliminar una cuenta de nivel Superusuario");
        }

        usuarioRepository.delete(usuario);
    }

    private void assignRoles(Usuario usuario, List<Long> rolIds) {
        for (Long idRol : rolIds) {
            Rol rol = rolRepository.findById(idRol)
                    .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Rol no encontrado: " + idRol));
            if (!rolUsuarioRepository.existsByUsuario_IdUsuarioAndRol_IdRol(usuario.getIdUsuario(), idRol)) {
                rolUsuarioRepository.save(RolUsuario.builder()
                        .usuario(usuario)
                        .rol(rol)
                        .activo(true)
                        .build());
            }
        }
    }

    private void validarEstadoAcceso(String estado) {
        if (!ESTADO_HABILITADO.equals(estado) && !ESTADO_BLOQUEADO.equals(estado)
                && !ESTADO_SUSPENDIDO.equals(estado)) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Estado de acceso inválido. Valores aceptados: HABILITADO, SUSPENDIDO, BLOQUEADO");
        }
    }

    private void validarTipoUsuario(String tipo) {
        if (!"S".equals(tipo) && !"E".equals(tipo) && !"C".equals(tipo)) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Tipo de usuario inválido. Valores aceptados: S (Superuser), E (Empleado), C (Cliente)");
        }
    }
}
