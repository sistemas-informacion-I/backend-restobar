package org.restobar.gaira.modulo_electronico.controller.carrito;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.restobar.gaira.modulo_electronico.dto.carrito.CambiarSucursalRequest;
import org.restobar.gaira.modulo_electronico.dto.carrito.CarritoResponse;
import org.restobar.gaira.modulo_electronico.dto.item.ActualizarItemRequest;
import org.restobar.gaira.modulo_electronico.dto.item.AgregarItemRequest;
import org.restobar.gaira.modulo_electronico.service.carrito.CarritoService;
import org.restobar.gaira.security.userdetails.ApplicationUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.BigDecimal;

/**
 * Endpoints REST del carrito de compras online (CU20).
 *
 * <h3>Identidad del cliente</h3>
 * <ul>
 *   <li><b>Autenticado</b>: se extrae {@code idCliente} del JWT vía
 *       {@link ApplicationUserPrincipal}.</li>
 *   <li><b>Anónimo</b>: se usa el header {@code X-Session-Id} (UUID generado
 *       por el frontend al iniciar sesión de navegación).</li>
 * </ul>
 *
 * <p>Todos los endpoints reciben {@code idSucursal} como parámetro de query;
 * el carrito es exclusivo por sucursal (regla multi-sucursal CU20).
 */
@RestController
@RequestMapping("/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private static final String HEADER_SESSION = "X-Session-Id";

    private final CarritoService carritoService;

    // ── GET /carrito?idSucursal=1 ─────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<CarritoResponse> obtener(
            @RequestParam Long idSucursal,
            Authentication authentication,
            HttpServletRequest request) {

        Long idCliente = resolveIdCliente(authentication);
        String sessionId = resolveSessionId(idCliente, request);

        CarritoResponse response = carritoService.obtenerCarrito(idCliente, sessionId, idSucursal);
        return ResponseEntity.ok(response);
    }

    // ── POST /carrito/items?idSucursal=1 ──────────────────────────────────────

    @PostMapping("/items")
    public ResponseEntity<CarritoResponse> agregarItem(
            @RequestParam Long idSucursal,
            @Valid @RequestBody AgregarItemRequest body,
            Authentication authentication,
            HttpServletRequest request) {

        Long idCliente = resolveIdCliente(authentication);
        String sessionId = resolveSessionId(idCliente, request);

        CarritoResponse response = carritoService.agregarItem(idCliente, sessionId, idSucursal, body);
        return ResponseEntity.ok(response);
    }

    // ── PUT /carrito/items/{idProductoFinal}?idSucursal=1 ────────────────────

    @PutMapping("/items/{idProductoFinal}")
    public ResponseEntity<CarritoResponse> actualizarItem(
            @PathVariable Long idProductoFinal,
            @RequestParam Long idSucursal,
            @Valid @RequestBody ActualizarItemRequest body,
            Authentication authentication,
            HttpServletRequest request) {

        Long idCliente = resolveIdCliente(authentication);
        String sessionId = resolveSessionId(idCliente, request);

        CarritoResponse response = carritoService.actualizarItem(
                idCliente, sessionId, idSucursal, idProductoFinal, body);
        return ResponseEntity.ok(response);
    }

    // ── DELETE /carrito/items/{idProductoFinal}?idSucursal=1 ─────────────────

    @DeleteMapping("/items/{idProductoFinal}")
    public ResponseEntity<CarritoResponse> eliminarItem(
            @PathVariable Long idProductoFinal,
            @RequestParam Long idSucursal,
            Authentication authentication,
            HttpServletRequest request) {

        Long idCliente = resolveIdCliente(authentication);
        String sessionId = resolveSessionId(idCliente, request);

        CarritoResponse response = carritoService.eliminarItem(
                idCliente, sessionId, idSucursal, idProductoFinal);
        return ResponseEntity.ok(response);
    }

    // ── PATCH /carrito/sucursal ───────────────────────────────────────────────

    @PatchMapping("/sucursal")
    public ResponseEntity<CarritoResponse> cambiarSucursal(
            @Valid @RequestBody CambiarSucursalRequest body,
            Authentication authentication,
            HttpServletRequest request) {

        Long idCliente = resolveIdCliente(authentication);
        String sessionId = resolveSessionId(idCliente, request);

        CarritoResponse response = carritoService.cambiarSucursal(
                idCliente, sessionId, body.idSucursal());
        return ResponseEntity.ok(response);
    }

    // ── POST /carrito/checkout ────────────────────────────────────────────────

    /**
     * Convierte el carrito en comanda ONLINE + NotaVenta EMITIDA.
     * Requiere autenticación (el cliente anónimo debe haber iniciado sesión).
     * Retorna idNotaVenta para usar con la pasarela de pago (PayPal).
     * Si se proveen coordenadas de entrega, se crea automaticamente el registro de Entrega.
     */
    @PostMapping("/checkout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Long>> checkout(
            @RequestParam Long idSucursal,
            @RequestParam(required = false) Long idMetodoPago,
            @RequestParam(required = false) String direccionEntrega,
            @RequestParam(required = false) BigDecimal latitud,
            @RequestParam(required = false) BigDecimal longitud,
            @RequestParam(required = false) BigDecimal costoEnvio,
            Authentication authentication) {

        Long idCliente = resolveIdClienteRequerido(authentication);
        Long idNotaVenta = carritoService.checkout(idCliente, idSucursal, idMetodoPago,
                direccionEntrega, latitud, longitud, costoEnvio);

        return ResponseEntity
                .created(URI.create("/notas-venta/" + idNotaVenta))
                .body(Map.of("idNotaVenta", idNotaVenta));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Extrae idCliente del JWT; retorna null si el usuario es anónimo. */
    private Long resolveIdCliente(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof ApplicationUserPrincipal principal
                && "C".equals(principal.getTipoUsuario())) {
            return principal.getIdUsuario(); // idUsuario == idCliente para tipo C
        }
        return null;
    }

    /** Igual que {@link #resolveIdCliente} pero lanza 401 si es nulo. */
    private Long resolveIdClienteRequerido(Authentication authentication) {
        Long id = resolveIdCliente(authentication);
        if (id == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED,
                    "Debe iniciar sesión para realizar el checkout");
        }
        return id;
    }

    /** Devuelve el sessionId del header; exige que esté presente si el usuario es anónimo. */
    private String resolveSessionId(Long idCliente, HttpServletRequest request) {
        if (idCliente != null) return null; // no se necesita para autenticados
        String sid = request.getHeader(HEADER_SESSION);
        if (sid == null || sid.isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Se requiere el header " + HEADER_SESSION + " para clientes no autenticados");
        }
        return sid;
    }
}
