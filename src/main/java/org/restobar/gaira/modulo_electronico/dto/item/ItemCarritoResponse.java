package org.restobar.gaira.modulo_carrito.dto.item;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ItemCarritoResponse(
        Long idItemCarrito,
        Long idProductoFinal,
        String nombreProducto,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal,
        String notasEspeciales,
        LocalDateTime fechaAgregado,
        boolean disponible
) {}
