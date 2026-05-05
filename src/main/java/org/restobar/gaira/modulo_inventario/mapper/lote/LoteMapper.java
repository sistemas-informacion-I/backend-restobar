package org.restobar.gaira.modulo_inventario.mapper.lote;

import java.util.Map;

import org.restobar.gaira.modulo_inventario.dto.lote.LoteResponse;
import org.restobar.gaira.modulo_inventario.entity.LoteInventario;
import org.springframework.stereotype.Component;

@Component
public class LoteMapper {

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

    public Map<String, Object> mapToAudit(LoteInventario entity) {
        return Map.of(
            "id", entity.getIdLote(),
            "lote", entity.getNumeroLote() != null ? entity.getNumeroLote() : "S/N",
            "cantidad", entity.getCantidad(),
            "estado", entity.getEstado()
        );
    }
}
