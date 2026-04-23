package org.restobar.gaira.modulo_acceso.service.usuario;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.restobar.gaira.modulo_acceso.dto.usuario.EmpleadoRequest;
import org.restobar.gaira.modulo_acceso.dto.usuario.EmpleadoResponse;
import org.restobar.gaira.modulo_acceso.entity.Empleado;
import org.restobar.gaira.modulo_acceso.entity.Rol;
import org.restobar.gaira.modulo_acceso.entity.RolUsuario;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_acceso.mapper.usuario.EmpleadoMapper;
import org.restobar.gaira.modulo_acceso.repository.EmpleadoRepository;
import org.restobar.gaira.modulo_acceso.repository.RolRepository;
import org.restobar.gaira.modulo_acceso.repository.RolUsuarioRepository;
import org.restobar.gaira.modulo_acceso.repository.UsuarioRepository;
import org.restobar.gaira.security.audit.annotation.Auditable;
import org.restobar.gaira.security.audit.util.AuditableService;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class EmpleadoService implements AuditableService<Long, Object> {

    private final EmpleadoRepository empleadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final EmpleadoMapper empleadoMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Object getEntity(Long id) {
        return empleadoRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof Empleado e) {
            return empleadoMapper.toAuditMap(e);
        } else if (entity instanceof EmpleadoResponse er) {
            return empleadoMapper.toAuditMap(er);
        }
        return Map.of();
    }

    @Transactional(readOnly = true)
    public List<EmpleadoResponse> findAll() {
        return empleadoRepository.findAll().stream()
                .map(empleadoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmpleadoResponse findById(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Empleado no encontrado"));
        return empleadoMapper.toResponse(empleado);
    }

    @Transactional
    @Auditable(tabla = "empleado", operacion = "INSERT")
    public EmpleadoResponse create(EmpleadoRequest request) {
        validateUniqueFields(request, null, null);

        String username = resolveUsername(request);
        String codigoEmpleado = generateCodigoEmpleado(request.ci());

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
                .fechaContratacion(request.fechaContratacion() != null ? request.fechaContratacion() : LocalDate.now())
                .fechaFinalizacion(request.fechaFinalizacion())
                .build();

        empleado = empleadoRepository.save(empleado);
        return empleadoMapper.toResponse(empleado);
    }

    @Transactional
    @Auditable(tabla = "empleado", operacion = "UPDATE", idParamName = "id")
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

        // Si el codigo fue enviado y es diferente, lo cambiamos, si no, mantenemos el anterior
        if (request.codigoEmpleado() != null && !request.codigoEmpleado().isBlank()) {
            empleado.setCodigoEmpleado(request.codigoEmpleado().trim());
        }
        
        empleado.setSalario(request.salario());
        empleado.setTurno(request.turno());
        if (request.fechaContratacion() != null) {
            empleado.setFechaContratacion(request.fechaContratacion());
        }
        empleado.setFechaFinalizacion(request.fechaFinalizacion());
        
        empleadoRepository.save(empleado);

        if (request.roles() != null) {
            List<RolUsuario> existentes = rolUsuarioRepository.findByUsuario_IdUsuario(usuario.getIdUsuario());
            rolUsuarioRepository.deleteAll(existentes);
            assignRoles(usuario, request.roles());
        }

        return empleadoMapper.toResponse(empleado);
    }

    @Transactional
    @Auditable(tabla = "empleado", operacion = "DELETE", idParamName = "id")
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

        if (request.codigoEmpleado() != null && !request.codigoEmpleado().isBlank()) {
            boolean codigoCambio = empleadoActual == null || !request.codigoEmpleado().equalsIgnoreCase(empleadoActual.getCodigoEmpleado());
            if (codigoCambio && empleadoRepository.existsByCodigoEmpleado(request.codigoEmpleado())) {
                throw new ResponseStatusException(CONFLICT, "Código de empleado ya registrado");
            }
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

    private String generateCodigoEmpleado(String ci) {
        return ("EMP-" + ci.trim()).replaceAll("\\s+", "");
    }
}
