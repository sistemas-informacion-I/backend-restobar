package org.restobar.gaira.modulo_comercial.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.restobar.gaira.modulo_acceso.entity.Empleado;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * CU22 – Tabla core de flujos de dinero. Cada registro representa un ingreso o
 * egreso asociado a una sesión de {@link Caja}. Por regla de negocio, ningún
 * otro CU inserta aquí directamente: todos pasan por el CajaService.
 */
@Entity
@Table(name = "movimiento_caja", schema = "public")
@Getter
@Setter
@ToString(exclude = { "caja", "empleado" })
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoCaja {

    public enum Tipo {
        INGRESO,
        EGRESO
    }

    public enum Concepto {
        VENTA,          // Ingreso automático por nota_venta (CU15)
        COMPRA,         // Egreso automático por compra pagada (CU12)
        NOTA_SALIDA,    // Egreso automático por nota de salida (CU17)
        INGRESO_EXTRA,  // Ingreso manual del cajero
        RETIRO,         // Egreso manual del cajero (retiro a bóveda)
        AJUSTE          // Movimiento compensatorio (ej. anulación de nota de salida)
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_movimiento")
    private Long idMovimiento;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caja", nullable = false)
    private Caja caja;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10)
    private Tipo tipo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "concepto", nullable = false, length = 20)
    private Concepto concepto;

    @NotNull
    @Builder.Default
    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal monto = BigDecimal.ZERO;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    /** Empleado que registró el movimiento (para movimientos manuales). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado")
    private Empleado empleado;

    /** ID de la operación origen (id_nota_venta, id_compra, id_nota_salida) cuando aplica. */
    @Column(name = "referencia_id")
    private Long referenciaId;

    @CreationTimestamp
    @Column(name = "fecha", nullable = false, updatable = false)
    private LocalDateTime fecha;

    @PrePersist
    protected void onCreate() {
        if (monto == null) {
            monto = BigDecimal.ZERO;
        }
    }
}
