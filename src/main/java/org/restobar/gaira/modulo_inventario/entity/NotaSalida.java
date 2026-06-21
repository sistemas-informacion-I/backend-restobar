package org.restobar.gaira.modulo_inventario.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.restobar.gaira.modulo_acceso.entity.Empleado;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "nota_salida", schema = "public")
@Getter
@Setter
@ToString(exclude = {"sucursal", "empleado", "detalles"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotaSalida {

    public enum TipoGasto {
        SERVICIOS, ALQUILER, SUELDOS, MANTENIMIENTO, TRANSPORTE, IMPUESTOS, PERDIDA, OTROS
    }

    public enum EstadoNota {
        REGISTRADO, ANULADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_nota_salida")
    private Long idNotaSalida;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sucursal", nullable = false)
    private Sucursal sucursal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado")
    private Empleado empleado;

    @Builder.Default
    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @NotNull
    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_gasto", nullable = false, length = 50)
    private TipoGasto tipoGasto;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoNota estado = EstadoNota.REGISTRADO;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Builder.Default
    @OneToMany(mappedBy = "notaSalida", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleNotaSalida> detalles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (fecha == null) fecha = LocalDateTime.now();
        if (estado == null) estado = EstadoNota.REGISTRADO;
    }
}
