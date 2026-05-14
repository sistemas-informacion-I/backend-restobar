package org.restobar.gaira.modulo_inventario.dto.receta;

import java.math.BigDecimal;

import org.restobar.gaira.modulo_inventario.entity.Inventario.UnidadMedida;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record IngredienteRecetaRequest(
        @NotNull(message = "El insumo es obligatorio")
        Long idInventario,

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a cero")
        BigDecimal cantidad,

        @NotNull(message = "La unidad de medida es obligatoria")
        UnidadMedida unidadMedida,

        @Size(max = 500, message = "Las notas no pueden superar 500 caracteres")
        String notas
) {
}
