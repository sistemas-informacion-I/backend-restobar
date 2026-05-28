package org.restobar.gaira.modulo_comercial.entity;
import java.time.LocalDate;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.restobar.gaira.modulo_acceso.entity.Cliente;
import org.restobar.gaira.modulo_acceso.entity.Empleado;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;


@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity
@Table(name = "nota_venta", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotaVenta {
    
    public enum Estado{
        PAGADO,
        PENDIENTE,
        ANULADA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_Nota_venta")
    private Long idNotaVenta;


    //relaciones
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_cliente", nullable = false)
    private Cliente cliente;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_enmpleado", nullable = false)
    private Empleado empleado;

    /* @OneToOne
    @JoinColumn(name = "id_comanda", unique = true)
    private Comanda comanda; */
    //private movimientoCaja movCaja;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sucursal" , nullable = false)
    private Sucursal sucursal;

    @Builder.Default
    @OneToMany(mappedBy = "notaVenta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleNotaVenta> detalles = new LinkedList<>();

    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;

    @Column(name = "subTotal", precision = 10, scale = 2)
    private BigDecimal subTotal;

    @Column(name = "descuento" , precision = 10, scale = 2)
    private BigDecimal descuento;

    @Column(name = "impuesto" , precision = 10, scale = 2)
    private BigDecimal impuesto;

    @Column(name = "propina" , precision = 10, scale = 2)
    private BigDecimal propina;

    @Builder.Default
    @Column(name="total", precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;


    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private Estado estado = Estado.PENDIENTE;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @Column(name = "nit")
    private String nit;

}
