package org.restobar.gaira.acceso.controller.usuario;

import java.util.List;

import org.restobar.gaira.acceso.dto.usuario.UsuarioRequest;
import org.restobar.gaira.acceso.dto.usuario.UsuarioResponse;
import org.restobar.gaira.acceso.service.usuario.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<List<UsuarioResponse>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<UsuarioResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('users:create')")
    public ResponseEntity<UsuarioResponse> create(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('users:update')")
    public ResponseEntity<UsuarioResponse> update(@PathVariable Long id,
            @Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.update(id, request));
    }

    @PatchMapping("/{id}/bloquear")
    @PreAuthorize("hasAuthority('users:update')")
    public ResponseEntity<UsuarioResponse> bloquear(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.bloquear(id));
    }

    @PatchMapping("/{id}/desbloquear")
    @PreAuthorize("hasAuthority('users:update')")
    public ResponseEntity<UsuarioResponse> desbloquear(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.desbloquear(id));
    }

    @PatchMapping("/{id}/suspender")
    @PreAuthorize("hasAuthority('users:update')")
    public ResponseEntity<UsuarioResponse> suspender(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.suspender(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('users:delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
