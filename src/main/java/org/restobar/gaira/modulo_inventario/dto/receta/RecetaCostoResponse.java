package org.restobar.gaira.modulo_inventario.dto.receta;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record RecetaCostoResponse(
        Long idReceta,
        Long idSucursal,
        String nombreSucursal,
        BigDecimal costoTotal
) {
}
