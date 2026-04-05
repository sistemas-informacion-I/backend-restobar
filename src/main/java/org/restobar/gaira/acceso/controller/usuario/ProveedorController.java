package org.restobar.gaira.acceso.controller.usuario;

import java.util.List;

import org.restobar.gaira.acceso.dto.usuario.ProveedorResponse;
import org.restobar.gaira.acceso.service.usuario.ProveedorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('providers:read')")
    public ResponseEntity<List<ProveedorResponse>> findAll() {
        return ResponseEntity.ok(proveedorService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('providers:read')")
    public ResponseEntity<ProveedorResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.findById(id));
    }
}
