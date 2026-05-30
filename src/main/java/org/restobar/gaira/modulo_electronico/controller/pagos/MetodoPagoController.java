package org.restobar.gaira.modulo_electronico.controller.pagos;

import lombok.RequiredArgsConstructor;
import org.restobar.gaira.modulo_electronico.entity.MetodoPago;
import org.restobar.gaira.modulo_electronico.service.pagos.MetodoPagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/metodos-pago")
@RequiredArgsConstructor
public class MetodoPagoController {

    private final MetodoPagoService metodoPagoService;

    @GetMapping
    public ResponseEntity<List<MetodoPago>> getAll() {
        return ResponseEntity.ok(metodoPagoService.getAll());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MetodoPago> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(metodoPagoService.update(id, body));
    }
}
