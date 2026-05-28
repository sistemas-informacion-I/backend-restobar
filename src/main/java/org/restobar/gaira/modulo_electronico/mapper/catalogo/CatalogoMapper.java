package org.restobar.gaira.modulo_electronico.mapper.catalogo;

import java.util.HashMap;
import java.util.Map;

import org.restobar.gaira.modulo_electronico.dto.catalogo.CatalogoProductoResponse;
import org.restobar.gaira.modulo_comercial.entity.ProductoSucursal;
import org.springframework.stereotype.Component;

@Component
public class CatalogoMapper {

    public CatalogoProductoResponse toResponse(ProductoSucursal ps, boolean disponibleFinal, boolean hayStock) {
        if (ps == null) return null;
        var producto = ps.getProductoFinal();
        var categoria = producto != null ? producto.getCategoria() : null;

        return CatalogoProductoResponse.builder()
                .idProductoFinal(ps.getIdProductoFinal())
                .codigo(producto != null ? producto.getCodigo() : null)
                .nombre(producto != null ? producto.getNombre() : null)
                .descripcion(producto != null ? producto.getDescripcion() : null)
                .imagenUrl(producto != null ? producto.getImagenUrl() : null)
                .tiempoPreparacion(producto != null ? producto.getTiempoPreparacion() : null)
                .idCategoria(categoria != null ? categoria.getId() : null)
                .nombreCategoria(categoria != null ? categoria.getNombre() : null)
                .precio(ps.getPrecio())
                .disponible(disponibleFinal)
                .hayStock(hayStock)
                .build();
    }

    public Map<String, Object> toAuditMap(ProductoSucursal ps) {
        if (ps == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idProductoFinal", ps.getIdProductoFinal());
        map.put("idSucursal", ps.getIdSucursal());
        map.put("precio", ps.getPrecio());
        map.put("disponible", ps.isDisponible());
        map.put("activo", ps.isActivo());
        return map;
    }

    public Map<String, Object> toAuditMap(CatalogoProductoResponse response) {
        if (response == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idProductoFinal", response.idProductoFinal());
        map.put("nombre", response.nombre());
        map.put("precio", response.precio());
        map.put("disponible", response.disponible());
        map.put("hayStock", response.hayStock());
        return map;
    }
}
