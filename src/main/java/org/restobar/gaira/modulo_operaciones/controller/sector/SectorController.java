package org.restobar.gaira.modulo_operaciones.controller.sector;

import java.util.List;

import org.restobar.gaira.modulo_operaciones.dto.sector.SectorRequestDTO;
import org.restobar.gaira.modulo_operaciones.dto.sector.SectorResponseDTO;
import org.restobar.gaira.modulo_operaciones.service.sector.SectorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sectores")
@RequiredArgsConstructor
public class SectorController {

    private final SectorService sectorService;

    @GetMapping
    
    public ResponseEntity<List<SectorResponseDTO>> listarTodos() {
        return ResponseEntity.ok(sectorService.listarTodos());
    }

    @GetMapping("/{id}")
    
    public ResponseEntity<SectorResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(sectorService.obtenerPorId(id));
    }

    @GetMapping("/sucursal/{idSucursal}")
    
    public ResponseEntity<List<SectorResponseDTO>> listarPorSucursal(@PathVariable Long idSucursal) {
        return ResponseEntity.ok(sectorService.listarPorSucursal(idSucursal));
    }

    @PostMapping
    
    public ResponseEntity<SectorResponseDTO> crear(@Valid @RequestBody SectorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sectorService.crear(dto));
    }

    @PutMapping("/{id}")
    
    public ResponseEntity<SectorResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody SectorRequestDTO dto) {
        return ResponseEntity.ok(sectorService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        sectorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}