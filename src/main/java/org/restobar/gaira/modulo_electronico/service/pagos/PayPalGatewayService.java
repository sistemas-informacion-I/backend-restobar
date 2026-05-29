package org.restobar.gaira.modulo_electronico.service.pagos;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.restobar.gaira.modulo_comercial.entity.DetalleNotaVenta;
import org.restobar.gaira.modulo_comercial.entity.NotaVenta;
import org.restobar.gaira.modulo_comercial.repository.NotaVentaRepository;
import org.restobar.gaira.modulo_electronico.dto.pago.TransaccionOnlineResponse;
import org.restobar.gaira.modulo_electronico.dto.paypal.PayPalCaptureResponse;
import org.restobar.gaira.modulo_electronico.dto.paypal.PayPalCreateOrderRequest;
import org.restobar.gaira.modulo_electronico.dto.paypal.PayPalCreateOrderResponse;
import org.restobar.gaira.modulo_electronico.entity.MetodoPago;
import org.restobar.gaira.modulo_electronico.entity.TransaccionOnline;
import org.restobar.gaira.modulo_electronico.mapper.pago.PasarelaPagoMapper;
import org.restobar.gaira.modulo_electronico.mapper.pago.PayPalMapper;
import org.restobar.gaira.modulo_electronico.repository.MetodoPagoRepository;
import org.restobar.gaira.modulo_electronico.repository.TransaccionOnlineRepository;
import org.restobar.gaira.modulo_inventario.service.receta.RecetaService;
import org.restobar.gaira.modulo_operaciones.entity.Comanda;
import org.restobar.gaira.modulo_operaciones.repository.ComandaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.security.SecureRandom;


@Service
@RequiredArgsConstructor
public class PayPalGatewayService {

    private final NotaVentaRepository notaVentaRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final ComandaRepository comandaRepository;
    private final TransaccionOnlineRepository transaccionOnlineRepository;
    private final RecetaService recetaService;
    private final ObjectMapper objectMapper;
    private final PasarelaPagoMapper pasarelaPagoMapper;
    private final PayPalMapper payPalMapper;

    @Value("${paypal.mode:sandbox}")
    private String mode;

    @Value("${paypal.sandbox.client-id:}")
    private String sandboxClientId;

    @Value("${paypal.sandbox.client-secret:}")
    private String sandboxClientSecret;

    @Value("${paypal.live.client-id:}")
    private String liveClientId;

    @Value("${paypal.live.client-secret:}")
    private String liveClientSecret;

    @Value("${paypal.currency:USD}")
    private String defaultCurrency;

    @Value("${paypal.locale:en_US}")
    private String locale;

    @Value("${paypal.brand-name:Restobar}")
    private String brandName;

    @Value("${paypal.exchange-rate-bob-to-usd:6.96}")
    private BigDecimal exchangeRateBobToUsd;

    @Value("${paypal.return-url:http://localhost:3000/api/paypal/success}")
    private String defaultReturnUrl;

    @Value("${paypal.cancel-url:http://localhost:3000/api/paypal/cancel}")
    private String defaultCancelUrl;

    @Value("${paypal.validate-ssl:true}")
    private boolean validateSsl;

    private final HttpClient httpClient = buildHttpClient();

    private HttpClient httpClient() {
        return httpClient;
    }

    private HttpClient buildHttpClient() {
        SSLContext sslCtx = resolveSslContext();
        return HttpClient.newBuilder()
            .sslContext(sslCtx)
            .build();
    }

