package org.restobar.gaira.modulo_inventario.controller.inventario;

import java.util.List;

import org.restobar.gaira.modulo_inventario.dto.inventario.InventarioRequest;
import org.restobar.gaira.modulo_inventario.dto.inventario.InventarioResponse;
import org.restobar.gaira.modulo_inventario.service.inventario.InventarioService;
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
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping
    @PreAuthorize("hasAuthority('inventario:read')")
    public ResponseEntity<List<InventarioResponse>> listar() {
        return ResponseEntity.ok(inventarioService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('inventario:read')")
    public ResponseEntity<InventarioResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('inventario:create')")
    public ResponseEntity<InventarioResponse> crear(@Valid @RequestBody InventarioRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.crear(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('inventario:update')")
    public ResponseEntity<InventarioResponse> actualizar(@PathVariable Long id, @Valid @RequestBody InventarioRequest dto) {
        return ResponseEntity.ok(inventarioService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('inventario:delete')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inventarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
