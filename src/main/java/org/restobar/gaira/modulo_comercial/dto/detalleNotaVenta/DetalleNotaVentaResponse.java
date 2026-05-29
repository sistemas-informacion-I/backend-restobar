package org.restobar.gaira.modulo_comercial.dto.detalleNotaVenta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleNotaVentaResponse {

    private Long idDetalleNotaVenta;

    private Long idProductoFinal;

    private String nombreProducto;

    private Integer cantidad;

    private BigDecimal precioU;

    private BigDecimal costoU;

    private BigDecimal descuento;

    private BigDecimal subTotal;

    private String descripcion;
}
