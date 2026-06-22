package org.restobar.gaira.modulo_comercial.dto.caja;

import java.math.BigDecimal;

import org.restobar.gaira.modulo_comercial.entity.MovimientoCaja.Concepto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Movimiento manual ingresado por el cajero. Solo se permiten los conceptos
 * {@code INGRESO_EXTRA} (ingreso) y {@code RETIRO} (egreso); el resto de
 * conceptos provienen de operaciones automáticas de otros CU.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoManualRequest {

    @NotNull(message = "El concepto es requerido (INGRESO_EXTRA o RETIRO)")
    private Concepto concepto;

    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    private String descripcion;
}
