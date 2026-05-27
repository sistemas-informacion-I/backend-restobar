package org.restobar.gaira.modulo_comercial.dto.compra;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.restobar.gaira.modulo_comercial.dto.detalleCompra.DetalleCompraResponse;
import org.restobar.gaira.modulo_comercial.entity.Compra.EstadoPago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraResponseDTO {

    private Long idCompra;

    private Long idProveedor;

    private String nombreProveedor; // empresa

    private Long idEmpleado;

    private String nombreEmpleado; 

    private String nroFactura;

    private LocalDate fechaCompra;

    private LocalDate fechaEntregaProgramada;

    private LocalDate fechaEntregaReal;

    private BigDecimal subTotal;

    private BigDecimal descuento;

    private BigDecimal impuesto;

    private BigDecimal total;

    private EstadoPago estadoPago;

    private LocalDate fechaLimitePago;

    private LocalDate fechaPago;

    private String observaciones;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<DetalleCompraResponse> detalles;
}
