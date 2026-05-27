package org.restobar.gaira.modulo_inventario.controller.receta;

import java.util.List;
import org.restobar.gaira.modulo_inventario.dto.receta.RecetaCostoResponse;
import org.restobar.gaira.modulo_inventario.dto.receta.RecetaCreate;
import org.restobar.gaira.modulo_inventario.dto.receta.RecetaDuplicarRequest;
import org.restobar.gaira.modulo_inventario.dto.receta.RecetaResponse;
import org.restobar.gaira.modulo_inventario.dto.receta.RecetaUpdate;
import org.restobar.gaira.modulo_inventario.service.receta.RecetaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recetas")
@RequiredArgsConstructor
public class RecetaController {

    private final RecetaService recetaService;

    @GetMapping
    @PreAuthorize("hasAuthority('receta:read')")
    public ResponseEntity<List<RecetaResponse>> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) Long idProductoFinal) {
        return ResponseEntity.ok(recetaService.findAll(nombre, activo, idProductoFinal));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('receta:read')")
    public ResponseEntity<RecetaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(recetaService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('receta:create')")
    public ResponseEntity<RecetaResponse> crear(@Valid @RequestBody RecetaCreate dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recetaService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('receta:update')")
    public ResponseEntity<RecetaResponse> actualizar(@PathVariable Long id, @Valid @RequestBody RecetaUpdate dto) {
        return ResponseEntity.ok(recetaService.update(id, dto));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAuthority('receta:update')")
    public ResponseEntity<RecetaResponse> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(recetaService.desactivar(id));
    }

    @PostMapping("/{id}/duplicar")
    @PreAuthorize("hasAuthority('receta:create')")
    public ResponseEntity<RecetaResponse> duplicar(@PathVariable Long id, @Valid @RequestBody RecetaDuplicarRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recetaService.duplicar(id, dto));
    }

    @PostMapping("/{id}/recalcular-costo")
    @PreAuthorize("hasAuthority('receta:update')")
    public ResponseEntity<RecetaCostoResponse> recalcularCosto(
            @PathVariable Long id,
            @RequestParam Long idSucursal) {
        return ResponseEntity.ok(recetaService.recalcularCosto(id, idSucursal));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('receta:delete')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        recetaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
