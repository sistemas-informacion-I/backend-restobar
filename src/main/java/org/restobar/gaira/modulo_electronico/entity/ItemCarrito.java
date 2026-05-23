package org.restobar.gaira.modulo_carrito.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "item_carrito", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_carrito")
    private Long idItemCarrito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrito", nullable = false)
    private CarritoCompras carrito;

    @NotNull(message = "id_producto_final no puede ser nulo")
    @Column(name = "id_producto_final", nullable = false)
    private Long idProductoFinal;

    @Positive(message = "La cantidad debe ser mayor a cero")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    /** Precio capturado en el momento de agregar; se recalcula en checkout. */
    @PositiveOrZero(message = "El precio unitario no puede ser negativo")
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "notas_especiales", columnDefinition = "TEXT")
    private String notasEspeciales;

    @Builder.Default
    @Column(name = "fecha_agregado", nullable = false, updatable = false)
    private LocalDateTime fechaAgregado = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (fechaAgregado == null) fechaAgregado = LocalDateTime.now();
    }
}
