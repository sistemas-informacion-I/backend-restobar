package org.restobar.gaira.modulo_comercial.dto.detalleCompra;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleCompraRequest {

    @NotNull(message = "ID de stock es requerido")
    private Long idStock;

    @NotNull(message = "Cantidad es requerida")
    @Positive(message = "La cantidad debe ser mayor a 0")
    @Builder.Default
    private Integer cantidad = 1;

    @NotNull(message = "Precio unitario es requerido")
    @Positive(message = "El precio unitario debe ser mayor a 0")
    private BigDecimal precioUnitario;
}
