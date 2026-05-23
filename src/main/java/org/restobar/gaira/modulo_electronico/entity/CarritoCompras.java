package org.restobar.gaira.modulo_carrito.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carrito_compras", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoCompras {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrito")
    private Long idCarrito;

    /** Nulo si el cliente es anónimo; se asigna al autenticarse. */
    @Column(name = "id_cliente")
    private Long idCliente;

    /** Clave de sesión temporal para clientes anónimos. */
    @Column(name = "session_id", columnDefinition = "TEXT")
    private String sessionId;

    @Column(name = "id_sucursal")
    private Long idSucursal;

    @Builder.Default
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Builder.Default
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    /**
     * ACTIVO     → en uso (Redis como fuente de verdad).
     * ABANDONADO → TTL expirado sin convertir.
     * CONVERTIDO → comanda generada; registro histórico.
     */
    @Builder.Default
    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "ACTIVO";

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ItemCarrito> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null)     fechaCreacion     = LocalDateTime.now();
        if (fechaActualizacion == null) fechaActualizacion = LocalDateTime.now();
        if (estado == null)            estado            = "ACTIVO";
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
