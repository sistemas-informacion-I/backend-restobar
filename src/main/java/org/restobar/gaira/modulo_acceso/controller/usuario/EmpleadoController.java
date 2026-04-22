package org.restobar.gaira.modulo_acceso.controller.usuario;

import java.util.List;

import org.restobar.gaira.modulo_acceso.dto.usuario.EmpleadoResponse;
import org.restobar.gaira.modulo_acceso.service.usuario.EmpleadoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
