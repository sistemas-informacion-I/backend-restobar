package org.restobar.gaira.modulo_inventario.dto.receta;

import java.math.BigDecimal;

import org.restobar.gaira.modulo_inventario.entity.Inventario.UnidadMedida;

import lombok.Builder;

@Builder
public record IngredienteRecetaResponse(
        Long idIngredienteReceta,
        Long idInventario,
        String nombreInventario,
        BigDecimal cantidad,
        UnidadMedida unidadMedida,
        String notas
) {
}
