package org.restobar.gaira.modulo_acceso.controller.usuario;

import java.util.List;

import org.restobar.gaira.modulo_acceso.dto.usuario.ProveedorRequest;
import org.restobar.gaira.modulo_acceso.dto.usuario.ProveedorResponse;
import org.restobar.gaira.modulo_acceso.service.usuario.ProveedorService;
import org.restobar.gaira.security.userdetails.ApplicationUserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping
    @PreAuthorize("hasAuthority('providers:read')")
    public ResponseEntity<List<ProveedorResponse>> findAll(
            @RequestParam(required = false) String empresa,
            @RequestParam(required = false) String nit,
            @RequestParam(required = false) String categoria) {
        return ResponseEntity.ok(proveedorService.findAll(empresa, nit, categoria));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('providers:read')")
    public ResponseEntity<ProveedorResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('providers:create')")
    public ResponseEntity<ProveedorResponse> create(
            @Valid @RequestBody ProveedorRequest request,
            @AuthenticationPrincipal ApplicationUserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(proveedorService.create(request, principal.getIdUsuario()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('providers:update')")
    public ResponseEntity<ProveedorResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProveedorRequest request) {
        return ResponseEntity.ok(proveedorService.update(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAuthority('providers:update')")
    public ResponseEntity<ProveedorResponse> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.desactivar(id));
    }
}
