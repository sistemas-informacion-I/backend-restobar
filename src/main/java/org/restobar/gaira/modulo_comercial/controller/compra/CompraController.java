package org.restobar.gaira.modulo_comercial.controller.compra;

import java.time.LocalDate;
import java.util.List;

import org.restobar.gaira.modulo_comercial.dto.compra.CompraRequestDTO;
import org.restobar.gaira.modulo_comercial.dto.compra.CompraResponseDTO;
import org.restobar.gaira.modulo_comercial.entity.Compra.EstadoPago;
import org.restobar.gaira.modulo_comercial.service.compra.CompraService;
import org.restobar.gaira.security.userdetails.ApplicationUserPrincipal;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;

    @GetMapping
    @PreAuthorize("hasAuthority('compras:read')")
    public ResponseEntity<List<CompraResponseDTO>> findAll(
            @RequestParam(required = false) String nroFactura,
            @RequestParam(required = false) Long idProveedor,
            @RequestParam(required = false) EstadoPago estadoPago,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        return ResponseEntity.ok(compraService.findAll(nroFactura, idProveedor, estadoPago, fechaDesde, fechaHasta));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('compras:read')")
    public ResponseEntity<CompraResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(compraService.findById(id));
    }

    @GetMapping("/proveedor/{idProveedor}")
    @PreAuthorize("hasAuthority('compras:read')")
    public ResponseEntity<List<CompraResponseDTO>> findByProveedor(@PathVariable Long idProveedor) {
        return ResponseEntity.ok(compraService.findByProveedor(idProveedor));
    }

    @GetMapping("/estado/{estadoPago}")
    @PreAuthorize("hasAuthority('compras:read')")
    public ResponseEntity<List<CompraResponseDTO>> findByEstadoPago(@PathVariable EstadoPago estadoPago) {
        return ResponseEntity.ok(compraService.findByEstadoPago(estadoPago));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('compras:create')")
    public ResponseEntity<CompraResponseDTO> create(
            @Valid @RequestBody CompraRequestDTO request,
            @AuthenticationPrincipal ApplicationUserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(compraService.create(request, principal.getIdUsuario()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('compras:update')")
    public ResponseEntity<CompraResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CompraRequestDTO request) {
        return ResponseEntity.ok(compraService.update(id, request));
    }

    @PatchMapping("/{id}/estado-pago")
    @PreAuthorize("hasAuthority('compras:update')")
    public ResponseEntity<CompraResponseDTO> cambiarEstadoPago(
            @PathVariable Long id,
            @RequestParam EstadoPago estadoPago) {
        return ResponseEntity.ok(compraService.cambiarEstadoPago(id, estadoPago));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('compras:delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        compraService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
