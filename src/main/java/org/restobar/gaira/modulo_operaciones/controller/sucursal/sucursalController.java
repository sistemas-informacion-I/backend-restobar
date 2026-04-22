package org.restobar.gaira.modulo_operaciones.controller.sucursal;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.restobar.gaira.modulo_operaciones.dto.sucursal.SucursalResponseDTO;
import org.restobar.gaira.modulo_operaciones.service.sucursal.SucursalService;
import org.restobar.gaira.modulo_operaciones.dto.sucursal.SucursalRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;






//endpoint 
@RestController
@RequestMapping("/api/sucursal")
public class sucursalController {
    
    private final SucursalService sucursalService;

    public sucursalController(SucursalService sucursalService){
        this.sucursalService = sucursalService;
    }

    @GetMapping
    public ResponseEntity<List<SucursalResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(sucursalService.listarTodas());
    }
    
    @GetMapping("/{id}") 
    public  ResponseEntity<SucursalResponseDTO> obtenerId(@PathVariable Long id) {
        return ResponseEntity.ok(sucursalService.obtenerPorId(id));
    }
        
    @PostMapping
    public ResponseEntity<SucursalResponseDTO> crear( @RequestBody SucursalRequestDTO dto) { // una forma de obtener ese dato del front  
        return ResponseEntity.status(HttpStatus.CREATED).body(sucursalService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SucursalResponseDTO> actualizar(@PathVariable Long id, @RequestBody SucursalRequestDTO dtoFront) {
        return ResponseEntity.ok(sucursalService.actualizar(id, dtoFront));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        sucursalService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    






}
