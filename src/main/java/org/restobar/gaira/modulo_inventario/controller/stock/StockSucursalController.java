package org.restobar.gaira.modulo_inventario.controller.stock;

import java.util.List;

import org.restobar.gaira.modulo_inventario.dto.lote.LoteRequest;
import org.restobar.gaira.modulo_inventario.dto.lote.LoteResponse;
import org.restobar.gaira.modulo_inventario.dto.stock.StockSucursalRequest;
import org.restobar.gaira.modulo_inventario.dto.stock.StockSucursalResponse;
import org.restobar.gaira.modulo_inventario.dto.stock.StockAjusteRequest;
import org.restobar.gaira.modulo_inventario.entity.LoteInventario.EstadoLote;
import org.restobar.gaira.modulo_inventario.service.stock.StockSucursalService;
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
@RequestMapping("/api/inventario/stock")
@RequiredArgsConstructor
public class StockSucursalController {

    private final StockSucursalService stockService;

    @GetMapping("/sucursal/{idSucursal}")
    @PreAuthorize("hasAuthority('inventario:read')")
    public ResponseEntity<List<StockSucursalResponse>> listarPorSucursal(@PathVariable Long idSucursal) {
        return ResponseEntity.ok(stockService.listarPorSucursal(idSucursal));
    }

    @PostMapping("/inicial")
    @PreAuthorize("hasAuthority('inventario:update')")
    public ResponseEntity<StockSucursalResponse> establecerStockInicial(@Valid @RequestBody StockSucursalRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.establecerStockInicial(dto));
    }

    @PostMapping("/lote")
    @PreAuthorize("hasAuthority('inventario:update')")
    public ResponseEntity<LoteResponse> agregarLote(@Valid @RequestBody LoteRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.agregarLote(dto));
    }

    @PostMapping("/lote/{idLote}/estado")
    @PreAuthorize("hasAuthority('inventario:update')")
    public ResponseEntity<LoteResponse> actualizarEstadoLote(
            @PathVariable Long idLote,
            @RequestBody EstadoLote nuevoEstado) {
        if (nuevoEstado == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nuevo estado del lote es obligatorio");
        }
        return ResponseEntity.ok(stockService.actualizarEstadoLote(idLote, nuevoEstado));
    }

    @GetMapping("/{idStock}/lotes")
    @PreAuthorize("hasAuthority('inventario:read')")
    public ResponseEntity<Page<LoteResponse>> listarLotes(
            @PathVariable Long idStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(stockService.listarLotes(idStock, page, size));
    }

    @PostMapping("/{idStock}/ajustar")
    @PreAuthorize("hasAuthority('inventario:update')")
    public ResponseEntity<StockSucursalResponse> ajustarStock(
            @PathVariable Long idStock,
            @Valid @RequestBody StockAjusteRequest dto) {
        return ResponseEntity.ok(stockService.ajustarStock(idStock, dto));
    }
}
