package org.restobar.gaira.modulo_electronico.mapper.pago;

import org.restobar.gaira.modulo_comercial.entity.DetalleNotaVenta;
import org.restobar.gaira.modulo_comercial.entity.NotaVenta;
import org.restobar.gaira.modulo_operaciones.entity.Comanda;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class NotaVentaMapper {

    public Map<String, Object> toResponseMap(NotaVenta notaVenta) {
        Comanda comanda = notaVenta.getComanda();

        Map<String, Object> datosAdicionales = notaVenta.getTransaccionesOnline().isEmpty()
                ? null
                : notaVenta.getTransaccionesOnline().get(0).getDatosAdicionales();

        Map<String, Object> response = new HashMap<>();
        response.put("idNotaVenta", notaVenta.getIdNotaVenta());
        response.put("numeroComanda", comanda != null ? comanda.getNumeroComanda() : null);
        response.put("idComanda", comanda != null ? comanda.getIdComanda() : null);
        response.put("fechaEmision", notaVenta.getFechaEmision());
        response.put("fechaPago", notaVenta.getFechaPago());
        response.put("subtotal", notaVenta.getSubtotal());
        response.put("impuesto", notaVenta.getImpuesto());
        response.put("total", notaVenta.getTotal());
        response.put("estado", notaVenta.getEstado());
        response.put("nitCliente", notaVenta.getNitCliente());
        response.put("observaciones", notaVenta.getObservaciones());
        response.put("nombreMetodoPago", notaVenta.getMetodoPago() != null ? notaVenta.getMetodoPago().getNombre() : null);

        if (datosAdicionales != null) {
            response.put("customerName", datosAdicionales.get("customer_name"));
            response.put("customerEmail", datosAdicionales.get("customer_email"));
            response.put("customerPhone", datosAdicionales.get("customer_phone"));
            response.put("shippingAddress", datosAdicionales.get("shipping_address"));
            response.put("shippingCity", datosAdicionales.get("shipping_city"));
            response.put("shippingState", datosAdicionales.get("shipping_state"));
            response.put("shippingZip", datosAdicionales.get("shipping_zip"));
            response.put("shippingNotes", datosAdicionales.get("shipping_notes"));
            response.put("invoiceNumber", datosAdicionales.get("invoice_number"));
        }

        List<Map<String, Object>> detalles = notaVenta.getDetalleNotaVentas().stream()
                .map(this::toDetalleMap)
                .collect(Collectors.toList());
        response.put("detalles", detalles);

        return response;
    }

    private Map<String, Object> toDetalleMap(DetalleNotaVenta d) {
        Map<String, Object> item = new HashMap<>();
        item.put("idDetalle", d.getIdDetalleNotaVenta());
        item.put("idProductoFinal", d.getProductoFinal() != null ? d.getProductoFinal().getIdProductoFinal() : null);
        item.put("nombreProducto", d.getProductoFinal() != null ? d.getProductoFinal().getNombre() : "Producto");
        item.put("cantidad", d.getCantidad());
        item.put("precioUnitario", d.getPrecioUnitario());
        item.put("subtotal", d.getSubtotal());
        return item;
    }
}
