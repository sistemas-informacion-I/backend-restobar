package org.restobar.gaira.modulo_comercial.dto.producto;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record ProductoSucursalResponse(
    Long idProductoFinal,
    String codigoProducto,
    String nombreProducto,
    Long idSucursal,
    String nombreSucursal,
    BigDecimal precio,
    Boolean disponible,
    Boolean activo
) {}
