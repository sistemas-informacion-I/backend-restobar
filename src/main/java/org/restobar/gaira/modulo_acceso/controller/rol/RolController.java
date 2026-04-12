package org.restobar.gaira.modulo_acceso.controller.rol;

import java.util.List;

import org.restobar.gaira.modulo_acceso.dto.permiso.AssignPermission;
import org.restobar.gaira.modulo_acceso.dto.rol.AssignRole;
import org.restobar.gaira.modulo_acceso.dto.rol.RolCreate;
import org.restobar.gaira.modulo_acceso.dto.rol.RolResponse;
import org.restobar.gaira.modulo_acceso.dto.rol.RolUpdate;
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
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolService rolService;

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
    public ResponseEntity<RolResponse> create(@Valid @RequestBody RolCreate request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rolService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('roles:update')")
    public ResponseEntity<RolResponse> update(@PathVariable Long id, @Valid @RequestBody RolUpdate request) {
        return ResponseEntity.ok(rolService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('roles:delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rolService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/permisos")
    @PreAuthorize("hasAuthority('roles:update')")
    public ResponseEntity<Void> assignPermission(@PathVariable Long id, @Valid @RequestBody AssignPermission request) {
        rolService.assignPermission(id, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/usuarios/{idUsuario}/asignar-entidad")
    @PreAuthorize("hasAuthority('users:update')")
    public ResponseEntity<Void> assignRoleToUser(@PathVariable Long idUsuario, @Valid @RequestBody AssignRole request) {
        rolService.assignRoleToUser(idUsuario, request);
        return ResponseEntity.ok().build();
    }
}
