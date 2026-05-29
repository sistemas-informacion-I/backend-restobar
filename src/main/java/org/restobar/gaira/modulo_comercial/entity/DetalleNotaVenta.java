package org.restobar.gaira.modulo_comercial.entity;

import java.math.BigDecimal;

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
@Table(name = "detalle_nota_venta", schema = "public")
@Getter
@Setter
@ToString(exclude = { "notaVenta", "productoFinal" })
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleNotaVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_detalle_nota_venta")
    private Long idDetalleNotaVenta;

    @NotNull(message = "La nota de venta no puede ser nula")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nota_venta", nullable = false)
    private NotaVenta notaVenta;

    @NotNull(message = "El producto final no puede ser nulo")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto_final", nullable = false)
    private ProductoFinal productoFinal;

    @NotNull(message = "La cantidad no puede ser nula")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @NotNull(message = "El precio unitario no puede ser nulo")
    @DecimalMin(value = "0", inclusive = true, message = "El precio unitario no puede ser negativo")
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @NotNull(message = "El costo unitario no puede ser nulo")
    @DecimalMin(value = "0", inclusive = true, message = "El costo unitario no puede ser negativo")
    @Column(name = "costo_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoUnitario;

    @Builder.Default
    @DecimalMin(value = "0", inclusive = true, message = "El descuento no puede ser negativo")
    @Column(name = "descuento", nullable = false, precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @NotNull(message = "El subtotal no puede ser nulo")
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @PrePersist
    protected void onCreate() {
        if (descuento == null) {
            descuento = BigDecimal.ZERO;
        }
    }
}