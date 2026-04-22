package org.restobar.gaira.modulo_acceso.dto.auth;

import lombok.Builder;
import org.restobar.gaira.modulo_acceso.dto.usuario.UsuarioResponse;

import java.util.List;

@Builder
public record AuthResponse(
                String accessToken,
                String refreshToken,
                UsuarioResponse user,
                String username,
                List<String> roles) {
}
