package org.restobar.gaira.modulo_inventario.dto.stock;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockSucursalRequest {
    @NotNull(message = "El ID de inventario es obligatorio")
    private Long idInventario;

    @NotNull(message = "El ID de sucursal es obligatorio")
    private Long idSucursal;

    private BigDecimal cantidadMinima;
    private BigDecimal cantidadMaxima;
    private String ubicacionAlmacen;
}
