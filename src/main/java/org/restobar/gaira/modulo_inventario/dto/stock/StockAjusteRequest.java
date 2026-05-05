package org.restobar.gaira.modulo_inventario.dto.stock;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockAjusteRequest {

    @NotNull
    private Long idInventario;

    @NotNull
    private BigDecimal cantidad;

    private String numeroLote;

    private LocalDate fechaIngreso;

    private LocalDate fechaVencimiento;

    private BigDecimal precioCompra;

    private Long idLote;
}
