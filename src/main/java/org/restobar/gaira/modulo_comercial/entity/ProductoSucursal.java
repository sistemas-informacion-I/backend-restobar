package org.restobar.gaira.modulo_comercial.entity;

import java.math.BigDecimal;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "producto_sucursal", schema = "public")
@IdClass(ProductoSucursalId.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoSucursal {

    @Id
    @Column(name = "id_producto_final")
    private Long idProductoFinal;

    @Id
    @Column(name = "id_sucursal")
    private Long idSucursal;

    @ManyToOne
    @JoinColumn(name = "id_producto_final", insertable = false, updatable = false)
    private ProductoFinal productoFinal;

    @ManyToOne
    @JoinColumn(name = "id_sucursal", insertable = false, updatable = false)
    private Sucursal sucursal;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "disponible", nullable = false)
    @Builder.Default
    private boolean disponible = true;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private boolean activo = true;
}
