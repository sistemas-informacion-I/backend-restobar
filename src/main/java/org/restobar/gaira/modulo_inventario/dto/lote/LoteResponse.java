package org.restobar.gaira.modulo_inventario.dto.lote;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.restobar.gaira.modulo_inventario.entity.LoteInventario.EstadoLote;

import lombok.Data;

@Data
public class LoteResponse {
    private Long idLote;
    private Long idStock;
    private String numeroLote;
    private BigDecimal cantidad;
    private LocalDate fechaIngreso;
    private LocalDate fechaVencimiento;
    private BigDecimal precioCompra;
    private EstadoLote estado;
}
