package org.restobar.gaira.modulo_operaciones.entity;

import java.time.LocalDateTime;

import org.restobar.gaira.modulo_acceso.entity.Cliente;
import org.restobar.gaira.modulo_acceso.entity.Empleado;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "comanda", schema = "public")
@Getter
@Setter
@ToString(exclude = { "cliente", "empleado", "notaVenta" })
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comanda {

    public enum TipoServicio {
        MESA,
        PARA_LLEVAR,
        ONLINE
    }

    public enum EstadoComanda {
        PENDIENTE_PAGO,
        ABIERTA,
        EN_PREPARACION,
        LISTA,
        ENTREGADA,
        CERRADA,
        CANCELADA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_comanda")
    private Long idComanda;

    @NotBlank(message = "Numero de comanda no puede estar vacío")
    @Column(name = "numero_comanda", nullable = false, unique = true, length = 50)
    private String numeroComanda;

    @NotNull(message = "Sucursal no puede ser nula")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sucursal", nullable = false)
    private Sucursal sucursal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado")
    private Empleado empleado;

    @Column(name = "id_reserva")
    private Long idReserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mesa")
    private Mesa mesa;

    @Column(name = "id_carrito")
    private Long idCarrito;

    @NotNull(message = "Tipo de servicio no puede ser nulo")
    @Column(name = "tipo_servicio", nullable = false, length = 20)
    private String tipoServicio;

    @Builder.Default
    @Column(name = "fecha_apertura", nullable = false)
    private LocalDateTime fechaApertura = LocalDateTime.now();

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "numero_personas")
    private Integer numeroPersonas;

    @Builder.Default
    @Column(name = "estado", nullable = false, length = 30)
    private String estado = EstadoComanda.ABIERTA.name();

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @OneToOne(mappedBy = "comanda", fetch = FetchType.LAZY)
    private org.restobar.gaira.modulo_comercial.entity.NotaVenta notaVenta;

    @PrePersist
    protected void onCreate() {
        if (fechaApertura == null) {
            fechaApertura = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoComanda.ABIERTA.name();
        }
    }
}