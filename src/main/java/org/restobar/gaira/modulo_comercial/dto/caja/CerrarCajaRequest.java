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
public class CerrarCajaRequest {

    /** Saldo real (dinero físico contado) declarado por el cajero al arqueo. */
    @NotNull(message = "El saldo real (monto final) es requerido")
    @DecimalMin(value = "0.0", inclusive = true, message = "El saldo real no puede ser negativo")
    private BigDecimal montoFinal;

    @Size(max = 500, message = "La observación no puede superar 500 caracteres")
    private String observacion;
}
