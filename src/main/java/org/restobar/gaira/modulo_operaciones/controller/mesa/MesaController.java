package org.restobar.gaira.modulo_operaciones.controller.mesa;

import lombok.RequiredArgsConstructor;
import org.restobar.gaira.modulo_operaciones.dto.mesa.MesaCreateDTO;
import org.restobar.gaira.modulo_operaciones.dto.mesa.MesaDTO;
import org.restobar.gaira.modulo_operaciones.dto.mesa.MesaUpdateDTO;
import org.restobar.gaira.modulo_operaciones.service.mesa.MesaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/mesas")
@RequiredArgsConstructor
public class MesaController {

    private final MesaService mesaService;

    @GetMapping
    @PreAuthorize("hasAuthority('mesas:read')")
    public ResponseEntity<List<MesaDTO>> findAll() {
        return ResponseEntity.ok(mesaService.getAllMesas());
    }

    @GetMapping("/sector/{idSector}")
    @PreAuthorize("hasAuthority('mesas:read')")
    public ResponseEntity<List<MesaDTO>> findBySector(@PathVariable Long idSector) {
        return ResponseEntity.ok(mesaService.getMesasBySector(idSector));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('mesas:read')")
    public ResponseEntity<MesaDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(mesaService.getMesaById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('mesas:create')")
    public ResponseEntity<MesaDTO> create(@Valid @RequestBody MesaCreateDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mesaService.createMesa(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('mesas:update')")
    public ResponseEntity<MesaDTO> update(@PathVariable Long id, @Valid @RequestBody MesaUpdateDTO request) {
        return ResponseEntity.ok(mesaService.updateMesa(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('mesas:delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mesaService.deleteMesa(id);
        return ResponseEntity.noContent().build();
    }
}