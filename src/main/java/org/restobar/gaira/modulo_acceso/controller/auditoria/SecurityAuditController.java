package org.restobar.gaira.modulo_acceso.controller.auditoria;

import java.util.List;

import org.restobar.gaira.modulo_acceso.dto.auditoria.LogAuditoriaResponse;
import org.restobar.gaira.modulo_acceso.service.auditoria.LogAuditoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/security")
public class SecurityAuditController {

    private final LogAuditoriaService logAuditoriaService;

    public SecurityAuditController(LogAuditoriaService logAuditoriaService) {
        this.logAuditoriaService = logAuditoriaService;
    }

    @GetMapping("/auditoria")
    @PreAuthorize("hasAuthority('audit:read')")
    public ResponseEntity<List<LogAuditoriaResponse>> latestAuditLogs() {
        return ResponseEntity.ok(logAuditoriaService.latest());
    }
}
