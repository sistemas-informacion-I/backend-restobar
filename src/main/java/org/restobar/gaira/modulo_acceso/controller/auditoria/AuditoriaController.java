package org.restobar.gaira.modulo_acceso.controller.auditoria;

import java.time.LocalDateTime;

import org.restobar.gaira.modulo_acceso.dto.auditoria.AuditoriaFilter;
import org.restobar.gaira.modulo_acceso.dto.auditoria.LogAuditoriaResponse;
import org.restobar.gaira.security.audit.service.LogAuditoriaService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    private final LogAuditoriaService logAuditoriaService;

    public AuditoriaController(LogAuditoriaService logAuditoriaService) {
        this.logAuditoriaService = logAuditoriaService;
    }

    /**
     * Lista paginada con filtros opcionales: tabla, operacion, idUsuario, desde,
     * hasta, page, size.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('audit:read')")
    public ResponseEntity<Page<LogAuditoriaResponse>> findAll(
            @RequestParam(required = false) String tabla,
            @RequestParam(required = false) String operacion,
            @RequestParam(required = false) Long idUsuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        AuditoriaFilter filter = new AuditoriaFilter(
                tabla, operacion, idUsuario, desde, hasta, page, size);
        return ResponseEntity.ok(logAuditoriaService.findAll(filter));
    }

    /**
     * Detalle completo incluyendo datosAnteriores y datosNuevos para el diff
     * visual.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('audit:read')")
    public ResponseEntity<LogAuditoriaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(logAuditoriaService.findById(id));
    }
}
