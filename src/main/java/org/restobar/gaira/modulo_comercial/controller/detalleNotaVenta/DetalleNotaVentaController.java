package org.restobar.gaira.modulo_comercial.controller.detalleNotaVenta;

import java.util.List;

import org.restobar.gaira.modulo_comercial.dto.detalleNotaVenta.DetalleNotaVentaResponse;
import org.restobar.gaira.modulo_comercial.service.detalleNotaVenta.DetalleNotaVentaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/detalles-nota-venta")
@RequiredArgsConstructor
public class DetalleNotaVentaController {

    private final DetalleNotaVentaService detalleNotaVentaService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ventas:read')")
    public ResponseEntity<DetalleNotaVentaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(detalleNotaVentaService.findById(id));
    }

    @GetMapping("/nota-venta/{idNotaVenta}")
    @PreAuthorize("hasAuthority('ventas:read')")
    public ResponseEntity<List<DetalleNotaVentaResponse>> findByNotaVentaId(@PathVariable Long idNotaVenta) {
        return ResponseEntity.ok(detalleNotaVentaService.findByNotaVentaId(idNotaVenta));
    }
}
	