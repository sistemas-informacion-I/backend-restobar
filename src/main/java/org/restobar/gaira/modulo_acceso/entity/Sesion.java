package org.restobar.gaira.modulo_acceso.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sesion", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sesion")
    private Long idSesion;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @NotBlank(message = "Token de sesión no puede estar vacío")
    @Column(name = "token_sesion", nullable = false, unique = true, length = 2000)
    private String tokenSesion;

    @NotBlank(message = "Refresh token no puede estar vacío")
    @Column(name = "refresh_token", nullable = false, unique = true, length = 2000)
    private String refreshToken;

    @Column(name = "refresh_expiracion")
    private LocalDateTime refreshExpiracion;

    @Column(name = "fecha_inicio", nullable = false, updatable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;

    @Column(name = "ip_origen")
    private String ipOrigen;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    /**
     * Fecha en que la sesión fue cerrada/revocada.
     * Si es NULL, la sesión está activa.
     */
    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @PrePersist
    protected void onCreate() {
        if (fechaInicio == null) {
            fechaInicio = LocalDateTime.now();
        }
    }

    /**
     * Conveniencia: indica si la sesión está activa (sin fecha de cierre).
     */
    public boolean isActiva() {
        return fechaCierre == null;
    }
}
