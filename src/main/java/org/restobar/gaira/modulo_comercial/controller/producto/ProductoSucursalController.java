package org.restobar.gaira.modulo_comercial.controller.producto;

import java.util.List;
import org.restobar.gaira.modulo_comercial.dto.producto.ProductoSucursalRequest;
import org.restobar.gaira.modulo_comercial.dto.producto.ProductoSucursalResponse;
import org.restobar.gaira.modulo_comercial.service.producto.ProductoSucursalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoSucursalController {

    private final ProductoSucursalService productoSucursalService;

    @GetMapping("/sucursal/{idSucursal}")
    @PreAuthorize("hasAuthority('producto:read')")
    public ResponseEntity<List<ProductoSucursalResponse>> listarPorSucursal(@PathVariable Long idSucursal) {
        return ResponseEntity.ok(productoSucursalService.listarPorSucursal(idSucursal));
    }

    @GetMapping("/{idProducto}/sucursal/{idSucursal}")
    @PreAuthorize("hasAuthority('producto:read')")
    public ResponseEntity<ProductoSucursalResponse> obtenerEnSucursal(
            @PathVariable Long idProducto,
            @PathVariable Long idSucursal) {
        return ResponseEntity.ok(productoSucursalService.obtenerPorProductoYSucursal(idProducto, idSucursal));
    }

    @PostMapping("/{idProducto}/asignar-sucursal")
    @PreAuthorize("hasAuthority('producto:create')")
    public ResponseEntity<ProductoSucursalResponse> asignarASucursal(
            @PathVariable Long idProducto,
            @Valid @RequestBody ProductoSucursalRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoSucursalService.asignarASucursal(idProducto, dto));
    }

    @PutMapping("/{idProducto}/sucursal/{idSucursal}")
    @PreAuthorize("hasAuthority('producto:update')")
    public ResponseEntity<ProductoSucursalResponse> actualizarEnSucursal(
            @PathVariable Long idProducto,
            @PathVariable Long idSucursal,
            @Valid @RequestBody ProductoSucursalRequest dto) {
        return ResponseEntity.ok(productoSucursalService.actualizarEnSucursal(idProducto, idSucursal, dto));
    }

    @DeleteMapping("/{idProducto}/sucursal/{idSucursal}")
    @PreAuthorize("hasAuthority('producto:delete')")
    public ResponseEntity<Void> removerDeSucursal(
            @PathVariable Long idProducto,
            @PathVariable Long idSucursal) {
        productoSucursalService.removerDeSucursal(idProducto, idSucursal);
        return ResponseEntity.noContent().build();
    }
}
