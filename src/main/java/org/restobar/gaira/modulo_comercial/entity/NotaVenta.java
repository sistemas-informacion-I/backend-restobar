package org.restobar.gaira.modulo_comercial.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.restobar.gaira.modulo_acceso.entity.Cliente;
import org.restobar.gaira.modulo_acceso.entity.Empleado;
import org.restobar.gaira.modulo_electronico.entity.TransaccionOnline;
import org.restobar.gaira.modulo_electronico.entity.MetodoPago;
import org.restobar.gaira.modulo_operaciones.entity.Comanda;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "nota_venta", schema = "public")
@Getter
@Setter
@ToString(exclude = { "comanda", "sucursal", "cliente", "empleado", "metodoPago", "detalles",
        "transaccionesOnline" })
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotaVenta {

    public enum Estado{
        PAGADA,
        PENDIENTE,
        ANULADA,
        DEVUELTA,
        EMITIDA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_nota_venta")
    private Long idNotaVenta;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comanda", nullable = false, unique = true)
    private Comanda comanda;
    //private movimientoCaja movCaja;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sucursal", nullable = false)
    private Sucursal sucursal;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado")
    private Empleado empleado;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_metodo_pago", nullable = false)
    private MetodoPago metodoPago;
    @CreationTimestamp
    @Column(name = "fecha_emision", nullable = false, updatable = false)
    private LocalDateTime fechaEmision;

    @NotNull
    @DecimalMin(value = "0", inclusive = true, message = "El subtotal no puede ser negativo")
    @Column(name = "subTotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subTotal;

    @Builder.Default
    @DecimalMin(value = "0", inclusive = true, message = "El descuento no puede ser negativo")
    @Column(name = "descuento", nullable = false, precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;
    @Builder.Default
    @DecimalMin(value = "0", inclusive = true, message = "El impuesto no puede ser negativo")
    @Column(name = "impuesto", nullable = false, precision = 10, scale = 2)
    private BigDecimal impuesto = BigDecimal.ZERO;
    @Builder.Default
    @DecimalMin(value = "0", inclusive = true, message = "La propina no puede ser negativa")
    @Column(name = "propina", nullable = false, precision = 10, scale = 2)
    private BigDecimal propina = BigDecimal.ZERO;

    @NotNull
    @Builder.Default
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private Estado estado = Estado.PENDIENTE;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "nit", length = 20)
    private String nit;

    @Builder.Default
    @OneToMany(mappedBy = "notaVenta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleNotaVenta> detalles = new LinkedList<>();

    @Builder.Default
    @OneToMany(mappedBy = "notaVenta", fetch = FetchType.LAZY)
    private List<TransaccionOnline> transaccionesOnline = new LinkedList<>();

    @PrePersist
    protected void onCreate() {
        if (descuento == null) {
            descuento = BigDecimal.ZERO;
        }
        if (impuesto == null) {
            impuesto = BigDecimal.ZERO;
        }
        if (propina == null) {
            propina = BigDecimal.ZERO;
        }
        if (estado == null) {
            estado = Estado.PENDIENTE;
        }
    }
}