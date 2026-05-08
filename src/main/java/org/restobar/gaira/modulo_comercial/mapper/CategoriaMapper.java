package org.restobar.gaira.modulo_comercial.mapper;

import java.util.HashMap;
import java.util.Map;

import org.restobar.gaira.modulo_comercial.dto.categoria.CategoriaCreate;
import org.restobar.gaira.modulo_comercial.dto.categoria.CategoriaResponse;
import org.restobar.gaira.modulo_comercial.entity.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    public CategoriaResponse toResponse(Categoria categoria) {
        if (categoria == null) return null;

        Categoria padre = categoria.getCategoriaPadre();

        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                padre != null ? padre.getId() : null,
                padre != null ? padre.getNombre() : null,
                categoria.getNivel(),
                categoria.getActivo());
    }

    public Categoria toEntity(CategoriaCreate create, Categoria padre) {
        if (create == null) return null;

        int nivel = (padre == null) ? 1 : padre.getNivel() + 1;

        return Categoria.builder()
                .nombre(create.nombre().trim())
                .descripcion(create.descripcion() != null ? create.descripcion().trim() : null)
                .categoriaPadre(padre)
                .nivel(nivel)
                .activo(true)
                .build();
    }

    public Map<String, Object> toAuditMap(Categoria categoria) {
        if (categoria == null) return Map.of();

        Map<String, Object> map = new HashMap<>();
        map.put("idCategoria", categoria.getId());
        map.put("nombre", categoria.getNombre());
        map.put("descripcion", categoria.getDescripcion());
        map.put("idCategoriaPadre",
                categoria.getCategoriaPadre() != null ? categoria.getCategoriaPadre().getId() : null);
        map.put("nivel", categoria.getNivel());
        map.put("activo", categoria.getActivo());
        return map;
    }

    public Map<String, Object> toAuditMap(CategoriaResponse response) {
        if (response == null) return Map.of();

        Map<String, Object> map = new HashMap<>();
        map.put("idCategoria", response.idCategoria());
        map.put("nombre", response.nombre());
        map.put("descripcion", response.descripcion());
        map.put("idCategoriaPadre", response.idCategoriaPadre());
        map.put("nivel", response.nivel());
        map.put("activo", response.activo());
        return map;
    }
}
