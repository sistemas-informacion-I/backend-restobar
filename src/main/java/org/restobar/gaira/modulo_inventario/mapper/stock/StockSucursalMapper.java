package org.restobar.gaira.modulo_inventario.mapper.stock;

import java.util.Map;

import org.restobar.gaira.modulo_inventario.dto.stock.StockSucursalResponse;
import org.restobar.gaira.modulo_inventario.entity.StockSucursal;
import org.springframework.stereotype.Component;

@Component
public class StockSucursalMapper {

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

    public Map<String, Object> mapToAudit(StockSucursal entity) {
        return Map.of(
            "id", entity.getIdStock(),
            "insumo", entity.getInventario().getNombre(),
            "sucursal", entity.getSucursal().getNombre(),
            "cantidad", entity.getCantidad()
        );
    }
}
