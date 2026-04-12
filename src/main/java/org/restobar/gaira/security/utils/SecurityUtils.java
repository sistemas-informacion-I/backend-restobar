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

    public SecurityUtils(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
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
}
