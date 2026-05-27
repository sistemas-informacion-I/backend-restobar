package org.restobar.gaira.modulo_comercial.controller.producto;

import java.util.List;
import org.restobar.gaira.modulo_comercial.dto.producto.ProductoRequest;
import org.restobar.gaira.modulo_comercial.dto.producto.ProductoResponse;
import org.restobar.gaira.modulo_comercial.service.producto.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    @PreAuthorize("hasAuthority('producto:read')")
    public ResponseEntity<List<ProductoResponse>> listar(
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) Boolean activo) {
        return ResponseEntity.ok(productoService.listarTodos(idCategoria, activo));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('producto:read')")
    public ResponseEntity<ProductoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('producto:create')")
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('producto:update')")
    public ResponseEntity<ProductoResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest dto) {
        return ResponseEntity.ok(productoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('producto:delete')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/imagen", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('producto:update')")
    public ResponseEntity<ProductoResponse> subirImagen(
            @PathVariable Long id,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        return ResponseEntity.ok(productoService.subirImagen(id, file));
    }
}
