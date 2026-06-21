package org.restobar.gaira.modulo_inventario.controller.alerta;

import java.util.List;

import org.restobar.gaira.modulo_inventario.dto.alerta.AlertaInventarioRequest;
import org.restobar.gaira.modulo_inventario.dto.alerta.AlertaInventarioResponse;
import org.restobar.gaira.modulo_inventario.service.alerta.AlertaInventarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventario/alertas")
@RequiredArgsConstructor
public class AlertaInventarioController {

    private final AlertaInventarioService alertaService;

    @GetMapping
    @PreAuthorize("hasAuthority('inventario:read')")
    public ResponseEntity<List<AlertaInventarioResponse>> listarAlertas(
            @ModelAttribute AlertaInventarioRequest request) {
        return ResponseEntity.ok(alertaService.listarAlertas(request));
    }

    @GetMapping("/pendientes/count")
    @PreAuthorize("hasAuthority('inventario:read')")
    public ResponseEntity<Long> contarAlertasPendientes(@RequestParam(required = false) Long idSucursal) {
        return ResponseEntity.ok(alertaService.contarAlertasPendientes(idSucursal));
    }

    @PostMapping("/{idAlerta}/leer")
    @PreAuthorize("hasAuthority('inventario:update')")
    public ResponseEntity<AlertaInventarioResponse> marcarComoLeida(@PathVariable Long idAlerta) {
        return ResponseEntity.ok(alertaService.marcarComoLeida(idAlerta));
    }
}
