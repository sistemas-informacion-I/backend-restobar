package org.restobar.gaira.modulo_comercial.dto.caja;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AbrirCajaRequest {

    /**
     * Sucursal en la que se abre la caja. Para empleados (Cajero/Admin) es opcional:
     * se toma su sucursal asignada. Para Superusuario es obligatorio.
     */
    private Long idSucursal;

    @NotNull(message = "El monto inicial es requerido")
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto inicial no puede ser negativo")
    private BigDecimal montoInicial;

    @Size(max = 500, message = "La observación no puede superar 500 caracteres")
    private String observacion;
}
