package org.restobar.gaira.acceso.controller.auth;

import java.util.Map;

import org.restobar.gaira.acceso.dto.auth.AuthLoginRequest;
import org.restobar.gaira.acceso.dto.auth.AuthRegisterRequest;
import org.restobar.gaira.acceso.dto.auth.AuthResponse;
import org.restobar.gaira.acceso.dto.auth.ChangePasswordRequest;
import org.restobar.gaira.acceso.dto.auth.ProfileResponse;
import org.restobar.gaira.acceso.dto.auth.RefreshTokenRequest;
import org.restobar.gaira.acceso.dto.auth.UpdatePerfilRequest;
import org.restobar.gaira.acceso.dto.usuario.UsuarioResponse;
import org.restobar.gaira.acceso.entity.Usuario;
import org.restobar.gaira.acceso.mapper.AutenticacionMapper;
import org.restobar.gaira.security.userdetails.ApplicationUserPrincipal;
import org.restobar.gaira.acceso.service.auth.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRegisterRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request, httpRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthLoginRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.login(request, httpRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.refreshToken(request, httpRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileResponse> me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof ApplicationUserPrincipal principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = authService.getUserById(principal.getIdUsuario());
        UsuarioResponse usuarioResponse = AutenticacionMapper.toUsuarioResponse(usuario);
        
        // Extraer las authorities del principal
        java.util.List<String> authorities = principal.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .toList();
        
        return ResponseEntity.ok(new ProfileResponse(usuarioResponse, authorities));
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileResponse> updateProfile(
            @Valid @RequestBody UpdatePerfilRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest) {
        if (authentication == null || !(authentication.getPrincipal() instanceof ApplicationUserPrincipal principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = authService.updateProfile(principal.getIdUsuario(), request, httpRequest);
        UsuarioResponse usuarioResponse = AutenticacionMapper.toUsuarioResponse(usuario);
        
        java.util.List<String> authorities = principal.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .toList();
        
        return ResponseEntity.ok(new ProfileResponse(usuarioResponse, authorities));
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest) {
        if (authentication == null || !(authentication.getPrincipal() instanceof ApplicationUserPrincipal principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        authService.changePassword(principal.getIdUsuario(), request, httpRequest);
        
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
    }
}
