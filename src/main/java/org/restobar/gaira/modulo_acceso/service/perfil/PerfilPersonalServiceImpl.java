package org.restobar.gaira.modulo_acceso.service.perfil;

import lombok.RequiredArgsConstructor;
import org.restobar.gaira.modulo_acceso.dto.perfil.CambioPasswordRequest;
import org.restobar.gaira.modulo_acceso.dto.perfil.PerfilPersonalResponse;
import org.restobar.gaira.modulo_acceso.dto.perfil.PerfilPersonalUpdate;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_acceso.mapper.perfil.PerfilPersonalMapper;
import org.restobar.gaira.modulo_acceso.repository.PerfilPersonalRepository;
import org.restobar.gaira.security.audit.annotation.Auditable;
import org.restobar.gaira.security.audit.util.AuditableService;
import org.restobar.gaira.security.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PerfilPersonalServiceImpl implements PerfilPersonalService, AuditableService<Long, Object> {

    private final PerfilPersonalRepository repository;
    private final PerfilPersonalMapper mapper;
    private final SecurityUtils securityUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Object getEntity(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof Usuario u) {
            return mapper.toAuditMap(u);
        } else if (entity instanceof PerfilPersonalResponse ppr) {
            return mapper.toAuditMap(ppr);
        }
        return Map.of();
    }

    private Usuario getAuthenticatedUsuario() {
        Long id = securityUtils.getCurrentUserId();
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No hay usuario autenticado en el contexto actual");
        }
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public PerfilPersonalResponse obtenerMiPerfil() {
        return mapper.toResponse(getAuthenticatedUsuario());
    }

    @Override
    @Transactional
    @Auditable(tabla = "usuario", operacion = "UPDATE")
    public PerfilPersonalResponse actualizarMiPerfil(PerfilPersonalUpdate update) {
        Usuario usuario = getAuthenticatedUsuario();
        
        usuario.setNombre(update.nombre());
        usuario.setApellido(update.apellido());
        usuario.setTelefono(update.telefono());
        usuario.setSexo(update.sexo());
        usuario.setCorreo(update.correo());
        usuario.setDireccion(update.direccion());

        usuario = repository.save(usuario);
        return mapper.toResponse(usuario);
    }

    @Override
    @Transactional
    @Auditable(tabla = "usuario", operacion = "UPDATE") // Cambio de password
    public void cambiarMiPassword(CambioPasswordRequest request) {
        Usuario usuario = getAuthenticatedUsuario();
        
        if (!passwordEncoder.matches(request.passwordActual(), usuario.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña actual proporcionada es incorrecta");
        }
        
        usuario.setPasswordHash(passwordEncoder.encode(request.passwordNuevo()));
        repository.save(usuario);
    }

    @Override
    @Transactional
    @Auditable(tabla = "usuario", operacion = "UPDATE") // Baja logica
    public void eliminarMiPerfil() {
        Usuario usuario = getAuthenticatedUsuario();
        usuario.setActivo(false);
        // Borrado logico
        repository.save(usuario);
    }
}

