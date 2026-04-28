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

import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('sucursales:read')")
    public ResponseEntity<List<SucursalResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(sucursalService.listarTodas());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('sucursales:read')")
    public  ResponseEntity<SucursalResponseDTO> obtenerId(@PathVariable Long id) {
        return ResponseEntity.ok(sucursalService.obtenerPorId(id));
    }
        
    @PostMapping
    @PreAuthorize("hasAuthority('sucursales:create')")
    public ResponseEntity<SucursalResponseDTO> crear( @RequestBody SucursalRequestDTO dto) { // una forma de obtener ese dato del front  
        return ResponseEntity.status(HttpStatus.CREATED).body(sucursalService.crear(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('sucursales:update')")
    public ResponseEntity<SucursalResponseDTO> actualizar(@PathVariable Long id, @RequestBody SucursalRequestDTO dtoFront) {
        return ResponseEntity.ok(sucursalService.actualizar(id, dtoFront));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sucursales:delete')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        sucursalService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    






}
