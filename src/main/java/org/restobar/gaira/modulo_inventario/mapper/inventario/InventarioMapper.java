package org.restobar.gaira.modulo_inventario.mapper.inventario;

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

    public Map<String, Object> mapToAudit(Inventario entity) {
        return Map.of(
            "id", entity.getIdInventario(),
            "codigo", entity.getCodigo(),
            "nombre", entity.getNombre()
        );
    }
}
