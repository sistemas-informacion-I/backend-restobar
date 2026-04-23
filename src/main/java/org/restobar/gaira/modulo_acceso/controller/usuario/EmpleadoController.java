package org.restobar.gaira.modulo_acceso.controller.usuario;

import java.util.List;

import org.restobar.gaira.modulo_acceso.dto.usuario.EmpleadoRequest;
import org.restobar.gaira.modulo_acceso.dto.usuario.EmpleadoResponse;
import org.restobar.gaira.modulo_acceso.service.usuario.EmpleadoService;
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
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('employees:read')")
    public ResponseEntity<List<EmpleadoResponse>> findAll() {
        return ResponseEntity.ok(empleadoService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('employees:read')")
    public ResponseEntity<EmpleadoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(empleadoService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('employees:create')")
    public ResponseEntity<EmpleadoResponse> create(@Valid @RequestBody EmpleadoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('employees:update')")
    public ResponseEntity<EmpleadoResponse> update(@PathVariable Long id, @Valid @RequestBody EmpleadoRequest request) {
        return ResponseEntity.ok(empleadoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('employees:delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        empleadoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
