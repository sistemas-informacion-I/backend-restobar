package org.restobar.gaira.modulo_inventario.controller.stock;

import java.util.List;

import org.restobar.gaira.modulo_inventario.dto.stock.StockSucursalRequest;
import org.restobar.gaira.modulo_inventario.dto.stock.StockSucursalResponse;
import org.restobar.gaira.modulo_inventario.dto.stock.StockAjusteRequest;
import org.restobar.gaira.modulo_inventario.service.stock.StockSucursalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/{idStock}/ajustar")
    @PreAuthorize("hasAuthority('inventario:update')")
    public ResponseEntity<StockSucursalResponse> ajustarStock(
            @PathVariable Long idStock,
            @Valid @RequestBody StockAjusteRequest dto) {
        return ResponseEntity.ok(stockService.ajustarStock(idStock, dto));
    }
}
