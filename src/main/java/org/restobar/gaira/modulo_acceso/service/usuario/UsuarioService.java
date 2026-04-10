package org.restobar.gaira.modulo_acceso.service.usuario;

import java.util.List;

import org.restobar.gaira.modulo_acceso.dto.usuario.UsuarioRequest;
import org.restobar.gaira.modulo_acceso.dto.usuario.UsuarioResponse;
import org.restobar.gaira.modulo_acceso.entity.Rol;
import org.restobar.gaira.modulo_acceso.entity.RolUsuario;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_acceso.repository.RolRepository;
import org.restobar.gaira.modulo_acceso.repository.RolUsuarioRepository;
import org.restobar.gaira.modulo_acceso.repository.UsuarioRepository;
import org.restobar.gaira.modulo_acceso.mapper.AutenticacionMapper;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@SuppressWarnings("null")
public class UsuarioService {

    private static final String ESTADO_HABILITADO = "HABILITADO";
    private static final String ESTADO_BLOQUEADO = "BLOQUEADO";
    private static final String ESTADO_SUSPENDIDO = "SUSPENDIDO";

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          RolUsuarioRepository rolUsuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.rolUsuarioRepository = rolUsuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> findAll() {
        return usuarioRepository.findAllWithRoles().stream()
                .map(AutenticacionMapper::toUsuarioResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse findById(Long id) {
        Usuario usuario = usuarioRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));
        return AutenticacionMapper.toUsuarioResponse(usuario);
    }

    @Transactional
    public UsuarioResponse create(UsuarioRequest request) {
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

        Usuario usuario = Usuario.builder()
                .ci(request.ci())
                .nombre(request.nombre())
                .apellido(request.apellido())
                .username(request.username() != null ? request.username() : request.ci())
                .passwordHash(request.password() != null ? passwordEncoder.encode(request.password()) : passwordEncoder.encode("CHANGE_ME"))
                .telefono(request.telefono())
                .sexo(request.sexo())
                .correo(request.correo())
                .direccion(request.direccion())
                .activo(request.activo() == null || request.activo())
                .estadoAcceso(request.estadoAcceso() != null ? request.estadoAcceso() : ESTADO_HABILITADO)
                .intentosFallidos(0)
                .build();

        usuarioRepository.save(usuario);

        // Assign roles if provided
        if (request.roles() != null && !request.roles().isEmpty()) {
            assignRoles(usuario, request.roles());
        }

        return findById(usuario.getIdUsuario());
    }

    @Transactional
    public UsuarioResponse update(Long id, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));

        if (!usuario.getCi().equals(request.ci()) && usuarioRepository.existsByCi(request.ci())) {
            throw new ResponseStatusException(CONFLICT, "CI ya registrado");
        }
        if (request.correo() != null && !request.correo().isBlank() && !request.correo().equals(usuario.getCorreo())
                && usuarioRepository.existsByCorreo(request.correo())) {
            throw new ResponseStatusException(CONFLICT, "Correo ya registrado");
        }

        usuario.setCi(request.ci());
        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setTelefono(request.telefono());
        usuario.setSexo(request.sexo());
        usuario.setCorreo(request.correo());
        usuario.setDireccion(request.direccion());
        if (request.activo() != null) {
            usuario.setActivo(request.activo());
        }
        if (request.estadoAcceso() != null) {
            validarEstadoAcceso(request.estadoAcceso());
            usuario.setEstadoAcceso(request.estadoAcceso());
        }

        usuarioRepository.save(usuario);

        // Replace role assignments if roles list was explicitly provided
        if (request.roles() != null) {
            // Remove existing role assignments
            List<RolUsuario> existing = rolUsuarioRepository.findByUsuario_IdUsuario(id);
            rolUsuarioRepository.deleteAll(existing);

            // Assign new roles
            if (!request.roles().isEmpty()) {
                assignRoles(usuario, request.roles());
            }
        }

        return findById(id);
    }

    @Transactional
    public UsuarioResponse bloquear(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));
        usuario.setEstadoAcceso(ESTADO_BLOQUEADO);
        return AutenticacionMapper.toUsuarioResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse desbloquear(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));
        usuario.setEstadoAcceso(ESTADO_HABILITADO);
        usuario.setIntentosFallidos(0);
        return AutenticacionMapper.toUsuarioResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse suspender(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));
        usuario.setEstadoAcceso(ESTADO_SUSPENDIDO);
        return AutenticacionMapper.toUsuarioResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void delete(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));
        usuarioRepository.delete(usuario);
    }

    // ─── Helpers ────────────────────────────────────────────────────────────────

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
}
