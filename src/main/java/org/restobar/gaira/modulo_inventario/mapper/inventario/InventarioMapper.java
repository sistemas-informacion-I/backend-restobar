package org.restobar.gaira.modulo_inventario.mapper.inventario;

import java.util.HashMap;
import java.util.Map;

import org.restobar.gaira.modulo_inventario.dto.inventario.InventarioRequest;
import org.restobar.gaira.modulo_inventario.dto.inventario.InventarioResponse;
import org.restobar.gaira.modulo_inventario.entity.Inventario;
import org.springframework.stereotype.Component;

@Component
public class InventarioMapper {

    public Inventario toEntity(InventarioRequest dto) {
        return Inventario.builder()
                .codigo(dto.getCodigo())
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .unidadMedida(dto.getUnidadMedida())
                .marca(dto.getMarca())
                .esRehutilizable(dto.getEsRehutilizable())
                .activo(dto.getActivo())
                .build();
    }

    public void updateEntity(Inventario entity, InventarioRequest dto) {
        if (entity == null || dto == null) return;

        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setUnidadMedida(dto.getUnidadMedida());
        entity.setMarca(dto.getMarca());
        entity.setEsRehutilizable(dto.getEsRehutilizable());
        entity.setActivo(dto.getActivo());
    }

    public InventarioResponse toResponse(Inventario entity) {
        InventarioResponse response = new InventarioResponse();
        response.setIdInventario(entity.getIdInventario());
        response.setCodigo(entity.getCodigo());
        response.setNombre(entity.getNombre());
        response.setDescripcion(entity.getDescripcion());
        response.setUnidadMedida(entity.getUnidadMedida());
        response.setMarca(entity.getMarca());
        response.setEsRehutilizable(entity.isEsRehutilizable());
        response.setActivo(entity.isActivo());
        response.setFechaCreacion(entity.getFechaCreacion());
        return response;
    }

    public Map<String, Object> toAuditMap(Inventario entity) {
        if (entity == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idInventario", entity.getIdInventario());
        map.put("codigo", entity.getCodigo());
        map.put("nombre", entity.getNombre());
        map.put("descripcion", entity.getDescripcion());
        map.put("unidadMedida", entity.getUnidadMedida());
        map.put("marca", entity.getMarca());
        map.put("esRehutilizable", entity.isEsRehutilizable());
        map.put("activo", entity.isActivo());
        map.put("fechaCreacion", entity.getFechaCreacion());
        return map;
    }

    public Map<String, Object> toAuditMap(InventarioResponse response) {
        if (response == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idInventario", response.getIdInventario());
        map.put("codigo", response.getCodigo());
        map.put("nombre", response.getNombre());
        map.put("descripcion", response.getDescripcion());
        map.put("unidadMedida", response.getUnidadMedida());
        map.put("marca", response.getMarca());
        map.put("esRehutilizable", response.getEsRehutilizable());
        map.put("activo", response.getActivo());
        map.put("fechaCreacion", response.getFechaCreacion());
        return map;
    }
}
