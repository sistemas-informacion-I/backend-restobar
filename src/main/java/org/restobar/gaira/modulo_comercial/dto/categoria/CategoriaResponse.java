package org.restobar.gaira.modulo_comercial.dto.categoria;

public record CategoriaResponse(
        Long idCategoria,
        String nombre,
        String descripcion,
        Long idCategoriaPadre,
        String nombreCategoriaPadre,
        Integer nivel,
        Boolean activo
) {}
