package org.restobar.gaira.modulo_acceso.controller.auth;

import java.util.Map;

import org.restobar.gaira.modulo_acceso.dto.auth.AuthRegister;
import org.restobar.gaira.modulo_acceso.dto.auth.AuthLogin;
import org.restobar.gaira.modulo_acceso.dto.auth.AuthResponse;
import org.restobar.gaira.modulo_acceso.dto.auth.RefreshToken;
import org.restobar.gaira.security.userdetails.ApplicationUserPrincipal;
import org.restobar.gaira.modulo_acceso.service.auth.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRegister request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request, httpRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthLogin request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.login(request, httpRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshToken request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.refreshToken(request, httpRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshToken request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof ApplicationUserPrincipal principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(Map.of(
                "idUsuario", principal.getIdUsuario(),
                "username", principal.getUsername(),
                "email", principal.getEmail(),
                "authorities", principal.getAuthorities().stream().map(a -> a.getAuthority()).toList()));
    }
}
