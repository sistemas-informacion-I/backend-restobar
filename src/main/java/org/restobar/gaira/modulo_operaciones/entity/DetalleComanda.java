package org.restobar.gaira.modulo_operaciones.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_comercial.entity.ProductoFinal;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detalle_comanda", schema = "public")
@Getter
@Setter
@ToString(exclude = { "comanda" })
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleComanda {

    public enum EstadoDetalle {
        PENDIENTE,
        EN_PREPARACION,
        LISTO,
        ENTREGADO,
        CANCELADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_detalle_comanda")
    private Long idDetalleComanda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comanda", nullable = false)
    private Comanda comanda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto_final", nullable = false)
    private ProductoFinal productoFinal;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @Column(name = "estacion_preparacion", length = 50)
    private String estacionPreparacion;

    @Column(name = "fecha_aceptacion")
    private LocalDateTime fechaAceptacion;

    @Column(name = "empleado_asignado", length = 100)
    private String empleadoAsignado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onPersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoDetalle.PENDIENTE.name();
        }
    }
}
