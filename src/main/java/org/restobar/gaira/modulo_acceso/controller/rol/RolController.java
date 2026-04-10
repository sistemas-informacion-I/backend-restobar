package org.restobar.gaira.modulo_acceso.controller.rol;

import java.util.List;

import org.restobar.gaira.modulo_acceso.dto.permiso.AssignPermissionRequest;
import org.restobar.gaira.modulo_acceso.dto.rol.AssignRoleRequest;
import org.restobar.gaira.modulo_acceso.dto.rol.RolRequest;
import org.restobar.gaira.modulo_acceso.dto.rol.RolResponse;
import org.restobar.gaira.modulo_acceso.service.rol.RolService;
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
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('roles:read')")
    public ResponseEntity<List<RolResponse>> findAll() {
        return ResponseEntity.ok(rolService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('roles:read')")
    public ResponseEntity<RolResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(rolService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('roles:create')")
    public ResponseEntity<RolResponse> create(@Valid @RequestBody RolRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rolService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('roles:update')")
    public ResponseEntity<RolResponse> update(@PathVariable Long id, @Valid @RequestBody RolRequest request) {
        return ResponseEntity.ok(rolService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('roles:delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rolService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{idRol}/permisos")
    @PreAuthorize("hasAuthority('roles:update')")
    public ResponseEntity<Void> assignPermission(@PathVariable Long idRol,
            @Valid @RequestBody AssignPermissionRequest request) {
        rolService.assignPermission(idRol, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/usuarios/{idUsuario}")
    @PreAuthorize("hasAuthority('roles:update')")
    public ResponseEntity<Void> assignRoleToUser(@PathVariable Long idUsuario,
            @Valid @RequestBody AssignRoleRequest request) {
        rolService.assignRoleToUser(idUsuario, request);
        return ResponseEntity.noContent().build();
    }
}
