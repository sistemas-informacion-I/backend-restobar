package org.restobar.gaira.modulo_electronico.controller.catalogo;

import java.util.List;

import org.restobar.gaira.modulo_electronico.dto.catalogo.CatalogoProductoResponse;
import org.restobar.gaira.modulo_electronico.dto.catalogo.CatalogoUpdateRequest;
import org.restobar.gaira.modulo_electronico.service.catalogo.CatalogoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/catalogo")
@RequiredArgsConstructor
public class CatalogoController {

    private final CatalogoService catalogoService;

    // ─── Vista Cliente (pública) ──────────────────────────────────────────────

    /**
     * GET /api/catalogo/sucursal/{idSucursal}
     * Catálogo público para el cliente: solo productos disponibles con stock.
     * Soporta filtros por texto y categoría.
     */
    @GetMapping("/sucursal/{idSucursal}")
    public ResponseEntity<List<CatalogoProductoResponse>> getCatalogo(
            @PathVariable Long idSucursal,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Long idCategoria) {
        return ResponseEntity.ok(catalogoService.getCatalogoPorSucursal(idSucursal, busqueda, idCategoria));
    }

    // ─── Vista Admin ──────────────────────────────────────────────────────────

    /**
     * GET /api/catalogo/admin/sucursal/{idSucursal}
     * Lista todos los productos de la sucursal (activos e inactivos) para gestión.
     */
    @GetMapping("/admin/sucursal/{idSucursal}")
    @PreAuthorize("hasAuthority('catalogo:read')")
    public ResponseEntity<List<CatalogoProductoResponse>> getCatalogoAdmin(
            @PathVariable Long idSucursal) {
        return ResponseEntity.ok(catalogoService.getCatalogoAdmin(idSucursal));
    }

    /**
     * PATCH /api/catalogo/admin/sucursal/{idSucursal}/producto/{idProducto}
     * Actualiza precio y disponibilidad de un producto en la sucursal.
     * Si el producto no tiene stock, no se puede activar disponibilidad.
     */
    @PatchMapping("/admin/sucursal/{idSucursal}/producto/{idProducto}")
    @PreAuthorize("hasAuthority('catalogo:update')")
    public ResponseEntity<CatalogoProductoResponse> actualizarDisponibilidad(
            @PathVariable Long idSucursal,
            @PathVariable Long idProducto,
            @Valid @RequestBody CatalogoUpdateRequest request) {
        return ResponseEntity.ok(catalogoService.actualizarDisponibilidad(idProducto, idSucursal, request));
    }
}
