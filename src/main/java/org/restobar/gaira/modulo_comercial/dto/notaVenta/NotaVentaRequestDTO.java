package org.restobar.gaira.modulo_comercial.dto.notaVenta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.restobar.gaira.modulo_comercial.dto.detalleNotaVenta.DetalleNotaVentaRequest;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotaVentaRequestDTO {

    @NotNull(message = "ID de cliente es requerido")
    private Long idCliente;

    @NotNull(message = "ID de empleado es requerido")
    private Long idEmpleado;

    @NotNull(message = "ID de sucursal es requerido")
    private Long idSucursal;

    @Size(max = 500, message = "Las observaciones no pueden superar 500 caracteres")
    private String observaciones;

    @Builder.Default
    private BigDecimal descuento = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal propina = BigDecimal.ZERO;

    @Size(max = 20, message = "NIT no puede superar 20 caracteres")
    private String nit;

    @Valid
    private List<DetalleNotaVentaRequest> detalles;
}