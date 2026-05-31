package org.restobar.gaira.modulo_comercial.controller;

import java.util.List;

import org.restobar.gaira.modulo_comercial.dto.categoria.CategoriaCreate;
import org.restobar.gaira.modulo_comercial.dto.categoria.CategoriaResponse;
import org.restobar.gaira.modulo_comercial.dto.categoria.CategoriaUpdate;
import org.restobar.gaira.modulo_comercial.service.CategoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    // GET /api/categorias?nombre=xxx  — lista todas (con filtro opcional por nombre)
    @GetMapping
    @PreAuthorize("hasAuthority('categories:read')")
    public ResponseEntity<List<CategoriaResponse>> findAll(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(categoriaService.findAll(nombre));
    }

    // GET /api/categorias/raices  — solo categorías raíz (sin padre)
    @GetMapping("/raices")
    @PreAuthorize("hasAuthority('categories:read')")
    public ResponseEntity<List<CategoriaResponse>> findRoots() {
        return ResponseEntity.ok(categoriaService.findRoots());
    }

    // GET /api/categorias/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('categories:read')")
    public ResponseEntity<CategoriaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.findById(id));
    }

    // GET /api/categorias/{id}/hijos  — subcategorías directas de un padre
    @GetMapping("/{id}/hijos")
    @PreAuthorize("hasAuthority('categories:read')")
    public ResponseEntity<List<CategoriaResponse>> findChildren(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.findChildren(id));
    }

    // POST /api/categorias
    @PostMapping
    @PreAuthorize("hasAuthority('categories:create')")
    public ResponseEntity<CategoriaResponse> create(
            @Valid @RequestBody CategoriaCreate request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoriaService.create(request));
    }

    // PUT /api/categorias/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('categories:update')")
    public ResponseEntity<CategoriaResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaUpdate request) {
        return ResponseEntity.ok(categoriaService.update(id, request));
    }

    // PATCH /api/categorias/{id}/desactivar
    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAuthority('categories:update')")
    public ResponseEntity<CategoriaResponse> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.desactivar(id));
    }
    // PATCH /api/categorias/{id}/activar
    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAuthority('categories:update')")
    public ResponseEntity<CategoriaResponse> activar(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.activar(id));
    }
}
