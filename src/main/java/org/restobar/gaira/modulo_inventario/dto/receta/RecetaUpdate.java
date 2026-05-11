package org.restobar.gaira.modulo_inventario.dto.receta;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RecetaUpdate(
        @NotNull(message = "El producto final es obligatorio")
        Long idProductoFinal,

        @NotNull(message = "La sucursal de referencia es obligatoria")
        Long idSucursalReferencia,

        @NotBlank(message = "El nombre de la receta es obligatorio")
        @Size(max = 150, message = "El nombre de la receta no puede superar 150 caracteres")
        String nombre,

        String descripcion,

        @PositiveOrZero(message = "El tiempo de preparación no puede ser negativo")
        Integer tiempoPreparacion,

        String instrucciones,

        @Size(max = 80, message = "La etiqueta de versión no puede superar 80 caracteres")
        String versionEtiqueta,

        LocalDate fechaVigenciaInicio,

        LocalDate fechaVigenciaFin,

        @NotNull(message = "La lista de ingredientes es obligatoria")
        @Size(min = 1, message = "La receta debe tener al menos un ingrediente")
        List<IngredienteRecetaRequest> ingredientes,

        Boolean activo
) {}