    private SSLContext resolveSslContext() {
        if (!validateSsl) {
            try {
                TrustManager[] trustAll = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                        public void checkClientTrusted(
                            X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(
                            X509Certificate[] certs, String authType) {}
                    }
                };
                SSLContext ctx = SSLContext.getInstance("TLS");
                ctx.init(null, trustAll, new SecureRandom());
                return ctx;
            } catch (Exception e) {
                throw new RuntimeException("No se pudo configurar SSL para PayPal", e);
            }
        }
        try {
            return SSLContext.getDefault();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo obtener SSL context por defecto", e);
        }
    }

    private volatile String cachedAccessToken;
    private volatile LocalDateTime tokenExpiry;

    private static final long TOKEN_CACHE_SECONDS = 3500;

    @Transactional
    public PayPalCreateOrderResponse createOrder(
        PayPalCreateOrderRequest request
    ) {
        NotaVenta notaVenta = notaVentaRepository
            .findById(request.idNotaVenta())
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Nota de venta no encontrada"
                )
            );

        MetodoPago metodoPago = metodoPagoRepository
            .findById(request.idMetodoPago())
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Método de pago no encontrado"
                )
            );

        if (!payPalMapper.esMetodoPayPal(metodoPago)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "El método seleccionado no corresponde a PayPal"
            );
        }

        if (!Boolean.TRUE.equals(metodoPago.getActivo())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "El método PayPal está inactivo"
            );
        }

        if (
            !NotaVenta.EstadoNotaVenta.EMITIDA.name().equalsIgnoreCase(
                notaVenta.getEstado()
            ) &&
            !NotaVenta.EstadoNotaVenta.PAGADA.name().equalsIgnoreCase(
                notaVenta.getEstado()
            )
        ) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "La nota de venta no está lista para pago online"
            );
        }

        Optional<TransaccionOnline> existente =
            transaccionOnlineRepository.findTopByNotaVenta_IdNotaVentaOrderByIdTransaccionDesc(
                request.idNotaVenta()
            );

        if (
            existente.isPresent() &&
            !esEstadoTerminal(existente.get().getEstado())
        ) {
            TransaccionOnline transaccionExistente = existente.get();
            Map<String, Object> datos =
                transaccionExistente.getDatosAdicionales() != null
                    ? transaccionExistente.getDatosAdicionales()
                    : Map.of();
            String approvalUrl = datos
                .getOrDefault("approval_url", "")
                .toString();
            String existingInvoice = datos
                .getOrDefault("invoice_number", "")
                .toString();
            return new PayPalCreateOrderResponse(
                transaccionExistente.getIdTransaccion(),
                transaccionExistente.getNumeroTransaccion(),
                approvalUrl,
                transaccionExistente.getEstado(),
                modeResolved(),
                resolveCurrency(request.moneda()),
                transaccionExistente.getMonto(),
                existingInvoice
            );
        }

        if (!StringUtils.hasText(request.referencia())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La referencia es obligatoria"
            );
        }

        PayPalCredentials credentials = resolveCredentials();
        String currency = resolveCurrency(request.moneda());
        String returnUrl = StringUtils.hasText(request.returnUrl())
            ? request.returnUrl()
            : defaultReturnUrl;
        String cancelUrl = StringUtils.hasText(request.cancelUrl())
            ? request.cancelUrl()
            : defaultCancelUrl;

        JsonNode order = executeJson(
            HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/v2/checkout/orders"))
                .header(
                    "Authorization",
                    "Bearer " + getAccessToken(credentials)
                )
                .header("Content-Type", "application/json")
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        payPalMapper.buildCreateOrderPayload(
                            request,
                            currency,
                            returnUrl,
                            cancelUrl,
                            brandName,
                            locale,
                            exchangeRateBobToUsd
                        )
                    )
                )
                .build()
        );

        String paypalOrderId = payPalMapper.requiredText(order, "id");
        String approvalUrl = payPalMapper.extractApprovalUrl(order.path("links"));
        String invoiceNumber = payPalMapper.generarNumeroFactura();

        Optional<TransaccionOnline> transaccionExistenteOpt =
            transaccionOnlineRepository.findByNumeroTransaccion(paypalOrderId);

        if (transaccionExistenteOpt.isPresent()) {
            TransaccionOnline existenteTrans = transaccionExistenteOpt.get();
            if (!esEstadoTerminal(existenteTrans.getEstado())) {
                existenteTrans.setFechaInicio(LocalDateTime.now());
                existenteTrans.setDatosAdicionales(
                    payPalMapper.buildAdditionalData(request, approvalUrl, modeResolved())
                );
                transaccionOnlineRepository.save(existenteTrans);

                notaVenta.setEstado(NotaVenta.EstadoNotaVenta.EMITIDA.name());
                notaVentaRepository.save(notaVenta);

                Comanda comanda = notaVenta.getComanda();
                if (comanda != null) {
                    comanda.setEstado(
                        Comanda.EstadoComanda.PENDIENTE_PAGO.name()
                    );
                    comandaRepository.save(comanda);
                }

                return new PayPalCreateOrderResponse(
                    existenteTrans.getIdTransaccion(),
                    paypalOrderId,
                    approvalUrl,
                    existenteTrans.getEstado(),
                    modeResolved(),
                    currency,
                    request.monto(),
                    invoiceNumber
                );
            }
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Ya existe una transacción con este número de orden PayPal"
            );
        }

        TransaccionOnline transaccion = TransaccionOnline.builder()
            .notaVenta(notaVenta)
            .numeroTransaccion(paypalOrderId)
            .monto(request.monto())
            .moneda(currency)
            .estado(TransaccionOnline.EstadoTransaccion.PENDIENTE.name())
            .datosAdicionales(payPalMapper.buildAdditionalData(request, approvalUrl, modeResolved()))
            .build();

        transaccion.getDatosAdicionales().put("invoice_number", invoiceNumber);
        transaccion = transaccionOnlineRepository.save(transaccion);

        notaVenta.setEstado(NotaVenta.EstadoNotaVenta.EMITIDA.name());
        notaVentaRepository.save(notaVenta);

        Comanda comanda = notaVenta.getComanda();
        if (comanda != null) {
            comanda.setEstado(Comanda.EstadoComanda.PENDIENTE_PAGO.name());
            comandaRepository.save(comanda);
        }

        return new PayPalCreateOrderResponse(
            transaccion.getIdTransaccion(),
            paypalOrderId,
            approvalUrl,
            transaccion.getEstado(),
            modeResolved(),
            currency,
            request.monto(),
            invoiceNumber
        );
    }

    @Transactional
    public PayPalCaptureResponse captureOrder(
        String paypalOrderId,
        String payerId
    ) {
        TransaccionOnline transaccion = transaccionOnlineRepository
            .findByNumeroTransaccion(paypalOrderId)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Transacción PayPal no encontrada"
                )
            );

        if (
            TransaccionOnline.EstadoTransaccion.APROBADA.name().equalsIgnoreCase(
                transaccion.getEstado()
            )
        ) {
            return new PayPalCaptureResponse(
                paypalOrderId,
                payPalMapper.valueAsString(transaccion.getDatosAdicionales(), "capture_id"),
                "COMPLETED",
                pasarelaPagoMapper.toTransaccionResponse(transaccion)
            );
        }

        PayPalCredentials credentials = resolveCredentials();
        JsonNode capture = executeJson(
            HttpRequest.newBuilder()
                .uri(
                    URI.create(
                        baseUrl() +
                            "/v2/checkout/orders/" +
                            paypalOrderId +
                            "/capture"
                    )
                )
                .header(
                    "Authorization",
                    "Bearer " + getAccessToken(credentials)
                )
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build()
        );

        String status = payPalMapper.requiredText(capture, "status");
        if (!"COMPLETED".equalsIgnoreCase(status)) {
            transaccion.setEstado(
                TransaccionOnline.EstadoTransaccion.RECHAZADA.name()
            );
            transaccion.setCodigoError(status);
            transaccionOnlineRepository.save(transaccion);
            return new PayPalCaptureResponse(
                paypalOrderId,
                null,
                status,
                pasarelaPagoMapper.toTransaccionResponse(transaccion)
            );
        }

        String captureId = payPalMapper.extractCaptureId(
            capture
                .path("purchase_units")
                .path(0)
                .path("payments")
                .path("captures")
        );
        transaccion.setEstado(
            TransaccionOnline.EstadoTransaccion.APROBADA.name()
        );
        transaccion.setFechaCompletado(LocalDateTime.now());
        transaccion.setCodigoAutorizacion(captureId);
        transaccion.setCodigoError(null);

        Map<String, Object> additionalData = payPalMapper.mergeAdditionalData(
            transaccion.getDatosAdicionales(),
            Map.of(
                "capture_id",
                captureId,
                "paypal_status",
                status,
                "mode",
                modeResolved()
            )
        );
        if (StringUtils.hasText(payerId)) {
            additionalData.put("payer_id", payerId);
        }
        transaccion.setDatosAdicionales(additionalData);
        transaccion = transaccionOnlineRepository.save(transaccion);

        NotaVenta notaVenta = transaccion.getNotaVenta();
        notaVenta.setEstado(NotaVenta.EstadoNotaVenta.PAGADA.name());
        if (notaVenta.getFechaPago() == null) {
            notaVenta.setFechaPago(transaccion.getFechaCompletado());
        }
        notaVentaRepository.save(notaVenta);

        Comanda comanda = notaVenta.getComanda();
        if (comanda != null) {
            comanda.setEstado(Comanda.EstadoComanda.ABIERTA.name());
            comandaRepository.save(comanda);
        }

        actualizarStock(notaVenta);

        return new PayPalCaptureResponse(
            paypalOrderId,
            captureId,
            status,
            pasarelaPagoMapper.toTransaccionResponse(transaccion)
        );
    }

    @Transactional
    public TransaccionOnlineResponse cancelOrder(
        String paypalOrderId,
        String reason
    ) {
        TransaccionOnline transaccion = transaccionOnlineRepository
            .findByNumeroTransaccion(paypalOrderId)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Transacción PayPal no encontrada"
                )
            );

        if (
            TransaccionOnline.EstadoTransaccion.APROBADA.name().equalsIgnoreCase(
                transaccion.getEstado()
            )
        ) {
            return pasarelaPagoMapper.toTransaccionResponse(transaccion);
        }

        transaccion.setEstado(
            TransaccionOnline.EstadoTransaccion.CANCELADA.name()
        );
        transaccion.setCodigoError(
            StringUtils.hasText(reason) ? reason : "CANCELLED_BY_USER"
        );
        transaccion.setFechaCompletado(LocalDateTime.now());
        transaccion = transaccionOnlineRepository.save(transaccion);
        return pasarelaPagoMapper.toTransaccionResponse(transaccion);
    }

    private void actualizarStock(NotaVenta notaVenta) {
        List<DetalleNotaVenta> detalles = notaVenta.getDetalleNotaVentas();
        if (detalles == null || detalles.isEmpty()) {
            return;
        }

        Long idSucursal = notaVenta.getSucursal().getIdSucursal();

        for (DetalleNotaVenta detalle : detalles) {
            Long idProducto = detalle.getProductoFinal().getIdProductoFinal();
            Integer cantidad = detalle.getCantidad();

            recetaService.descontarIngredientesVenta(
                idProducto,
                idSucursal,
                cantidad
            );
        }
    }

    private boolean esEstadoTerminal(String estado) {
        return (
            TransaccionOnline.EstadoTransaccion.APROBADA.name().equalsIgnoreCase(estado) ||
            TransaccionOnline.EstadoTransaccion.RECHAZADA.name().equalsIgnoreCase(estado) ||
            TransaccionOnline.EstadoTransaccion.REEMBOLSADA.name().equalsIgnoreCase(estado) ||
            TransaccionOnline.EstadoTransaccion.CANCELADA.name().equalsIgnoreCase(estado)
        );
    }

    private JsonNode executeJson(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient().send(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "PayPal respondió con estado " +
                        response.statusCode() +
                        ": " +
                        response.body()
                );
            }
            return objectMapper.readTree(response.body());
        } catch (IOException ex) {
            throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "Error de IO al comunicar con PayPal",
                ex
            );
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "Interrupción al comunicar con PayPal",
                ex
            );
        }
    }

    private String getAccessToken(PayPalCredentials credentials) {
        if (
            cachedAccessToken != null &&
            tokenExpiry != null &&
            LocalDateTime.now().isBefore(tokenExpiry)
        ) {
            return cachedAccessToken;
        }

        String auth = java.util.Base64.getEncoder().encodeToString(
            (credentials.clientId + ":" + credentials.clientSecret).getBytes(
                StandardCharsets.UTF_8
            )
        );

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + "/v1/oauth2/token"))
            .header("Authorization", "Basic " + auth)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    "grant_type=client_credentials"
                )
            )
            .build();

        JsonNode token = executeJson(request);
        cachedAccessToken = payPalMapper.requiredText(token, "access_token");

        String expiresIn = token.path("expires_in").asText(null);
        if (StringUtils.hasText(expiresIn)) {
            long seconds = Long.parseLong(expiresIn);
            tokenExpiry = LocalDateTime.now().plus(
                seconds - 60,
                ChronoUnit.SECONDS
            );
        } else {
            tokenExpiry = LocalDateTime.now().plusSeconds(TOKEN_CACHE_SECONDS);
        }

        return cachedAccessToken;
    }

    private String baseUrl() {
        return "live".equalsIgnoreCase(mode)
            ? "https://api-m.paypal.com"
            : "https://api-m.sandbox.paypal.com";
    }

    private String modeResolved() {
        return "live".equalsIgnoreCase(mode) ? "live" : "sandbox";
    }

    private PayPalCredentials resolveCredentials() {
        if ("live".equalsIgnoreCase(mode)) {
            if (
                !StringUtils.hasText(liveClientId) ||
                !StringUtils.hasText(liveClientSecret)
            ) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Faltan credenciales live de PayPal"
                );
            }
            return new PayPalCredentials(
                liveClientId.trim(),
                liveClientSecret.trim()
            );
        }

        if (
            !StringUtils.hasText(sandboxClientId) ||
            !StringUtils.hasText(sandboxClientSecret)
        ) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Faltan credenciales sandbox de PayPal"
            );
        }
        return new PayPalCredentials(
            sandboxClientId.trim(),
            sandboxClientSecret.trim()
        );
    }

    private String resolveCurrency(String moneda) {
        return StringUtils.hasText(moneda)
            ? moneda.trim().toUpperCase()
            : defaultCurrency.toUpperCase();
    }

    private record PayPalCredentials(String clientId, String clientSecret) {}
}
