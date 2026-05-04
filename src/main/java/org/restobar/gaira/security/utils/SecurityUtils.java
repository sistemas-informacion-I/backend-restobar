package org.restobar.gaira.security.utils;

import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_acceso.repository.UsuarioRepository;
import org.restobar.gaira.security.userdetails.ApplicationUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private final UsuarioRepository usuarioRepository;
    private final org.restobar.gaira.modulo_operaciones.repository.EmpleadoSucursalRepository empleadoSucursalRepository;

    public SecurityUtils(UsuarioRepository usuarioRepository, 
                        org.restobar.gaira.modulo_operaciones.repository.EmpleadoSucursalRepository empleadoSucursalRepository) {
        this.usuarioRepository = usuarioRepository;
        this.empleadoSucursalRepository = empleadoSucursalRepository;
    }

    /**
     * Obtiene el usuario actual autenticado desde el contexto de seguridad.
     * @return Usuario autenticado o null si no hay sesión o no se encuentra en BD.
     */
    public Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        
        if (principal instanceof ApplicationUserPrincipal userPrincipal) {
            return usuarioRepository.findById(userPrincipal.getIdUsuario()).orElse(null);
        }

        return null;
    }

    /**
     * Obtiene el ID del usuario actual.
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof ApplicationUserPrincipal principal) {
            return principal.getIdUsuario();
        }
        return null;
    }

    /**
     * Obtiene el ID de la sucursal actual para el usuario autenticado.
     * Útil para filtrar datos en modo Administrador.
     */
    public Long getCurrentSucursalId() {
        Long idUsuario = getCurrentUserId();
        if (idUsuario == null) return null;

        // Buscamos la sucursal activa del empleado asociado al usuario
        return empleadoSucursalRepository.findByEmpleado_Usuario_IdUsuario(idUsuario).stream()
                .filter(es -> Boolean.TRUE.equals(es.getActivo()) && es.getFechaFin() == null)
                .map(es -> es.getSucursal().getIdSucursal())
                .findFirst()
                .orElse(null);
    }
}
