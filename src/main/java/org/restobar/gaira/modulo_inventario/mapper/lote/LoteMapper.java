package org.restobar.gaira.modulo_inventario.mapper.lote;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.restobar.gaira.modulo_inventario.dto.lote.LoteRequest;
import org.restobar.gaira.modulo_inventario.dto.lote.LoteResponse;
import org.restobar.gaira.modulo_inventario.dto.stock.StockAjusteRequest;
import org.restobar.gaira.modulo_inventario.entity.LoteInventario;
import org.restobar.gaira.modulo_inventario.entity.LoteInventario.EstadoLote;
import org.restobar.gaira.modulo_inventario.entity.StockSucursal;
import org.springframework.stereotype.Component;

@Component
public class LoteMapper {

    public LoteInventario toEntity(LoteRequest dto, StockSucursal stock) {
        if (dto == null) return null;

        return LoteInventario.builder()
                .stockSucursal(stock)
                .numeroLote(dto.getNumeroLote())
                .cantidad(dto.getCantidad())
                .fechaIngreso(dto.getFechaIngreso() != null ? dto.getFechaIngreso() : LocalDate.now())
                .fechaVencimiento(dto.getFechaVencimiento())
                .precioCompra(dto.getPrecioCompra())
                .estado(dto.getEstado() != null ? dto.getEstado() : EstadoLote.DISPONIBLE)
                .build();
    }

    public LoteInventario toEntity(StockAjusteRequest dto, StockSucursal stock) {
        if (dto == null) return null;

        return LoteInventario.builder()
                .stockSucursal(stock)
                .numeroLote(dto.getNumeroLote())
                .cantidad(dto.getCantidad())
                .fechaIngreso(dto.getFechaIngreso() != null ? dto.getFechaIngreso() : LocalDate.now())
                .fechaVencimiento(dto.getFechaVencimiento())
                .precioCompra(dto.getPrecioCompra())
                .estado(EstadoLote.DISPONIBLE)
                .build();
    }

    public LoteResponse toResponse(LoteInventario entity) {
        LoteResponse response = new LoteResponse();
        response.setIdLote(entity.getIdLote());
        response.setIdStock(entity.getStockSucursal().getIdStock());
        response.setNumeroLote(entity.getNumeroLote());
        response.setCantidad(entity.getCantidad());
        response.setFechaIngreso(entity.getFechaIngreso());
        response.setFechaVencimiento(entity.getFechaVencimiento());
        response.setPrecioCompra(entity.getPrecioCompra());
        response.setEstado(entity.getEstado());
        return response;
    }

    public Map<String, Object> toAuditMap(LoteInventario entity) {
        if (entity == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idLote", entity.getIdLote());
        map.put("idStock", entity.getStockSucursal() != null ? entity.getStockSucursal().getIdStock() : null);
        map.put("numeroLote", entity.getNumeroLote());
        map.put("cantidad", entity.getCantidad());
        map.put("fechaIngreso", entity.getFechaIngreso());
        map.put("fechaVencimiento", entity.getFechaVencimiento());
        map.put("precioCompra", entity.getPrecioCompra());
        map.put("estado", entity.getEstado());
        return map;
    }

    public Map<String, Object> toAuditMap(LoteResponse response) {
        if (response == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idLote", response.getIdLote());
        map.put("idStock", response.getIdStock());
        map.put("numeroLote", response.getNumeroLote());
        map.put("cantidad", response.getCantidad());
        map.put("fechaIngreso", response.getFechaIngreso());
        map.put("fechaVencimiento", response.getFechaVencimiento());
        map.put("precioCompra", response.getPrecioCompra());
        map.put("estado", response.getEstado());
        return map;
    }
}
