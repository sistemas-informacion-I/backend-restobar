package org.restobar.gaira.modulo_comercial.mapper.producto;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.restobar.gaira.modulo_comercial.dto.producto.ProductoRequest;
import org.restobar.gaira.modulo_comercial.dto.producto.ProductoResponse;
import org.restobar.gaira.modulo_comercial.entity.Categoria;
import org.restobar.gaira.modulo_comercial.entity.ProductoFinal;
import org.restobar.gaira.modulo_comercial.repository.CategoriaRepository;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductoMapper {

    private final CategoriaRepository categoriaRepository;
    private final org.restobar.gaira.shared.storage.service.StorageService storageService;

    public ProductoFinal toEntity(ProductoRequest dto) {
        Categoria categoria = null;
        if (dto.idCategoria() != null) {
            categoria = categoriaRepository.findById(dto.idCategoria()).orElse(null);
        }

        boolean activo = dto.activo() == null ? true : dto.activo();

        return ProductoFinal.builder()
                .codigo(dto.codigo())
                .nombre(dto.nombre())
                .descripcion(dto.descripcion())
                .categoria(categoria)
                .tiempoPreparacion(dto.tiempoPreparacion())
                .imagenUrl(dto.imagenUrl())
                .activo(activo)
                .build();
    }

    public void updateEntity(ProductoFinal entity, ProductoRequest dto) {
        if (entity == null || dto == null) return;

        entity.setNombre(dto.nombre());
        entity.setDescripcion(dto.descripcion());

        if (dto.idCategoria() != null) {
            Categoria categoria = categoriaRepository.findById(dto.idCategoria()).orElse(null);
            entity.setCategoria(categoria);
        }

        entity.setTiempoPreparacion(dto.tiempoPreparacion());
        entity.setImagenUrl(dto.imagenUrl());
        if (dto.activo() != null) {
            entity.setActivo(dto.activo());
        }
    }

    public ProductoResponse toResponse(ProductoFinal entity) {
        if (entity == null) return null;
        return ProductoResponse.builder()
                .idProductoFinal(entity.getIdProductoFinal())
                .codigo(entity.getCodigo())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .idCategoria(entity.getCategoria() != null ? entity.getCategoria().getId() : null)
                .nombreCategoria(entity.getCategoria() != null ? entity.getCategoria().getNombre() : null)
                .tiempoPreparacion(entity.getTiempoPreparacion())
                .imagenUrl(entity.getImagenUrl() != null ? storageService.getUrl(entity.getImagenUrl()) : null)
                .activo(entity.isActivo())
                .fechaCreacion(entity.getFechaCreacion())
                .build();
    }

    public Map<String, Object> toAuditMap(ProductoFinal entity) {
        if (entity == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idProductoFinal", entity.getIdProductoFinal());
        map.put("codigo", entity.getCodigo());
        map.put("nombre", entity.getNombre());
        map.put("descripcion", entity.getDescripcion());
        map.put("idCategoria", entity.getCategoria() != null ? entity.getCategoria().getId() : null);
        map.put("tiempoPreparacion", entity.getTiempoPreparacion());
        map.put("imagenUrl", entity.getImagenUrl());
        map.put("activo", entity.isActivo());
        map.put("fechaCreacion", entity.getFechaCreacion());
        return map;
    }

    public Map<String, Object> toAuditMap(ProductoResponse response) {
        if (response == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idProductoFinal", response.idProductoFinal());
        map.put("codigo", response.codigo());
        map.put("nombre", response.nombre());
        map.put("descripcion", response.descripcion());
        map.put("idCategoria", response.idCategoria());
        map.put("tiempoPreparacion", response.tiempoPreparacion());
        map.put("imagenUrl", response.imagenUrl());
        map.put("activo", response.activo());
        map.put("fechaCreacion", response.fechaCreacion());
        return map;
    }
}
