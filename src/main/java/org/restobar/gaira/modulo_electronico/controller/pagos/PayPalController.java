package org.restobar.gaira.modulo_electronico.controller.pagos;

import org.restobar.gaira.modulo_electronico.dto.paypal.PayPalCaptureResponse;
import org.restobar.gaira.modulo_electronico.dto.paypal.PayPalCreateOrderRequest;
import org.restobar.gaira.modulo_electronico.dto.paypal.PayPalCreateOrderResponse;
import org.restobar.gaira.modulo_electronico.dto.pago.TransaccionOnlineResponse;
import org.restobar.gaira.modulo_electronico.service.pagos.PayPalGatewayService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/paypal")
@RequiredArgsConstructor
@Validated
public class PayPalController {

    private final PayPalGatewayService payPalGatewayService;

    @PostMapping("/create")
    public ResponseEntity<PayPalCreateOrderResponse> createPayment(
            @Valid @RequestBody PayPalCreateOrderRequest request) {
        return ResponseEntity.ok(payPalGatewayService.createOrder(request));
    }

    @GetMapping("/capture")
    public ResponseEntity<PayPalCaptureResponse> capturePayment(
            @RequestParam("token") String orderId,
            @RequestParam(value = "PayerID", required = false) String payerId) {
        return ResponseEntity.ok(payPalGatewayService.captureOrder(orderId, payerId));
    }

    @GetMapping("/cancel")
    public ResponseEntity<TransaccionOnlineResponse> cancelPayment(
            @RequestParam(name = "token", required = false) String orderId,
            @RequestParam(name = "reason", required = false) String reason) {
        if (orderId == null || orderId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(payPalGatewayService.cancelOrder(orderId, reason));
    }

    /**
     * Alias for {@link #capturePayment(String, String)} — used by PayPal return URL.
     */
    @GetMapping("/success")
    public ResponseEntity<PayPalCaptureResponse> success(
            @RequestParam("token") String orderId,
            @RequestParam(value = "PayerID", required = false) String payerId) {
        return capturePayment(orderId, payerId);
    }
}