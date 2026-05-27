package org.restobar.gaira.modulo_inventario.dto.receta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Builder;

@Builder
public record RecetaResponse(
        Long idReceta,
        Long idProductoFinal,
        String nombreProductoFinal,
        Long idSucursalReferencia,
        String nombreSucursalReferencia,
        String nombre,
        String descripcion,
        Integer tiempoPreparacion,
        String instrucciones,
        String versionEtiqueta,
        LocalDate fechaVigenciaInicio,
        LocalDate fechaVigenciaFin,
        BigDecimal costoTotal,
        Boolean activo,
        List<IngredienteRecetaResponse> ingredientes
) {}