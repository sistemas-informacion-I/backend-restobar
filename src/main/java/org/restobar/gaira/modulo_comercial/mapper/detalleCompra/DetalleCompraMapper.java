package org.restobar.gaira.modulo_comercial.mapper.detalleCompra;

import org.restobar.gaira.modulo_comercial.dto.detalleCompra.DetalleCompraRequest;
import org.restobar.gaira.modulo_comercial.dto.detalleCompra.DetalleCompraResponse;
import org.restobar.gaira.modulo_comercial.entity.Compra;
import org.restobar.gaira.modulo_comercial.entity.DetalleCompra;
import org.restobar.gaira.modulo_inventario.entity.StockSucursal;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class DetalleCompraMapper {

    // Convierte un request + entidades resueltas a DetalleCompra, calculando el subTotal
    public DetalleCompra toEntity(DetalleCompraRequest request, Compra compra, StockSucursal stock) {
        if (request == null) return null;

        DetalleCompra detalle = DetalleCompra.builder()
                .compra(compra)
                .stock(stock)
                .cantidad(request.getCantidad() != null ? request.getCantidad() : 1)
                .precioUnitario(request.getPrecioUnitario() != null ? request.getPrecioUnitario() : BigDecimal.ZERO)
                .build();
        detalle.calcularSubTotal();
        return detalle;
    }

    // Convierte una entidad DetalleCompra a su response, obteniendo el nombre del producto desde el inventario
    public DetalleCompraResponse toResponse(DetalleCompra detalle) {
        if (detalle == null) return null;

        return DetalleCompraResponse.builder()
                .idDetalleCompra(detalle.getIdDetalleCompra())
                .idStock(detalle.getStock().getIdStock())
                .nombreProducto(detalle.getStock() != null && detalle.getStock().getInventario() != null
                        ? detalle.getStock().getInventario().getNombre()
                        : null)
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subTotal(detalle.getSubTotal())
                .build();
    }

    // Construye un mapa con los datos relevantes del detalle para auditoría
    public Map<String, Object> toAuditMap(DetalleCompra detalle) {
        if (detalle == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idDetalleCompra", detalle.getIdDetalleCompra());
        map.put("idStock", detalle.getStock().getIdStock());
        map.put("cantidad", detalle.getCantidad());
        map.put("precioUnitario", detalle.getPrecioUnitario());
        map.put("subTotal", detalle.getSubTotal());
        return map;
    }
}
