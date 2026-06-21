package org.restobar.gaira.modulo_comercial.dto.caja;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.restobar.gaira.modulo_comercial.entity.MovimientoCaja.Concepto;
import org.restobar.gaira.modulo_comercial.entity.MovimientoCaja.Tipo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoCajaResponse {

    private Long idMovimiento;

    private Long idCaja;

    private Tipo tipo;

    private Concepto concepto;

    private BigDecimal monto;

    private String descripcion;

    private Long idEmpleado;

    private String nombreEmpleado;

    private Long referenciaId;

    private LocalDateTime fecha;
}
