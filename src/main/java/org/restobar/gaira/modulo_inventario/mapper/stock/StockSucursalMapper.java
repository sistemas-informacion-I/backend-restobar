package org.restobar.gaira.modulo_inventario.mapper.stock;

import java.util.HashMap;
import java.util.Map;

import java.math.BigDecimal;

import org.restobar.gaira.modulo_inventario.dto.stock.StockSucursalRequest;
import org.restobar.gaira.modulo_inventario.dto.stock.StockSucursalResponse;
import org.restobar.gaira.modulo_inventario.entity.Inventario;
import org.restobar.gaira.modulo_inventario.entity.StockSucursal;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.springframework.stereotype.Component;

@Component
public class StockSucursalMapper {

    public StockSucursal toEntity(StockSucursalRequest dto, Inventario inventario, Sucursal sucursal) {
        if (dto == null) return null;

        return StockSucursal.builder()
                .inventario(inventario)
                .sucursal(sucursal)
                .cantidad(BigDecimal.ZERO)
                .cantidadMinima(dto.getCantidadMinima() != null ? dto.getCantidadMinima() : BigDecimal.ZERO)
                .cantidadMaxima(dto.getCantidadMaxima())
                .ubicacionAlmacen(dto.getUbicacionAlmacen())
                .activo(true)
                .build();
    }

    public StockSucursalResponse toResponse(StockSucursal entity) {
        StockSucursalResponse response = new StockSucursalResponse();
        response.setIdStock(entity.getIdStock());
        response.setIdInventario(entity.getInventario().getIdInventario());
        response.setNombreInventario(entity.getInventario().getNombre());
        response.setIdSucursal(entity.getSucursal().getIdSucursal());
        response.setNombreSucursal(entity.getSucursal().getNombre());
        response.setCantidad(entity.getCantidad());
        response.setCantidadMinima(entity.getCantidadMinima());
        response.setCantidadMaxima(entity.getCantidadMaxima());
        response.setPrecioUnitario(entity.getPrecioUnitario());
        response.setPrecioPromedio(entity.getPrecioPromedio());
        response.setUbicacionAlmacen(entity.getUbicacionAlmacen());
        response.setActivo(entity.getActivo());
        return response;
    }

    public Map<String, Object> toAuditMap(StockSucursal entity) {
        if (entity == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idStock", entity.getIdStock());
        map.put("idInventario", entity.getInventario() != null ? entity.getInventario().getIdInventario() : null);
        map.put("nombreInventario", entity.getInventario() != null ? entity.getInventario().getNombre() : null);
        map.put("idSucursal", entity.getSucursal() != null ? entity.getSucursal().getIdSucursal() : null);
        map.put("nombreSucursal", entity.getSucursal() != null ? entity.getSucursal().getNombre() : null);
        map.put("cantidad", entity.getCantidad());
        map.put("cantidadMinima", entity.getCantidadMinima());
        map.put("cantidadMaxima", entity.getCantidadMaxima());
        map.put("precioUnitario", entity.getPrecioUnitario());
        map.put("precioPromedio", entity.getPrecioPromedio());
        map.put("ubicacionAlmacen", entity.getUbicacionAlmacen());
        map.put("activo", entity.getActivo());
        return map;
    }

    public Map<String, Object> toAuditMap(StockSucursalResponse response) {
        if (response == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idStock", response.getIdStock());
        map.put("idInventario", response.getIdInventario());
        map.put("nombreInventario", response.getNombreInventario());
        map.put("idSucursal", response.getIdSucursal());
        map.put("nombreSucursal", response.getNombreSucursal());
        map.put("cantidad", response.getCantidad());
        map.put("cantidadMinima", response.getCantidadMinima());
        map.put("cantidadMaxima", response.getCantidadMaxima());
        map.put("precioUnitario", response.getPrecioUnitario());
        map.put("precioPromedio", response.getPrecioPromedio());
        map.put("ubicacionAlmacen", response.getUbicacionAlmacen());
        map.put("activo", response.getActivo());
        return map;
    }
}
