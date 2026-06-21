package org.restobar.gaira.modulo_electronico.controller.entrega;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.restobar.gaira.modulo_electronico.dto.entrega.EntregaResponse;
import org.restobar.gaira.modulo_electronico.dto.entrega.UbicacionRequest;
import org.restobar.gaira.modulo_electronico.service.entrega.EntregaService;
import org.restobar.gaira.security.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/entregas")
@RequiredArgsConstructor
public class EntregaController {

    private final EntregaService entregaService;
    private final SecurityUtils securityUtils;

    @PostMapping
    @PreAuthorize("hasRole('SUPERUSER') or hasRole('ADMIN')")
    public ResponseEntity<EntregaResponse> crear(@RequestParam Long idComanda,
                                                  @RequestParam String direccionEntrega,
                                                  @RequestParam BigDecimal latitud,
                                                  @RequestParam BigDecimal longitud,
                                                  @RequestParam(required = false) BigDecimal costoEnvio) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(entregaService.crearEntrega(idComanda, direccionEntrega, latitud, longitud, costoEnvio));
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPERUSER') or hasRole('ADMIN') or hasRole('REPARTIDOR')")
    public ResponseEntity<List<EntregaResponse>> listarPendientes() {
        return ResponseEntity.ok(entregaService.listarPendientes());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EntregaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(entregaService.getEntrega(id));
    }

    @GetMapping("/comanda/{idComanda}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EntregaResponse> obtenerPorComanda(@PathVariable Long idComanda) {
        return ResponseEntity.ok(entregaService.getEntregaByComanda(idComanda));
    }

    @GetMapping("/mis-entregas")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EntregaResponse>> misEntregas() {
        Long idUsuario = securityUtils.getCurrentUserId();
        if (idUsuario == null) return ResponseEntity.ok(List.of());
        return ResponseEntity.ok(entregaService.getEntregasByUsuario(idUsuario));
    }

    @GetMapping("/{idEntrega}/disponibilidad")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> verificarDisponibilidad(
            @PathVariable Long idEntrega,
            @RequestParam(required = false) BigDecimal latitud,
            @RequestParam(required = false) BigDecimal longitud) {
        return ResponseEntity.ok(entregaService.verificarDisponibilidad(idEntrega, latitud, longitud));
    }

    @PatchMapping("/{id}/aceptar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EntregaResponse> aceptar(
            @PathVariable Long id,
            @RequestParam(required = false) BigDecimal latitud,
            @RequestParam(required = false) BigDecimal longitud) {
        return ResponseEntity.ok(entregaService.aceptarEntrega(id, latitud, longitud));
    }

    @PatchMapping("/{id}/iniciar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EntregaResponse> iniciarViaje(@PathVariable Long id) {
        return ResponseEntity.ok(entregaService.iniciarViaje(id));
    }

    @PostMapping("/ubicacion")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> reportarUbicacion(@Valid @RequestBody UbicacionRequest ubicacion) {
        entregaService.reportarUbicacion(ubicacion);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/entregado")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EntregaResponse> marcarEntregado(@PathVariable Long id) {
        return ResponseEntity.ok(entregaService.marcarEntregado(id));
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('SUPERUSER') or hasRole('ADMIN')")
    public ResponseEntity<EntregaResponse> cancelar(@PathVariable Long id,
                                                     @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(entregaService.cancelarEntrega(id, motivo));
    }
}
