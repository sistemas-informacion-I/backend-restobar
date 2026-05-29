package org.restobar.gaira.modulo_comercial.mapper.notaVenta;

import org.restobar.gaira.modulo_acceso.entity.Cliente;
import org.restobar.gaira.modulo_acceso.entity.Empleado;
import org.restobar.gaira.modulo_comercial.dto.detalleNotaVenta.DetalleNotaVentaResponse;
import org.restobar.gaira.modulo_comercial.dto.notaVenta.NotaVentaRequestDTO;
import org.restobar.gaira.modulo_comercial.dto.notaVenta.NotaVentaResponseDTO;
import org.restobar.gaira.modulo_comercial.entity.DetalleNotaVenta;
import org.restobar.gaira.modulo_comercial.entity.NotaVenta;
import org.restobar.gaira.modulo_comercial.mapper.detalleNotaVenta.DetalleNotaVentaMapper;
import org.restobar.gaira.modulo_operaciones.entity.Comanda;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class NotaVentaMapper {

    private final DetalleNotaVentaMapper detalleNotaVentaMapper;

    public NotaVentaMapper(DetalleNotaVentaMapper detalleNotaVentaMapper) {
        this.detalleNotaVentaMapper = detalleNotaVentaMapper;
    }

    public NotaVenta toEntity(NotaVentaRequestDTO request, Cliente cliente, Empleado empleado, Sucursal sucursal,
                              List<DetalleNotaVenta> detalles) {
        if (request == null) return null;

        NotaVenta notaVenta = NotaVenta.builder()
                .cliente(cliente)
                .empleado(empleado)
                .sucursal(sucursal)
                .observaciones(request.getObservaciones() != null ? request.getObservaciones().trim() : null)
                .descuento(request.getDescuento() != null ? request.getDescuento() : BigDecimal.ZERO)
                .propina(request.getPropina() != null ? request.getPropina() : BigDecimal.ZERO)
                .nit(request.getNit() != null ? request.getNit().trim() : null)
                .build();

        if (detalles != null) {
            detalles.forEach(d -> d.setNotaVenta(notaVenta));
            notaVenta.setDetalles(detalles);
        }

        return notaVenta;
    }

    public NotaVentaResponseDTO toResponse(NotaVenta notaVenta) {
        if (notaVenta == null) return null;

        List<DetalleNotaVentaResponse> detallesResponse = Collections.emptyList();
        if (notaVenta.getDetalles() != null) {
            detallesResponse = notaVenta.getDetalles().stream()
                    .map(detalleNotaVentaMapper::toResponse)
                    .collect(Collectors.toList());
        }

        String nombreCliente = null;
        if (notaVenta.getCliente() != null && notaVenta.getCliente().getUsuario() != null) {
            nombreCliente = notaVenta.getCliente().getUsuario().getNombre()
                    + " " + notaVenta.getCliente().getUsuario().getApellido();
        }

        String nombreEmpleado = null;
        if (notaVenta.getEmpleado() != null && notaVenta.getEmpleado().getUsuario() != null) {
            nombreEmpleado = notaVenta.getEmpleado().getUsuario().getNombre()
                    + " " + notaVenta.getEmpleado().getUsuario().getApellido();
        }

        return NotaVentaResponseDTO.builder()
                .idNotaVenta(notaVenta.getIdNotaVenta())
                .idCliente(notaVenta.getCliente().getIdCliente())
                .nombreCliente(nombreCliente)
                .idEmpleado(notaVenta.getEmpleado().getIdEmpleado())
                .nombreEmpleado(nombreEmpleado)
                .idSucursal(notaVenta.getSucursal().getIdSucursal())
                .nombreSucursal(notaVenta.getSucursal().getNombre())
                .fechaEmision(notaVenta.getFechaEmision())
                .subTotal(notaVenta.getSubTotal())
                .descuento(notaVenta.getDescuento())
                .impuesto(notaVenta.getImpuesto())
                .propina(notaVenta.getPropina())
                .total(notaVenta.getTotal())
                .estado(notaVenta.getEstado())
                .observaciones(notaVenta.getObservaciones())
                .fechaPago(notaVenta.getFechaPago())
                .nit(notaVenta.getNit())
                .detalles(detallesResponse)
                .build();
    }

    public void updateEntityFromDto(NotaVenta notaVenta, NotaVentaRequestDTO request,
                                    Cliente cliente, Empleado empleado, Sucursal sucursal) {
        if (notaVenta == null || request == null) return;

        notaVenta.setCliente(cliente);
        notaVenta.setEmpleado(empleado);
        notaVenta.setSucursal(sucursal);
        notaVenta.setObservaciones(request.getObservaciones() != null ? request.getObservaciones().trim() : null);
        notaVenta.setDescuento(request.getDescuento() != null ? request.getDescuento() : BigDecimal.ZERO);
        notaVenta.setPropina(request.getPropina() != null ? request.getPropina() : BigDecimal.ZERO);
        notaVenta.setNit(request.getNit() != null ? request.getNit().trim() : null);
    }

    public Map<String, Object> toAuditMap(NotaVenta notaVenta) {
        if (notaVenta == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idNotaVenta", notaVenta.getIdNotaVenta());
        map.put("idCliente", notaVenta.getCliente().getIdCliente());
        map.put("idEmpleado", notaVenta.getEmpleado().getIdEmpleado());
        map.put("idSucursal", notaVenta.getSucursal().getIdSucursal());
        map.put("fechaEmision", notaVenta.getFechaEmision() != null ? notaVenta.getFechaEmision().toString() : null);
        map.put("subTotal", notaVenta.getSubTotal());
        map.put("descuento", notaVenta.getDescuento());
        map.put("impuesto", notaVenta.getImpuesto());
        map.put("total", notaVenta.getTotal());
        map.put("estado", notaVenta.getEstado() != null ? notaVenta.getEstado().name() : null);
        return map;
    }

    public Map<String, Object> toAuditMap(NotaVentaResponseDTO dto) {
        if (dto == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idNotaVenta", dto.getIdNotaVenta());
        map.put("idCliente", dto.getIdCliente());
        map.put("idEmpleado", dto.getIdEmpleado());
        map.put("idSucursal", dto.getIdSucursal());
        map.put("fechaEmision", dto.getFechaEmision() != null ? dto.getFechaEmision().toString() : null);
        map.put("subTotal", dto.getSubTotal());
        map.put("descuento", dto.getDescuento());
        map.put("impuesto", dto.getImpuesto());
        map.put("total", dto.getTotal());
        map.put("estado", dto.getEstado() != null ? dto.getEstado().name() : null);
        return map;
    }

    /**
     * Convierte NotaVenta a Map para respuesta de endpoints de pagos en línea.
     * Incluye datos de transacci&oacute;n online, m&eacute;todo de pago y datos adicionales del cliente
     * requeridos por la funcionalidad de pagos.
     */
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
        response.put("subtotal", notaVenta.getSubTotal());
        response.put("impuesto", notaVenta.getImpuesto());
        response.put("total", notaVenta.getTotal());
        response.put("estado", notaVenta.getEstado());
        response.put("nitCliente", notaVenta.getNit());
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

        List<Map<String, Object>> detalles = notaVenta.getDetalles().stream()
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
        item.put("precioUnitario", d.getPrecioU());
        item.put("subtotal", d.getSubTotal());
        return item;
    }
}