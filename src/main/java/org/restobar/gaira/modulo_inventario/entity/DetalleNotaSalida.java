package org.restobar.gaira.modulo_inventario.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_nota_salida", schema = "public")
@Getter
@Setter
@ToString(exclude = {"notaSalida", "stockSucursal"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleNotaSalida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_detalle")
    private Long idDetalle;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nota_salida", nullable = false)
    private NotaSalida notaSalida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_stock_sucursal")
    private StockSucursal stockSucursal;

    @NotNull
    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @NotNull
    @Column(name = "cantidad", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;

    @NotNull
    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;
}
