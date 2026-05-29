package org.restobar.gaira.modulo_electronico.mapper.pago;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.restobar.gaira.modulo_electronico.dto.paypal.PayPalCreateOrderRequest;
import org.restobar.gaira.modulo_electronico.entity.MetodoPago;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class PayPalMapper {

    private static final String PAYPAL_PROVIDER = "PAYPAL";

    private final ObjectMapper objectMapper;

    // ── Build PayPal API Payload ──────────────────────────────────────────────

    public String buildCreateOrderPayload(
            PayPalCreateOrderRequest request,
            String currency,
            String returnUrl,
            String cancelUrl,
            String brandName,
            String locale,
            BigDecimal exchangeRateBobToUsd) {

        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("intent", "CAPTURE");

            ObjectNode appCtx = root.putObject("application_context");
            appCtx.put("brand_name", brandName);
            appCtx.put("locale", locale);
            appCtx.put("landing_page", "BILLING");
            appCtx.put("shipping_preference", "NO_SHIPPING");
            appCtx.put("user_action", "PAY_NOW");
            appCtx.put("return_url", returnUrl);
            appCtx.put("cancel_url", cancelUrl);

            ObjectNode purchaseUnit = root
                    .putArray("purchase_units")
                    .addObject()
                    .put("reference_id", request.referencia())
                    .put("description", "Compra en " + brandName);

            BigDecimal montoUSD = request.monto()
                    .divide(exchangeRateBobToUsd, 2, RoundingMode.HALF_UP);

            ObjectNode amount = purchaseUnit.putObject("amount");
            amount.put("currency_code", currency);
            amount.put("value", montoUSD.toPlainString());

            List<PayPalCreateOrderRequest.ItemPedido> items = request.items();
            if (items != null && !items.isEmpty()) {
                ObjectNode breakdown = amount.putObject("breakdown");
                BigDecimal itemTotalUSD = BigDecimal.ZERO;
                ArrayNode itemsArray = purchaseUnit.putArray("items");

                for (PayPalCreateOrderRequest.ItemPedido item : items) {
                    BigDecimal unitAmountUSD = item.unitAmount()
                            .divide(exchangeRateBobToUsd, 2, RoundingMode.HALF_UP);
                    BigDecimal lineTotalUSD = unitAmountUSD.multiply(
                            BigDecimal.valueOf(item.quantity()));

                    ObjectNode itemNode = itemsArray.addObject();
                    itemNode.put("name", item.name());
                    itemNode.put("quantity", item.quantity());
                    itemNode.putObject("unit_amount")
                            .put("currency_code", currency)
                            .put("value", unitAmountUSD.toPlainString());
                    if (item.sku() != null) {
                        itemNode.put("sku", item.sku());
                    }
                    itemTotalUSD = itemTotalUSD.add(lineTotalUSD);
                }

                breakdown.putObject("item_total")
                        .put("currency_code", currency)
                        .put("value", itemTotalUSD.setScale(2, RoundingMode.HALF_UP).toPlainString());

                BigDecimal taxTotalUSD = montoUSD.subtract(itemTotalUSD);
                if (taxTotalUSD.compareTo(BigDecimal.ZERO) > 0) {
                    breakdown.putObject("tax_total")
                            .put("currency_code", currency)
                            .put("value", taxTotalUSD.setScale(2, RoundingMode.HALF_UP).toPlainString());
                }
            }

            return objectMapper.writeValueAsString(root);
        } catch (IOException ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo construir la orden de PayPal",
                    ex);
        }
    }

    // ── Transaction Metadata ──────────────────────────────────────────────────

    public Map<String, Object> buildAdditionalData(
            PayPalCreateOrderRequest request,
            String approvalUrl,
            String mode) {

        Map<String, Object> data = new HashMap<>();
        if (request.datosAdicionales() != null) {
            data.putAll(request.datosAdicionales());
        }
        data.put("provider", PAYPAL_PROVIDER);
        data.put("mode", mode);
        data.put("approval_url", approvalUrl);
        data.put("reference", request.referencia());
        putIfPresent(data, "customer_name", request.customerName());
        putIfPresent(data, "customer_email", request.customerEmail());
        putIfPresent(data, "customer_phone", request.customerPhone());
        putIfPresent(data, "nit_cliente", request.nitCliente());
        putIfPresent(data, "shipping_address", request.shippingAddress());
        putIfPresent(data, "shipping_city", request.shippingCity());
        putIfPresent(data, "shipping_state", request.shippingState());
        putIfPresent(data, "shipping_zip", request.shippingZip());
        putIfPresent(data, "shipping_notes", request.shippingNotes());
        return data;
    }

    public Map<String, Object> mergeAdditionalData(
            Map<String, Object> original,
            Map<String, Object> extra) {

        Map<String, Object> result = new HashMap<>();
        if (original != null) result.putAll(original);
        if (extra != null) result.putAll(extra);
        return result;
    }

    public String valueAsString(Map<String, Object> data, String key) {
        if (data == null || !data.containsKey(key) || data.get(key) == null) {
            return null;
        }
        return data.get(key).toString();
    }

    // ── PayPal Response Parsing ───────────────────────────────────────────────

    public String extractApprovalUrl(JsonNode links) {
        if (links == null || !links.isArray()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "PayPal no devolvió enlace de aprobación");
        }
        for (JsonNode link : links) {
            if ("approve".equalsIgnoreCase(link.path("rel").asText())) {
                return link.path("href").asText();
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                "PayPal no devolvió enlace de aprobación");
    }

    public String extractCaptureId(JsonNode capturesNode) {
        if (capturesNode == null || !capturesNode.isArray() || capturesNode.isEmpty()) {
            return null;
        }
        return capturesNode.get(0).path("id").asText(null);
    }

    public String requiredText(JsonNode node, String field) {
        String value = node.path(field).asText(null);
        if (!StringUtils.hasText(value)) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "PayPal no devolvió el campo " + field);
        }
        return value;
    }

    // ── Invoice Number Generator ──────────────────────────────────────────────

    public String generarNumeroFactura() {
        return String.format("INV-%s-%s",
                LocalDateTime.now().toString().substring(0, 10).replace("-", ""),
                String.format("%04d", (int) (Math.random() * 10000)));
    }

    // ── Payment Method Checks ─────────────────────────────────────────────────

    public boolean esMetodoPayPal(MetodoPago metodoPago) {
        String nombre = metodoPago.getNombre();
        return StringUtils.hasText(nombre) &&
                nombre.trim().equalsIgnoreCase("paypal");
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private void putIfPresent(Map<String, Object> map, String key, String value) {
        if (value != null) {
            map.put(key, value);
        }
    }
}
