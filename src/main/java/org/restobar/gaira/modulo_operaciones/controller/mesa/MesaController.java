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
    public ResponseEntity<List<MesaDTO>> findAll() {
        return ResponseEntity.ok(mesaService.getAllMesas());
    }

    @GetMapping("/sector/{idSector}")
    public ResponseEntity<List<MesaDTO>> findBySector(@PathVariable Long idSector) {
        return ResponseEntity.ok(mesaService.getMesasBySector(idSector));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MesaDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(mesaService.getMesaById(id));
    }

    @PostMapping
    public ResponseEntity<MesaDTO> create(@Valid @RequestBody MesaCreateDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mesaService.createMesa(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MesaDTO> update(@PathVariable Long id, @Valid @RequestBody MesaUpdateDTO request) {
        return ResponseEntity.ok(mesaService.updateMesa(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mesaService.deleteMesa(id);
        return ResponseEntity.noContent().build();
    }
}