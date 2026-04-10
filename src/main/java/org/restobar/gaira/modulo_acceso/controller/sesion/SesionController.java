package org.restobar.gaira.modulo_acceso.controller.sesion;

import java.util.List;

import org.restobar.gaira.modulo_acceso.dto.sesion.SesionResponse;
import org.restobar.gaira.modulo_acceso.service.sesion.SesionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sesiones")
public class SesionController {

    private final SesionService sesionService;

    public SesionController(SesionService sesionService) {
        this.sesionService = sesionService;
    }

    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("hasAuthority('sessions:read')")
    public ResponseEntity<List<SesionResponse>> findByUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(sesionService.findByUsuario(idUsuario));
    }

    @PatchMapping("/{idSesion}/revocar")
    @PreAuthorize("hasAuthority('sessions:revoke')")
    public ResponseEntity<Void> revoke(@PathVariable Long idSesion) {
        sesionService.revoke(idSesion);
        return ResponseEntity.noContent().build();
    }
}
