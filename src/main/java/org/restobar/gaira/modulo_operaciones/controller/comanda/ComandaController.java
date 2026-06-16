package org.restobar.gaira.modulo_operaciones.controller.comanda;

import lombok.RequiredArgsConstructor;
import org.restobar.gaira.modulo_operaciones.dto.comanda.ComandaCreateDTO;
import org.restobar.gaira.modulo_operaciones.dto.comanda.ComandaResponseDTO;
import org.restobar.gaira.modulo_operaciones.dto.comanda.ComandaUpdateDTO;
import org.restobar.gaira.modulo_operaciones.service.comanda.ComandaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/comandas")
@RequiredArgsConstructor
public class ComandaController {

    private final ComandaService comandaService;

    @GetMapping
    @PreAuthorize("hasAuthority('comandas:read')")
    public ResponseEntity<List<ComandaResponseDTO>> findAll() {
        return ResponseEntity.ok(comandaService.getComandas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('comandas:read')")
    public ResponseEntity<ComandaResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(comandaService.getComandaById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('comandas:create')")
    public ResponseEntity<ComandaResponseDTO> create(@Valid @RequestBody ComandaCreateDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(comandaService.createComanda(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('comandas:update')")
    public ResponseEntity<ComandaResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ComandaUpdateDTO request) {
        return ResponseEntity.ok(comandaService.updateComanda(id, request));
    }

    @GetMapping("/sucursal/{idSucursal}")
    @PreAuthorize("hasAuthority('comandas:read')")
    public ResponseEntity<List<ComandaResponseDTO>> findBySucursal(@PathVariable Long idSucursal) {
        return ResponseEntity.ok(comandaService.getComandasBySucursal(idSucursal));
    }

    @GetMapping("/mesa/{idMesa}")
    @PreAuthorize("hasAuthority('comandas:read')")
    public ResponseEntity<List<ComandaResponseDTO>> findByMesa(@PathVariable Long idMesa) {
        return ResponseEntity.ok(comandaService.getComandasByMesa(idMesa));
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAuthority('comandas:read')")
    public ResponseEntity<List<ComandaResponseDTO>> findByEstado(@PathVariable String estado) {
        return ResponseEntity.ok(comandaService.getComandasByEstado(estado));
    }

    @PatchMapping("/{id}/cerrar")
    @PreAuthorize("hasAuthority('comandas:update')")
    public ResponseEntity<ComandaResponseDTO> close(@PathVariable Long id) {
        return ResponseEntity.ok(comandaService.closeComanda(id));
    }
}
