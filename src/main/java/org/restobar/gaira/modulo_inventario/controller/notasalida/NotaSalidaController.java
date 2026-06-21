package org.restobar.gaira.modulo_inventario.controller.notasalida;

import jakarta.validation.Valid;
import org.restobar.gaira.modulo_inventario.dto.notasalida.NotaSalidaRequestDTO;
import org.restobar.gaira.modulo_inventario.dto.notasalida.NotaSalidaResponseDTO;
import org.restobar.gaira.modulo_inventario.entity.NotaSalida;
import org.restobar.gaira.modulo_inventario.service.notasalida.NotaSalidaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/inventario/notas-salida")
public class NotaSalidaController {

    private final NotaSalidaService notaSalidaService;

    public NotaSalidaController(NotaSalidaService notaSalidaService) {
        this.notaSalidaService = notaSalidaService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('inventario:create') or hasAnyRole('SUPERUSER', 'SUPERUSUARIO') or @securityUtils.isSuperUser()")
    public ResponseEntity<NotaSalidaResponseDTO> crearNotaSalida(@Valid @RequestBody NotaSalidaRequestDTO request) {
        NotaSalidaResponseDTO response = notaSalidaService.crearNotaSalida(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/anular")
    @PreAuthorize("hasAuthority('inventario:update') or hasAnyRole('SUPERUSER', 'SUPERUSUARIO') or @securityUtils.isSuperUser()")
    public ResponseEntity<Void> anularNotaSalida(@PathVariable Long id) {
        notaSalidaService.anularNotaSalida(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('inventario:read') or hasAnyRole('SUPERUSER', 'SUPERUSUARIO') or @securityUtils.isSuperUser()")
    public ResponseEntity<Page<NotaSalidaResponseDTO>> listarNotasSalida(
            @RequestParam(required = false) Long idSucursal,
            @RequestParam(required = false) NotaSalida.TipoGasto tipoGasto,
            @RequestParam(required = false) NotaSalida.EstadoNota estado,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<NotaSalidaResponseDTO> page = notaSalidaService.listarNotasSalida(idSucursal, tipoGasto, estado, pageable);
        return ResponseEntity.ok(page);
    }
}
