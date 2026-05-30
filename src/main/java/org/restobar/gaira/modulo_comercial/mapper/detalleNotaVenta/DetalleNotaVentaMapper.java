package org.restobar.gaira.modulo_comercial.mapper.detalleNotaVenta;

import org.restobar.gaira.modulo_comercial.dto.detalleNotaVenta.DetalleNotaVentaRequest;
import org.restobar.gaira.modulo_comercial.dto.detalleNotaVenta.DetalleNotaVentaResponse;
import org.restobar.gaira.modulo_comercial.entity.DetalleNotaVenta;
import org.restobar.gaira.modulo_comercial.entity.NotaVenta;
import org.restobar.gaira.modulo_comercial.entity.ProductoFinal;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DetalleNotaVentaMapper {

    public DetalleNotaVenta toEntity(DetalleNotaVentaRequest request, NotaVenta notaVenta, ProductoFinal productoFinal) {
        if (request == null) return null;

        DetalleNotaVenta detalle = DetalleNotaVenta.builder()
                .notaVenta(notaVenta)
                .productoFinal(productoFinal)
                .cantidad(request.getCantidad() != null ? request.getCantidad() : 1)
                .precioU(request.getPrecioU() != null ? request.getPrecioU() : java.math.BigDecimal.ZERO)
                .costoU(request.getCostoU())
                .descuento(request.getDescuento() != null ? request.getDescuento() : java.math.BigDecimal.ZERO)
                .descripcion(request.getDescripcion())
                .build();
        detalle.calcularSubTotal();
        return detalle;
    }

    public DetalleNotaVentaResponse toResponse(DetalleNotaVenta detalle) {
        if (detalle == null) return null;

        return DetalleNotaVentaResponse.builder()
                .idDetalleNotaVenta(detalle.getIdDetalleNotaVenta())
                .idProductoFinal(detalle.getProductoFinal().getIdProductoFinal())
                .nombreProducto(detalle.getProductoFinal() != null
                        ? detalle.getProductoFinal().getNombre()
                        : null)
                .cantidad(detalle.getCantidad())
                .precioU(detalle.getPrecioU())
                .costoU(detalle.getCostoU())
                .descuento(detalle.getDescuento())
                .subTotal(detalle.getSubTotal())
                .descripcion(detalle.getDescripcion())
                .build();
    }

    public Map<String, Object> toAuditMap(DetalleNotaVenta detalle) {
        if (detalle == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idDetalleNotaVenta", detalle.getIdDetalleNotaVenta());
        map.put("idProductoFinal", detalle.getProductoFinal().getIdProductoFinal());
        map.put("cantidad", detalle.getCantidad());
        map.put("precioU", detalle.getPrecioU());
        map.put("subTotal", detalle.getSubTotal());
        return map;
    }
}
	