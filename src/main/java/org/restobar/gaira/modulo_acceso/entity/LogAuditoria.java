package org.restobar.gaira.modulo_acceso.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "log_auditoria", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log")
    private Long idLog;

    @NotBlank(message = "Tabla no puede estar vacía")
    @Column(name = "tabla", nullable = false, length = 100)
    private String tabla;

    @NotBlank(message = "Operación no puede estar vacía")
    @Column(name = "operacion", nullable = false, length = 20)
    private String operacion;

    @Column(name = "id_registro", length = 50)
    private String idRegistro;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "datos_anteriores", columnDefinition = "jsonb")
    private Map<String, Object> datosAnteriores;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "datos_nuevos", columnDefinition = "jsonb")
    private Map<String, Object> datosNuevos;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "id_sucursal")
    private Long idSucursal;

    @Column(name = "ip_origen")
    private String ipOrigen;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "fecha_operacion", nullable = false, updatable = false)
    private LocalDateTime fechaOperacion;

    @PrePersist
    protected void onCreate() {
        if (fechaOperacion == null) {
            fechaOperacion = LocalDateTime.now();
        }
    }
}
