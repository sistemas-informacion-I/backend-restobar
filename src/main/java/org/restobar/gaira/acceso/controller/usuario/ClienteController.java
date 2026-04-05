package org.restobar.gaira.acceso.controller.usuario;

import java.util.List;

import org.restobar.gaira.acceso.dto.usuario.ClienteResponse;
import org.restobar.gaira.acceso.service.usuario.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('clients:read')")
    public ResponseEntity<List<ClienteResponse>> findAll() {
        return ResponseEntity.ok(clienteService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('clients:read')")
    public ResponseEntity<ClienteResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.findById(id));
    }
}
