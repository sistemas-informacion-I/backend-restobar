package org.restobar.gaira.modulo_inventario.dto.stock;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class StockSucursalResponse {
    private Long idStock;
    private Long idInventario;
    private String nombreInventario;
    private Long idSucursal;
    private String nombreSucursal;
    private BigDecimal cantidad;
    private BigDecimal cantidadMinima;
    private BigDecimal cantidadMaxima;
    private BigDecimal precioUnitario;
    private BigDecimal precioPromedio;
    private String ubicacionAlmacen;
    private Boolean activo;
}
