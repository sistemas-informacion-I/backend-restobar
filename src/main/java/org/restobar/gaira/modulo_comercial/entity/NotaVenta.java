package org.restobar.gaira.modulo_comercial.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.restobar.gaira.modulo_acceso.entity.Cliente;
import org.restobar.gaira.modulo_acceso.entity.Empleado;
import org.restobar.gaira.modulo_electronico.entity.TransaccionOnline;
import org.restobar.gaira.modulo_electronico.entity.MetodoPago;
import org.restobar.gaira.modulo_operaciones.entity.Comanda;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "nota_venta", schema = "public")
@Getter
@Setter
@ToString(exclude = { "comanda", "sucursal", "cliente", "empleado", "metodoPago", "detalleNotaVentas",
        "transaccionesOnline" })
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotaVenta {

    public enum EstadoNotaVenta {
        EMITIDA,
        PAGADA,
        ANULADA,
        DEVUELTA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_nota_venta")
    private Long idNotaVenta;

    @NotNull(message = "La comanda no puede ser nula")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comanda", nullable = false, unique = true)
    private Comanda comanda;

    @NotNull(message = "La sucursal no puede ser nula")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sucursal", nullable = false)
    private Sucursal sucursal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado")
    private Empleado empleado;

    @NotNull(message = "El método de pago no puede ser nulo")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @CreationTimestamp
    @Column(name = "fecha_emision", nullable = false, updatable = false)
    private LocalDateTime fechaEmision;

    @NotNull(message = "El subtotal no puede ser nulo")
    @DecimalMin(value = "0", inclusive = true, message = "El subtotal no puede ser negativo")
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

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

    @NotNull(message = "El total no puede ser nulo")
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Builder.Default
    @Column(name = "estado", nullable = false, length = 30)
    private String estado = EstadoNotaVenta.EMITIDA.name();

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "nit_cliente", length = 20)
    private String nitCliente;

    @Builder.Default
    @OneToMany(mappedBy = "notaVenta", fetch = FetchType.LAZY)
    private List<DetalleNotaVenta> detalleNotaVentas = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "notaVenta", fetch = FetchType.LAZY)
    private List<TransaccionOnline> transaccionesOnline = new ArrayList<>();

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
            estado = EstadoNotaVenta.EMITIDA.name();
        }
    }
}