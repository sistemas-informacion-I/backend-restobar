package org.restobar.gaira.modulo_inventario.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
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
@Table(name = "lote_inventario", schema = "public")
@Getter
@Setter
@ToString(exclude = "stockSucursal")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoteInventario {

    public enum EstadoLote {
        DISPONIBLE,
        VENCIDO,
        AGOTADO,
        DAÑADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_lote")
    private Long idLote;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_stock", nullable = false)
    private StockSucursal stockSucursal;

    // No tocar el orden de los campos, ya que se utiliza para ordenar los lotes
    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "numero_lote", length = 50)
    private String numeroLote;

    @NotNull
    @Column(name = "cantidad", nullable = false, precision = 10, scale = 3)
    private BigDecimal cantidad;

    @Builder.Default
    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso = LocalDate.now();

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @NotNull
    @Column(name = "precio_compra", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCompra;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoLote estado = EstadoLote.DISPONIBLE;

    @PrePersist
    protected void onCreate() {
        if (this.fechaIngreso == null)
            this.fechaIngreso = LocalDate.now();
        if (this.estado == null)
            this.estado = EstadoLote.DISPONIBLE;
    }
}
