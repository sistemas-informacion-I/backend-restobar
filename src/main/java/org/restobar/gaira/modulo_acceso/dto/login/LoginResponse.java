package org.restobar.gaira.modulo_acceso.dto.login;

import lombok.Builder;
import org.restobar.gaira.modulo_acceso.dto.usuario.UsuarioResponse;

import java.util.List;

@Builder
public record LoginResponse(
                String accessToken,
                String refreshToken,
                UsuarioResponse user,
                String username,
                String tipoUsuario, // Identidad del usuario (S, E, C)
                Long sucursalId,    // ID de sucursal para empleados (E)
                List<String> roles) {
}
