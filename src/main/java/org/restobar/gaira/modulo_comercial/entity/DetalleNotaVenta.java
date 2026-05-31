package org.restobar.gaira.modulo_comercial.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@ToString
@Entity
@Table(name = "detalle_nota_venta", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleNotaVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_detalle_nota_venta")
    private Long idDetalleNotaVenta;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nota_venta", nullable = false)
    @ToString.Exclude
    private NotaVenta notaVenta;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto_final", nullable = false)
    @ToString.Exclude
    private ProductoFinal productoFinal;

    @NotNull
    @Positive
    @Builder.Default
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad = 1;

    @NotNull
    @Builder.Default
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioU = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "costo_unitario", precision = 10, scale = 2)
    private BigDecimal costoU = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "descuento", precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subTotal = BigDecimal.ZERO;

    @Column(name = "descripcion")
    private String descripcion;

    @PrePersist
    protected void onCreate() {
        calcularSubTotal();
    }

    public void calcularSubTotal() {
        this.subTotal = this.precioU.multiply(BigDecimal.valueOf(this.cantidad))
                .subtract(this.descuento != null ? this.descuento : BigDecimal.ZERO);
    }
}