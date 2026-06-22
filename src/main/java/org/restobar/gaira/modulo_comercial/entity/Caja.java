package org.restobar.gaira.modulo_comercial.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.restobar.gaira.modulo_acceso.entity.Empleado;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
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
 * CU22 – Gestionar Caja.
 * Representa una sesión de caja por sucursal. Solo puede existir una caja
 * {@code ABIERTA} por sucursal a la vez. Centraliza el ciclo de vida
 * (apertura, arqueo y cierre) y agrupa todos los {@link MovimientoCaja}.
 */
@Entity
@Table(name = "caja", schema = "public")
@Getter
@Setter
@ToString(exclude = { "sucursal", "empleadoApertura", "empleadoCierre", "movimientos" })
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Caja {

    public enum Estado {
        ABIERTA,
        CERRADA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_caja")
    private Long idCaja;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sucursal", nullable = false)
    private Sucursal sucursal;

    /** Empleado que aperturó la caja (puede ser nulo si la abre un SUPERUSER sin ficha de empleado). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado_apertura")
    private Empleado empleadoApertura;

    /** Empleado que cerró la caja. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado_cierre")
    private Empleado empleadoCierre;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private Estado estado = Estado.ABIERTA;

    @NotNull
    @Builder.Default
    @Column(name = "monto_inicial", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoInicial = BigDecimal.ZERO;

    /** Saldo real (dinero físico) declarado por el cajero al cierre. */
    @Column(name = "monto_final", precision = 12, scale = 2)
    private BigDecimal montoFinal;

    /** Saldo esperado calculado al cierre: montoInicial + ingresos - egresos. */
    @Column(name = "saldo_esperado", precision = 12, scale = 2)
    private BigDecimal saldoEsperado;

    /** Diferencia al cierre: montoFinal - saldoEsperado (positivo = sobrante, negativo = faltante). */
    @Column(name = "diferencia", precision = 12, scale = 2)
    private BigDecimal diferencia;

    @CreationTimestamp
    @Column(name = "fecha_apertura", nullable = false, updatable = false)
    private LocalDateTime fechaApertura;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "observacion_apertura", length = 500)
    private String observacionApertura;

    @Column(name = "observacion_cierre", length = 500)
    private String observacionCierre;

    @Builder.Default
    @OneToMany(mappedBy = "caja", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MovimientoCaja> movimientos = new LinkedList<>();

    @PrePersist
    protected void onCreate() {
        if (estado == null) {
            estado = Estado.ABIERTA;
        }
        if (montoInicial == null) {
            montoInicial = BigDecimal.ZERO;
        }
        if (movimientos == null) {
            movimientos = new LinkedList<>();
        }
    }
}
