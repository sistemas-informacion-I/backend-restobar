package org.restobar.gaira.modulo_inventario.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.restobar.gaira.modulo_operaciones.entity.Sucursal;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "stock_sucursal", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = {
        "id_inventario", "id_sucursal" }))
@Getter
@Setter
@ToString(exclude = { "inventario", "sucursal", "lotes" })
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockSucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_stock")
    private Long idStock;

    // No tocar, se utiliza para validar la unicidad del stock por inventario y
    // sucursal
    @Version
    @Column(name = "version")
    private Long version;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inventario", nullable = false)
    private Inventario inventario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sucursal", nullable = false)
    private Sucursal sucursal;

    @Builder.Default
    @Column(name = "cantidad", nullable = false, precision = 10, scale = 3)
    private BigDecimal cantidad = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "cantidad_minima", nullable = false, precision = 10, scale = 3)
    private BigDecimal cantidadMinima = BigDecimal.ZERO;

    @Column(name = "cantidad_maxima", precision = 10, scale = 3)
    private BigDecimal cantidadMaxima;

    @Builder.Default
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "precio_promedio", nullable = false, precision = 10, scale = 4)
    private BigDecimal precioPromedio = BigDecimal.ZERO;

    @Column(name = "ubicacion_almacen", length = 50)
    private String ubicacionAlmacen;

    @Builder.Default
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Builder.Default
    @OneToMany(mappedBy = "stockSucursal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoteInventario> lotes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.cantidad == null)
            this.cantidad = BigDecimal.ZERO;
        if (this.cantidadMinima == null)
            this.cantidadMinima = BigDecimal.ZERO;
        if (this.precioUnitario == null)
            this.precioUnitario = BigDecimal.ZERO;
        if (this.precioPromedio == null)
            this.precioPromedio = BigDecimal.ZERO;
        if (this.activo == null)
            this.activo = true;
    }
}
