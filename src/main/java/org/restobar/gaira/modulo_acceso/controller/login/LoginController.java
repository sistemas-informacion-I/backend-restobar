package org.restobar.gaira.modulo_acceso.controller.login;

import java.util.Map;

import org.restobar.gaira.modulo_acceso.dto.login.LoginRequest;
import org.restobar.gaira.modulo_acceso.dto.login.LoginResponse;
import org.restobar.gaira.modulo_acceso.dto.login.RefreshTokenRequest;
import org.restobar.gaira.modulo_acceso.dto.login.SendCodeRequest;
import org.restobar.gaira.modulo_acceso.dto.login.VerifyCodeRequest;
import org.restobar.gaira.modulo_acceso.service.login.LoginService;
import org.restobar.gaira.security.userdetails.ApplicationUserPrincipal;
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
@RequestMapping("/login")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(loginService.login(request, httpRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(loginService.refreshToken(request, httpRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request, HttpServletRequest httpRequest) {
        loginService.logout(request, httpRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot/send-code")
    public ResponseEntity<Void> sendResetCode(@Valid @RequestBody SendCodeRequest request, HttpServletRequest httpRequest) {
        loginService.sendResetCode(request, httpRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot/verify")
    public ResponseEntity<Void> verifyResetCode(@Valid @RequestBody VerifyCodeRequest request, HttpServletRequest httpRequest) {
        loginService.verifyAndResetPassword(request, httpRequest);
        return ResponseEntity.ok().build();
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
                "tipoUsuario", principal.getTipoUsuario(),
                "sucursalId", principal.getSucursalId() != null ? principal.getSucursalId() : "",
                "authorities", principal.getAuthorities().stream().map(a -> a.getAuthority()).toList()));
    }
}
