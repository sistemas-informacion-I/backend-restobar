package org.restobar.gaira.modulo_electronico.dto.carrito;

import org.restobar.gaira.modulo_electronico.dto.item.ItemCarritoResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CarritoResponse(
        Long idCarrito,
        Long idSucursal,
        String estado,
        List<ItemCarritoResponse> items,
        BigDecimal total,
        LocalDateTime fechaActualizacion
) {}
