package org.restobar.gaira.modulo_acceso.controller.perfil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.restobar.gaira.modulo_acceso.dto.perfil.CambioPasswordRequest;
import org.restobar.gaira.modulo_acceso.dto.perfil.PerfilPersonalResponse;
import org.restobar.gaira.modulo_acceso.dto.perfil.PerfilPersonalUpdate;
import org.restobar.gaira.modulo_acceso.service.perfil.PerfilPersonalService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/perfil")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class PerfilPersonalController {

    private final PerfilPersonalService service;

    @GetMapping
    public ResponseEntity<PerfilPersonalResponse> obtenerMiPerfil() {
        return ResponseEntity.ok(service.obtenerMiPerfil());
    }

    @PutMapping
    public ResponseEntity<PerfilPersonalResponse> actualizarMiPerfil(@Valid @RequestBody PerfilPersonalUpdate update) {
        return ResponseEntity.ok(service.actualizarMiPerfil(update));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> cambiarMiPassword(@Valid @RequestBody CambioPasswordRequest request) {
        service.cambiarMiPassword(request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> eliminarMiPerfil() {
        service.eliminarMiPerfil();
        return ResponseEntity.noContent().build();
    }
}
