package org.restobar.gaira.modulo_comercial.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.restobar.gaira.modulo_acceso.entity.Empleado;


@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@ToString(exclude = {"proveedor", "empleado", "detalles"})
@Entity
@Table(name = "compra", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Compra {
    
    public enum EstadoPago{
        PENDIENTE,
        PAGADO,
        PARCIAL,
        VENCIDO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_Compra")
    private Long idCompra;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor  proveedor;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    @Builder.Default
    @OneToMany( mappedBy = "compra",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCompra> detalles = new ArrayList<>();

    @NotNull
    @Column(name = "nro_factura", nullable = false, length = 50)
    private String nroFactura;

    @NotNull
    @Column(name = "fecha_compra", nullable = false)
    private LocalDate fechaCompra;

    @Column(name = "fecha_entrega_programada")
    private LocalDate fechaEntregaProgramada;

    @Column(name = "fecha_entrega_real")
    private LocalDate fechaEntregaReal;

    @Builder.Default
    @Column(name = "sub_total", precision = 10, scale = 2)
    private BigDecimal subTotal = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "descuento", precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "impuesto", precision = 10, scale = 2)
    private BigDecimal impuesto = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_Pago")
    private EstadoPago estadoPago = EstadoPago.PENDIENTE;

    @Column(name = "fecha_limite_pago")
    private LocalDate fechaLimitePago;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por")
    private Empleado creadoPor;


    @PrePersist
    protected void onCreate() {
        if (this.subTotal == null)
            this.subTotal = BigDecimal.ZERO;
        if (this.descuento == null)
            this.descuento = BigDecimal.ZERO;
        if (this.impuesto == null)
            this.impuesto = BigDecimal.ZERO;
        if (this.total == null)
            this.total = BigDecimal.ZERO;
        if (this.estadoPago == null)
            this.estadoPago = EstadoPago.PENDIENTE;
        if (this.detalles == null)
            this.detalles = new ArrayList<>();
    }
    
}
