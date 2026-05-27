package org.restobar.gaira.modulo_inventario.controller.lote;

import org.restobar.gaira.modulo_inventario.dto.lote.LoteRequest;
import org.restobar.gaira.modulo_inventario.dto.lote.LoteResponse;
import org.restobar.gaira.modulo_inventario.entity.LoteInventario.EstadoLote;
import org.restobar.gaira.modulo_inventario.service.lote.LoteInventarioService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventario/lotes")
@RequiredArgsConstructor
public class LoteInventarioController {

    private final LoteInventarioService loteInventarioService;

    @PostMapping
    @PreAuthorize("hasAuthority('inventario:update')")
    public ResponseEntity<LoteResponse> agregarLote(@Valid @RequestBody LoteRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loteInventarioService.agregarLote(dto));
    }

    @PostMapping("/{idLote}/estado")
    @PreAuthorize("hasAuthority('inventario:update')")
    public ResponseEntity<LoteResponse> actualizarEstadoLote(
            @PathVariable Long idLote,
            @RequestBody EstadoLote nuevoEstado) {
        if (nuevoEstado == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nuevo estado del lote es obligatorio");
        }
        return ResponseEntity.ok(loteInventarioService.actualizarEstadoLote(idLote, nuevoEstado));
    }

    @GetMapping("/stock/{idStock}")
    @PreAuthorize("hasAuthority('inventario:read')")
    public ResponseEntity<Page<LoteResponse>> listarLotesPorStock(
            @PathVariable Long idStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(loteInventarioService.listarLotesPorStock(idStock, page, size));
    }
}
