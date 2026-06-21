package org.restobar.gaira.modulo_electronico.entity;

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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.restobar.gaira.modulo_acceso.entity.Empleado;
import org.restobar.gaira.modulo_operaciones.entity.Comanda;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "entrega", schema = "public")
@Getter
@Setter
@ToString(exclude = {"comanda", "empleado"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Entrega {

    public enum EstadoEntrega {
        PENDIENTE,
        ASIGNADO,
        EN_CAMINO,
        ENTREGADO,
        CANCELADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_entrega")
    private Long idEntrega;

    @NotNull(message = "La comanda es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comanda", nullable = false, unique = true)
    private Comanda comanda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado", insertable = false, updatable = false)
    private Empleado empleado;

    @Column(name = "id_usuario_repartidor")
    private Long idUsuarioRepartidor;

    @NotBlank(message = "La dirección de entrega es obligatoria")
    @Column(name = "direccion_entrega", nullable = false, columnDefinition = "TEXT")
    private String direccionEntrega;

    @NotNull(message = "Latitud de destino obligatoria")
    @Column(name = "latitud", nullable = false, precision = 10, scale = 8)
    private BigDecimal latitud;

    @NotNull(message = "Longitud de destino obligatoria")
    @Column(name = "longitud", nullable = false, precision = 11, scale = 8)
    private BigDecimal longitud;

    @Column(name = "latitud_actual", precision = 10, scale = 8)
    private BigDecimal latitudActual;

    @Column(name = "longitud_actual", precision = 11, scale = 8)
    private BigDecimal longitudActual;

    @Column(name = "distancia_km", precision = 10, scale = 2)
    private BigDecimal distanciaKm;

    @Column(name = "tiempo_estimado_min")
    private Integer tiempoEstimadoMin;

    @Builder.Default
    @NotNull(message = "El costo de envío no puede ser nulo")
    @DecimalMin(value = "0", inclusive = true, message = "El costo de envío no puede ser negativo")
    @Column(name = "costo_envio", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoEnvio = BigDecimal.ZERO;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoEntrega estado = EstadoEntrega.PENDIENTE;

    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @PrePersist
    protected void onCreate() {
        if (estado == null) {
            estado = EstadoEntrega.PENDIENTE;
        }
        if (costoEnvio == null) {
            costoEnvio = BigDecimal.ZERO;
        }
    }
}
