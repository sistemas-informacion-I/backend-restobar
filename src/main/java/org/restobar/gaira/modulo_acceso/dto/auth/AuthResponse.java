package org.restobar.gaira.modulo_acceso.dto.auth;

import org.restobar.gaira.modulo_acceso.dto.usuario.UsuarioResponse;
import java.util.List;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        UsuarioResponse usuario,
        String username,
        List<String> authorities
) {
}
