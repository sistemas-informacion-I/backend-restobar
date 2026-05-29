package org.restobar.gaira.modulo_electronico.controller.pagos;

import java.util.List;

import org.restobar.gaira.modulo_electronico.dto.pago.IniciarTransaccionOnlineRequest;
import org.restobar.gaira.modulo_electronico.dto.pago.MetodoPagoResponse;
import org.restobar.gaira.modulo_electronico.dto.pago.MetodoPagoUpdateRequest;
import org.restobar.gaira.modulo_electronico.dto.pago.TransaccionOnlineResponse;
import org.restobar.gaira.modulo_electronico.dto.pago.WebhookTransaccionOnlineRequest;
import org.restobar.gaira.modulo_electronico.service.pagos.PasarelaPagoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pasarela-pagos")
@RequiredArgsConstructor
@Validated
public class PasarelaPagoController {

    private final PasarelaPagoService pasarelaPagoService;

    @GetMapping("/metodos")
    public ResponseEntity<List<MetodoPagoResponse>> listarMetodos(
            @RequestParam(required = false) Boolean activos,
            @RequestParam(required = false) Boolean soloOnline) {
        return ResponseEntity.ok(pasarelaPagoService.listarMetodos(activos, soloOnline));
    }

    @PatchMapping("/metodos/{id}")
    public ResponseEntity<MetodoPagoResponse> actualizarMetodoPago(
            @PathVariable Long id,
            @Valid @RequestBody MetodoPagoUpdateRequest request) {
        return ResponseEntity.ok(pasarelaPagoService.actualizarMetodoPago(id, request));
    }

    @GetMapping("/transacciones/{id}")
    public ResponseEntity<TransaccionOnlineResponse> buscarTransaccion(@PathVariable Long id) {
        return ResponseEntity.ok(pasarelaPagoService.buscarTransaccion(id));
    }

    @PostMapping("/transacciones")
    public ResponseEntity<TransaccionOnlineResponse> iniciarTransaccion(
            @Valid @RequestBody IniciarTransaccionOnlineRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pasarelaPagoService.iniciarTransaccion(request));
    }

    @PatchMapping("/transacciones/{id}/webhook")
    public ResponseEntity<TransaccionOnlineResponse> procesarWebhook(
            @PathVariable Long id,
            @RequestHeader(name = "X-Payment-Signature", required = false) String firma,
            @Valid @RequestBody WebhookTransaccionOnlineRequest request) {
        return ResponseEntity.ok(pasarelaPagoService.procesarWebhook(id, request, firma));
    }
}