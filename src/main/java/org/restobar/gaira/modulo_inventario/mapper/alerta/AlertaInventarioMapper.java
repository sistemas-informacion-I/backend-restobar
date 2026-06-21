package org.restobar.gaira.modulo_inventario.mapper.alerta;

import java.util.HashMap;
import java.util.Map;

import org.restobar.gaira.modulo_inventario.dto.alerta.AlertaInventarioResponse;
import org.restobar.gaira.modulo_inventario.entity.AlertaInventario;
import org.springframework.stereotype.Component;

@Component
public class AlertaInventarioMapper {

    public AlertaInventarioResponse toResponse(AlertaInventario entity) {
        if (entity == null) return null;

        AlertaInventarioResponse response = new AlertaInventarioResponse();
        response.setIdAlerta(entity.getIdAlerta());
        response.setTipo(entity.getTipo());
        response.setEstado(entity.getEstado());
        response.setFechaGeneracion(entity.getFechaGeneracion());
        response.setFechaResolucion(entity.getFechaResolucion());

        if (entity.getSucursal() != null) {
            response.setIdSucursal(entity.getSucursal().getIdSucursal());
            response.setNombreSucursal(entity.getSucursal().getNombre());
        }

        if (entity.getStockSucursal() != null) {
            response.setIdStock(entity.getStockSucursal().getIdStock());
            response.setIdInventario(entity.getStockSucursal().getInventario().getIdInventario());
            response.setNombreInventario(entity.getStockSucursal().getInventario().getNombre());
            response.setCantidadActual(entity.getStockSucursal().getCantidad().toPlainString());
            response.setCantidadMinima(entity.getStockSucursal().getCantidadMinima().toPlainString());
        }

        if (entity.getLoteInventario() != null) {
            response.setIdLote(entity.getLoteInventario().getIdLote());
            response.setNumeroLote(entity.getLoteInventario().getNumeroLote());
            response.setFechaVencimiento(entity.getLoteInventario().getFechaVencimiento());
            response.setEstadoLote(entity.getLoteInventario().getEstado().name());
            if (entity.getLoteInventario().getStockSucursal() != null && entity.getLoteInventario().getStockSucursal().getInventario() != null) {
                response.setIdInventario(entity.getLoteInventario().getStockSucursal().getInventario().getIdInventario());
                response.setNombreInventario(entity.getLoteInventario().getStockSucursal().getInventario().getNombre());
                response.setCantidadActual(entity.getLoteInventario().getStockSucursal().getCantidad().toPlainString());
                response.setCantidadMinima(entity.getLoteInventario().getStockSucursal().getCantidadMinima().toPlainString());
            }
        }

        response.setNombreTipo(entity.getTipo() != null ? entity.getTipo().name() : null);
        response.setNombreEstado(entity.getEstado() != null ? entity.getEstado().name() : null);
        return response;
    }

    public Map<String, Object> toAuditMap(AlertaInventario entity) {
        if (entity == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idAlerta", entity.getIdAlerta());
        map.put("tipo", entity.getTipo());
        map.put("estado", entity.getEstado());
        map.put("fechaGeneracion", entity.getFechaGeneracion());
        map.put("fechaResolucion", entity.getFechaResolucion());
        if (entity.getSucursal() != null) {
            map.put("idSucursal", entity.getSucursal().getIdSucursal());
            map.put("nombreSucursal", entity.getSucursal().getNombre());
        }
        if (entity.getStockSucursal() != null) {
            map.put("idStock", entity.getStockSucursal().getIdStock());
            map.put("cantidadActual", entity.getStockSucursal().getCantidad());
            map.put("cantidadMinima", entity.getStockSucursal().getCantidadMinima());
        }
        if (entity.getLoteInventario() != null) {
            map.put("idLote", entity.getLoteInventario().getIdLote());
            map.put("numeroLote", entity.getLoteInventario().getNumeroLote());
            map.put("fechaVencimiento", entity.getLoteInventario().getFechaVencimiento());
            map.put("estadoLote", entity.getLoteInventario().getEstado());
        }
        return map;
    }
}
