package org.restobar.gaira.modulo_operaciones.controller.detalleComanda;

import lombok.RequiredArgsConstructor;
import org.restobar.gaira.modulo_operaciones.dto.comanda.DetalleComandaUpdateDTO;
import org.restobar.gaira.modulo_operaciones.dto.comanda.ComandaResponseDTO;
import org.restobar.gaira.modulo_operaciones.service.comanda.ComandaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/comandas/{idComanda}/detalles")
@RequiredArgsConstructor
public class DetalleComandaController {

    private final ComandaService comandaService;

    @PutMapping("/{idDetalle}")
    @PreAuthorize("hasAuthority('comandas:update')")
    public ResponseEntity<ComandaResponseDTO> updateDetalle(
            @PathVariable Long idComanda,
            @PathVariable Long idDetalle,
            @Valid @RequestBody DetalleComandaUpdateDTO request) {
        return ResponseEntity.ok(comandaService.updateDetalle(idDetalle, request));
    }

    @PatchMapping("/{idDetalle}/cancelar")
    @PreAuthorize("hasAuthority('comandas:update')")
    public ResponseEntity<ComandaResponseDTO> cancelarDetalle(
            @PathVariable Long idComanda,
            @PathVariable Long idDetalle) {
        return ResponseEntity.ok(comandaService.cancelarDetalle(idDetalle));
    }

    @DeleteMapping("/{idDetalle}")
    @PreAuthorize("hasAuthority('comandas:update')")
    public ResponseEntity<ComandaResponseDTO> deleteDetalle(
            @PathVariable Long idComanda,
            @PathVariable Long idDetalle) {
        return ResponseEntity.ok(comandaService.deleteDetalle(idDetalle));
    }
}
