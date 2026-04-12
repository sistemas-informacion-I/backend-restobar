package org.restobar.gaira.modulo_acceso.controller.permiso;

import java.util.List;

import org.restobar.gaira.modulo_acceso.dto.permiso.PermisoCreate;
import org.restobar.gaira.modulo_acceso.dto.permiso.PermisoUpdate;
import org.restobar.gaira.modulo_acceso.dto.permiso.PermisoResponse;
import org.restobar.gaira.modulo_acceso.service.permiso.PermisoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/permisos")
public class PermisoController {

    private final PermisoService permisoService;

    public PermisoController(PermisoService permisoService) {
        this.permisoService = permisoService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('permissions:read')")
    public ResponseEntity<List<PermisoResponse>> findAll() {
        return ResponseEntity.ok(permisoService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('permissions:read')")
    public ResponseEntity<PermisoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(permisoService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('permissions:create')")
    public ResponseEntity<PermisoResponse> create(@Valid @RequestBody PermisoCreate request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(permisoService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('permissions:update')")
    public ResponseEntity<PermisoResponse> update(@PathVariable Long id,
            @Valid @RequestBody PermisoUpdate request) {
        return ResponseEntity.ok(permisoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('permissions:delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        permisoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
