package org.restobar.gaira.modulo_comercial.dto.notaVenta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.restobar.gaira.modulo_comercial.dto.detalleNotaVenta.DetalleNotaVentaResponse;
import org.restobar.gaira.modulo_comercial.entity.NotaVenta.Estado;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotaVentaResponseDTO {

    private Long idNotaVenta;

    private Long idCliente;

    private String nombreCliente;

    private Long idEmpleado;

    private String nombreEmpleado;

    private Long idSucursal;

    private String nombreSucursal;

    private LocalDate fechaEmision;

    private BigDecimal subTotal;

    private BigDecimal descuento;

    private BigDecimal impuesto;

    private BigDecimal propina;

    private BigDecimal total;

    private Estado estado;

    private String observaciones;

    private LocalDate fechaPago;

    private String nit;

    private List<DetalleNotaVentaResponse> detalles;
}
