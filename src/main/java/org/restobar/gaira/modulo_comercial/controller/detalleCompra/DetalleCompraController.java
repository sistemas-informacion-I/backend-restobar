package org.restobar.gaira.modulo_comercial.controller.detalleCompra;

import java.util.List;

import org.restobar.gaira.modulo_comercial.dto.detalleCompra.DetalleCompraResponse;
import org.restobar.gaira.modulo_comercial.service.detalleCompra.DetalleCompraService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/detallescompra")
@RequiredArgsConstructor
public class DetalleCompraController {

    private final DetalleCompraService detalleCompraService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('compras:read')")
    public ResponseEntity<DetalleCompraResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(detalleCompraService.findById(id));
    }

    @GetMapping("/compra/{idCompra}")
    @PreAuthorize("hasAuthority('compras:read')")
    public ResponseEntity<List<DetalleCompraResponse>> findByCompraId(@PathVariable Long idCompra) {
        return ResponseEntity.ok(detalleCompraService.findByCompraId(idCompra));
    }
}
