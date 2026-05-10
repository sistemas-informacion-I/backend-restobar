package org.restobar.gaira.modulo_comercial.dto.compra;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.restobar.gaira.modulo_comercial.dto.detalleCompra.DetalleCompraRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraRequestDTO {

    @NotNull(message = "ID de proveedor es requerido")
    private Long idProveedor;

    @NotNull(message = "ID de empleado es requerido")
    private Long idEmpleado;

    @NotBlank(message = "Número de factura es requerido")
    @Size(max = 50, message = "El número de factura no puede superar 50 caracteres")
    private String nroFactura;

    @NotNull(message = "Fecha de compra es requerida")
    private LocalDate fechaCompra;

    private LocalDate fechaEntregaProgramada;

    private LocalDate fechaLimitePago;

    @Size(max = 500, message = "Las observaciones no pueden superar 500 caracteres")
    private String observaciones;

    @Builder.Default
    private BigDecimal descuento = BigDecimal.ZERO;

    @Valid
    private List<DetalleCompraRequest> detalles;
}
