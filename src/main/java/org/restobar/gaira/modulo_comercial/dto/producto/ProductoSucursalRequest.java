package org.restobar.gaira.modulo_comercial.dto.producto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ProductoSucursalRequest(
    @NotNull(message = "El ID de sucursal es obligatorio")
    Long idSucursal,

    @NotNull(message = "El precio es obligatorio")
    BigDecimal precio,

    Boolean disponible,
    Boolean activo
) {}
