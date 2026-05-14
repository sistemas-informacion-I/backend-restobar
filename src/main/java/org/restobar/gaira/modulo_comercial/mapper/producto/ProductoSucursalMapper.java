package org.restobar.gaira.modulo_comercial.mapper.producto;

import java.util.HashMap;
import java.util.Map;
import org.restobar.gaira.modulo_comercial.dto.producto.ProductoSucursalRequest;
import org.restobar.gaira.modulo_comercial.dto.producto.ProductoSucursalResponse;
import org.restobar.gaira.modulo_comercial.entity.ProductoSucursal;
import org.springframework.stereotype.Component;

@Component
public class ProductoSucursalMapper {

    public ProductoSucursal toEntity(Long idProductoFinal, ProductoSucursalRequest dto) {
        return ProductoSucursal.builder()
                .idProductoFinal(idProductoFinal)
                .idSucursal(dto.idSucursal())
                .precio(dto.precio())
                .disponible(dto.disponible() == null ? true : dto.disponible())
                .activo(dto.activo() == null ? true : dto.activo())
                .build();
    }

    public void updateEntity(ProductoSucursal entity, ProductoSucursalRequest dto) {
        if (entity == null || dto == null) return;

        entity.setPrecio(dto.precio());
        if (dto.disponible() != null) entity.setDisponible(dto.disponible());
        if (dto.activo() != null) entity.setActivo(dto.activo());
    }

    public ProductoSucursalResponse toResponse(ProductoSucursal entity) {
        if (entity == null) return null;
        return ProductoSucursalResponse.builder()
                .idProductoFinal(entity.getIdProductoFinal())
                .idSucursal(entity.getIdSucursal())
                .nombreSucursal(entity.getSucursal() != null ? entity.getSucursal().getNombre() : null)
                .nombreProducto(entity.getProductoFinal() != null ? entity.getProductoFinal().getNombre() : null)
                .codigoProducto(entity.getProductoFinal() != null ? entity.getProductoFinal().getCodigo() : null)
                .precio(entity.getPrecio())
                .disponible(entity.isDisponible())
                .activo(entity.isActivo())
                .build();
    }

    public Map<String, Object> toAuditMap(ProductoSucursal entity) {
        if (entity == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idProductoFinal", entity.getIdProductoFinal());
        map.put("idSucursal", entity.getIdSucursal());
        map.put("precio", entity.getPrecio());
        map.put("disponible", entity.isDisponible());
        map.put("activo", entity.isActivo());
        return map;
    }
}
