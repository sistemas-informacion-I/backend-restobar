package org.restobar.gaira.modulo_comercial.controller.notaVenta;

import java.time.LocalDate;
import java.util.List;

import org.restobar.gaira.modulo_comercial.dto.notaVenta.NotaVentaRequestDTO;
import org.restobar.gaira.modulo_comercial.dto.notaVenta.NotaVentaResponseDTO;
import org.restobar.gaira.modulo_comercial.entity.NotaVenta.Estado;
import org.restobar.gaira.modulo_comercial.service.notaVenta.NotaVentaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/notas-venta")
@RequiredArgsConstructor
public class NotaVentaController {

    private final NotaVentaService notaVentaService;

    @GetMapping
    @PreAuthorize("hasAuthority('ventas:read')")
    public ResponseEntity<List<NotaVentaResponseDTO>> findAll(
            @RequestParam(required = false) Long idCliente,
            @RequestParam(required = false) Long idSucursal,
            @RequestParam(required = false) Estado estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        return ResponseEntity.ok(notaVentaService.findAll(idCliente, idSucursal, estado, fechaDesde, fechaHasta));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ventas:read')")
    public ResponseEntity<NotaVentaResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(notaVentaService.findById(id));
    }

    @GetMapping("/cliente/{idCliente}")
    @PreAuthorize("hasAuthority('ventas:read')")
    public ResponseEntity<List<NotaVentaResponseDTO>> findByCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(notaVentaService.findByCliente(idCliente));
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAuthority('ventas:read')")
    public ResponseEntity<List<NotaVentaResponseDTO>> findByEstado(@PathVariable Estado estado) {
        return ResponseEntity.ok(notaVentaService.findByEstado(estado));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ventas:create')")
    public ResponseEntity<NotaVentaResponseDTO> create(
            @Valid @RequestBody NotaVentaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notaVentaService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ventas:update')")
    public ResponseEntity<NotaVentaResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody NotaVentaRequestDTO request) {
        return ResponseEntity.ok(notaVentaService.update(id, request));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('ventas:update')")
    public ResponseEntity<NotaVentaResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam Estado estado) {
        return ResponseEntity.ok(notaVentaService.cambiarEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ventas:delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notaVentaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
