package org.restobar.gaira.modulo_comercial.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.restobar.gaira.modulo_inventario.entity.StockSucursal;

import java.math.BigDecimal;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@ToString
@Entity
@Table(name = "detalle_compra", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_detalle_compra")
    private Long idDetalleCompra;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compra", nullable = false)
    @ToString.Exclude
    private Compra compra;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_stock", nullable = false)
    @ToString.Exclude
    private StockSucursal stock;

    @NotNull
    @Positive
    @Builder.Default
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad = 1;

    @NotNull
    @Positive
    @Builder.Default
    @Column(name = "precio_unitario", nullable = false)
    private BigDecimal precioUnitario = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "sub_total")
    private BigDecimal subTotal = BigDecimal.ZERO;

    @PrePersist
    protected void onCreate() {
        calcularSubTotal();
    }

    public void calcularSubTotal() {
        this.subTotal = this.precioUnitario.multiply(BigDecimal.valueOf(this.cantidad));
    }
}
