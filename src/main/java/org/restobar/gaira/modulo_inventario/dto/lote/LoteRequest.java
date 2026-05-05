package org.restobar.gaira.modulo_inventario.dto.lote;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.restobar.gaira.modulo_inventario.entity.LoteInventario.EstadoLote;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoteRequest {
    @NotNull(message = "El ID de stock es obligatorio")
    private Long idStock;

    private String numeroLote;

    @NotNull(message = "La cantidad es obligatoria")
    private BigDecimal cantidad;

    private LocalDate fechaIngreso;
    private LocalDate fechaVencimiento;

    @NotNull(message = "El precio de compra es obligatorio")
    private BigDecimal precioCompra;

    private EstadoLote estado;
}
