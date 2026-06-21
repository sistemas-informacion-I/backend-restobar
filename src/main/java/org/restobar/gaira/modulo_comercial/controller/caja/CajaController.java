package org.restobar.gaira.modulo_comercial.controller.caja;

import java.util.List;

import org.restobar.gaira.modulo_comercial.dto.caja.AbrirCajaRequest;
import org.restobar.gaira.modulo_comercial.dto.caja.ArqueoResponse;
import org.restobar.gaira.modulo_comercial.dto.caja.CajaResponse;
import org.restobar.gaira.modulo_comercial.dto.caja.CerrarCajaRequest;
import org.restobar.gaira.modulo_comercial.dto.caja.MovimientoCajaResponse;
import org.restobar.gaira.modulo_comercial.dto.caja.MovimientoManualRequest;
import org.restobar.gaira.modulo_comercial.entity.Caja.Estado;
import org.restobar.gaira.modulo_comercial.service.caja.CajaService;
import org.restobar.gaira.security.userdetails.ApplicationUserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cajas")
@RequiredArgsConstructor
public class CajaController {

    private final CajaService cajaService;

    /** Caja actualmente abierta en la sucursal en contexto (SU debe pasar idSucursal). */
    @GetMapping("/actual")
    @PreAuthorize("hasAuthority('caja:read')")
    public ResponseEntity<CajaResponse> getCajaActual(
            @RequestParam(required = false) Long idSucursal,
            @AuthenticationPrincipal ApplicationUserPrincipal principal) {
        return ResponseEntity.ok(cajaService.getCajaActual(principal, idSucursal));
    }

    /** Historial de cajas (filtrable por sucursal/estado). El empleado solo ve su sucursal. */
    @GetMapping
    @PreAuthorize("hasAuthority('caja:read')")
    public ResponseEntity<List<CajaResponse>> findAll(
            @RequestParam(required = false) Long idSucursal,
            @RequestParam(required = false) Estado estado,
            @AuthenticationPrincipal ApplicationUserPrincipal principal) {
        return ResponseEntity.ok(cajaService.findAll(principal, idSucursal, estado));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('caja:read')")
    public ResponseEntity<CajaResponse> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal ApplicationUserPrincipal principal) {
        return ResponseEntity.ok(cajaService.findById(id, principal));
    }

    @GetMapping("/{id}/movimientos")
    @PreAuthorize("hasAuthority('caja:read')")
    public ResponseEntity<List<MovimientoCajaResponse>> getMovimientos(
            @PathVariable Long id,
            @AuthenticationPrincipal ApplicationUserPrincipal principal) {
        return ResponseEntity.ok(cajaService.getMovimientos(id, principal));
    }

    @GetMapping("/{id}/arqueo")
    @PreAuthorize("hasAuthority('caja:read')")
    public ResponseEntity<ArqueoResponse> getArqueo(
            @PathVariable Long id,
            @AuthenticationPrincipal ApplicationUserPrincipal principal) {
        return ResponseEntity.ok(cajaService.getArqueo(id, principal));
    }

    @PostMapping("/abrir")
    @PreAuthorize("hasAuthority('caja:create')")
    public ResponseEntity<CajaResponse> abrir(
            @Valid @RequestBody AbrirCajaRequest request,
            @AuthenticationPrincipal ApplicationUserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cajaService.abrirCaja(request, principal));
    }

    @PostMapping("/{id}/movimientos")
    @PreAuthorize("hasAuthority('caja:update')")
    public ResponseEntity<CajaResponse> registrarMovimiento(
            @PathVariable Long id,
            @Valid @RequestBody MovimientoManualRequest request,
            @AuthenticationPrincipal ApplicationUserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cajaService.registrarMovimientoManual(id, request, principal));
    }

    @PostMapping("/{id}/cerrar")
    @PreAuthorize("hasAuthority('caja:update')")
    public ResponseEntity<CajaResponse> cerrar(
            @PathVariable Long id,
            @Valid @RequestBody CerrarCajaRequest request,
            @AuthenticationPrincipal ApplicationUserPrincipal principal) {
        return ResponseEntity.ok(cajaService.cerrarCaja(id, request, principal));
    }
}
