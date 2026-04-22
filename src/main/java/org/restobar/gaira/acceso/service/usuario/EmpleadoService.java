package org.restobar.gaira.acceso.service.usuario;

import java.util.List;

import org.restobar.gaira.acceso.dto.usuario.EmpleadoRequest;
import org.restobar.gaira.acceso.dto.usuario.EmpleadoResponse;
import org.restobar.gaira.acceso.entity.Empleado;
import org.restobar.gaira.acceso.entity.Rol;
import org.restobar.gaira.acceso.entity.RolUsuario;
import org.restobar.gaira.acceso.entity.Usuario;
import org.restobar.gaira.acceso.mapper.AutenticacionMapper;
import org.restobar.gaira.acceso.repository.EmpleadoRepository;
import org.restobar.gaira.acceso.repository.RolRepository;
import org.restobar.gaira.acceso.repository.RolUsuarioRepository;
import org.restobar.gaira.acceso.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@SuppressWarnings("null")
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public EmpleadoService(EmpleadoRepository empleadoRepository,
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            RolUsuarioRepository rolUsuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.empleadoRepository = empleadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.rolUsuarioRepository = rolUsuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<EmpleadoResponse> findAll() {
        return empleadoRepository.findAll().stream()
                .map(AutenticacionMapper::toEmpleadoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmpleadoResponse findById(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Empleado no encontrado"));
        return AutenticacionMapper.toEmpleadoResponse(empleado);
    }

    @Transactional
    public EmpleadoResponse create(EmpleadoRequest request) {
        validateUniqueFields(request, null, null);

        String username = resolveUsername(request);
        String codigoEmpleado = resolveCodigoEmpleado(request, null);

        Usuario usuario = Usuario.builder()
                .ci(request.ci())
                .nombre(request.nombre())
                .apellido(request.apellido())
                .username(username)
                .passwordHash(passwordEncoder.encode(resolvePassword(request.password())))
                .telefono(request.telefono())
                .sexo(request.sexo())
                .correo(request.correo())
                .direccion(request.direccion())
                .activo(request.activo() == null || request.activo())
                .estadoAcceso("HABILITADO")
                .intentosFallidos(0)
                .build();

        usuario = usuarioRepository.save(usuario);
        assignRoles(usuario, request.roles());

        Empleado empleado = Empleado.builder()
                .usuario(usuario)
                .codigoEmpleado(codigoEmpleado)
                .salario(request.salario())
                .turno(request.turno())
                .fechaContratacion(request.fechaContratacion())
                .fechaFinalizacion(request.fechaFinalizacion())
                .build();

        empleado = empleadoRepository.save(empleado);
        return AutenticacionMapper.toEmpleadoResponse(empleado);
    }

    @Transactional
    public EmpleadoResponse update(Long id, EmpleadoRequest request) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Empleado no encontrado"));

        Usuario usuario = empleado.getUsuario();
        if (usuario == null) {
            throw new ResponseStatusException(BAD_REQUEST, "El empleado no tiene un usuario asociado");
        }

        validateUniqueFields(request, usuario, empleado);

        usuario.setCi(request.ci());
        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setTelefono(request.telefono());
        usuario.setSexo(request.sexo());
        usuario.setCorreo(request.correo());
        usuario.setDireccion(request.direccion());
        usuario.setActivo(request.activo() == null || request.activo());

        String username = resolveUsername(request);
        if (!username.equals(usuario.getUsername())) {
            usuario.setUsername(username);
        }
        if (request.password() != null && !request.password().isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        usuarioRepository.save(usuario);

        empleado.setCodigoEmpleado(resolveCodigoEmpleado(request, empleado.getCodigoEmpleado()));
        empleado.setSalario(request.salario());
        empleado.setTurno(request.turno());
        empleado.setFechaContratacion(request.fechaContratacion());
        empleado.setFechaFinalizacion(request.fechaFinalizacion());
        empleadoRepository.save(empleado);

        if (request.roles() != null) {
            List<RolUsuario> existentes = rolUsuarioRepository.findByUsuario_IdUsuario(usuario.getIdUsuario());
            rolUsuarioRepository.deleteAll(existentes);
            assignRoles(usuario, request.roles());
        }

        return AutenticacionMapper.toEmpleadoResponse(empleado);
    }

    @Transactional
    public void delete(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Empleado no encontrado"));

        Usuario usuario = empleado.getUsuario();
        empleadoRepository.delete(empleado);

        if (usuario != null) {
            List<RolUsuario> roles = rolUsuarioRepository.findByUsuario_IdUsuario(usuario.getIdUsuario());
            rolUsuarioRepository.deleteAll(roles);
            usuarioRepository.delete(usuario);
        }
    }

    private void validateUniqueFields(EmpleadoRequest request, Usuario usuarioActual, Empleado empleadoActual) {
        if (usuarioActual == null || !request.ci().equals(usuarioActual.getCi())) {
            if (usuarioRepository.existsByCi(request.ci())) {
                throw new ResponseStatusException(CONFLICT, "CI ya registrado");
            }
        }

        if (request.correo() != null && !request.correo().isBlank()) {
            boolean correoCambiado = usuarioActual == null || !request.correo().equalsIgnoreCase(usuarioActual.getCorreo());
            if (correoCambiado && usuarioRepository.existsByCorreo(request.correo())) {
                throw new ResponseStatusException(CONFLICT, "Correo ya registrado");
            }
        }

        String username = resolveUsername(request);
        boolean usernameCambio = usuarioActual == null || !username.equalsIgnoreCase(usuarioActual.getUsername());
        if (usernameCambio && usuarioRepository.existsByUsername(username)) {
            throw new ResponseStatusException(CONFLICT, "Username ya registrado");
        }

        String codigoEmpleado = resolveCodigoEmpleado(request, empleadoActual != null ? empleadoActual.getCodigoEmpleado() : null);
        boolean codigoCambio = empleadoActual == null || !codigoEmpleado.equalsIgnoreCase(empleadoActual.getCodigoEmpleado());
        if (codigoCambio && empleadoRepository.existsByCodigoEmpleado(codigoEmpleado)) {
            throw new ResponseStatusException(CONFLICT, "Código de empleado ya registrado");
        }
    }

    private void assignRoles(Usuario usuario, List<Long> rolIds) {
        if (rolIds != null && !rolIds.isEmpty()) {
            for (Long idRol : rolIds) {
                Rol rol = rolRepository.findById(idRol)
                        .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Rol no encontrado: " + idRol));
                rolUsuarioRepository.save(RolUsuario.builder()
                        .usuario(usuario)
                        .rol(rol)
                        .activo(true)
                        .build());
            }
            return;
        }

        rolRepository.findByNombre("USER")
                .ifPresent(rol -> rolUsuarioRepository.save(RolUsuario.builder()
                        .usuario(usuario)
                        .rol(rol)
                        .activo(true)
                        .build()));
    }

    private String resolveUsername(EmpleadoRequest request) {
        if (request.username() != null && !request.username().isBlank()) {
            return request.username().trim();
        }
        return request.ci().trim();
    }

    private String resolvePassword(String password) {
        return (password != null && !password.isBlank()) ? password : "Cambio123!";
    }

    private String resolveCodigoEmpleado(EmpleadoRequest request, String currentCode) {
        if (request.codigoEmpleado() != null && !request.codigoEmpleado().isBlank()) {
            return request.codigoEmpleado().trim();
        }
        if (currentCode != null && !currentCode.isBlank()) {
            return currentCode;
        }
        return ("EMP-" + request.ci().trim()).replaceAll("\\s+", "");
    }
}
