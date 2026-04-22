package org.restobar.gaira.acceso.dto.auth;

import org.restobar.gaira.acceso.dto.usuario.UsuarioResponse;
import java.util.List;

/**
 * DTO para devolver la información completa del perfil del usuario autenticado.
 * Incluye la información del usuario y sus permisos/authorities.
 */
public record ProfileResponse(
        UsuarioResponse usuario,
        List<String> authorities
) {
}
