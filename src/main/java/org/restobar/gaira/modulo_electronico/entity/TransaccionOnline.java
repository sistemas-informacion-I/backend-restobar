package org.restobar.gaira.modulo_electronico.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.restobar.gaira.modulo_comercial.entity.NotaVenta;

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
@Table(name = "transaccion_online", schema = "public")
@Getter
@Setter
@ToString(exclude = "notaVenta")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransaccionOnline {

    public enum EstadoTransaccion {
        PENDIENTE,
        PROCESANDO,
        APROBADA,
        RECHAZADA,
        REEMBOLSADA,
        CANCELADA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_transaccion")
    private Long idTransaccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nota_venta")
    private NotaVenta notaVenta;

    @NotBlank(message = "Numero de transaccion no puede estar vacío")
    @Column(name = "numero_transaccion", nullable = false, unique = true, length = 100)
    private String numeroTransaccion;

    @NotNull(message = "El monto no puede ser nulo")
    @DecimalMin(value = "0.01", inclusive = true, message = "El monto debe ser mayor a 0")
    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Builder.Default
    @Column(name = "moneda", nullable = false, length = 10)
    private String moneda = "BOB";

    @Builder.Default
    @Column(name = "estado", nullable = false, length = 30)
    private String estado = EstadoTransaccion.PENDIENTE.name();

    @Builder.Default
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio = LocalDateTime.now();

    @Column(name = "fecha_completado")
    private LocalDateTime fechaCompletado;

    @Column(name = "codigo_autorizacion", length = 100)
    private String codigoAutorizacion;

    @Column(name = "codigo_error", length = 50)
    private String codigoError;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "datos_adicionales", columnDefinition = "jsonb")
    private Map<String, Object> datosAdicionales;

    @PrePersist
    protected void onCreate() {
        if (fechaInicio == null) {
            fechaInicio = LocalDateTime.now();
        }
        if (moneda == null) {
            moneda = "BOB";
        }
        if (estado == null) {
            estado = EstadoTransaccion.PENDIENTE.name();
        }
    }
}