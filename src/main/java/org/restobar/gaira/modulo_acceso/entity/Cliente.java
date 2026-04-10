package org.restobar.gaira.modulo_acceso.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "cliente", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long idCliente;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Pattern(regexp = "^\\d{1,13}$|^$", message = "NIT debe contener solo dígitos")
    @Column(name = "nit", length = 20)
    private String nit;

    @Column(name = "razon_social", length = 200)
    private String razonSocial;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @PositiveOrZero(message = "Puntos de fidelidad no puede ser negativo")
    @Builder.Default
    @Column(name = "puntos_fidelidad", nullable = false)
    private Integer puntosFidelidad = 0;

    @Builder.Default
    @Column(name = "nivel_cliente", nullable = false, length = 20)
    private String nivelCliente = "REGULAR";

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @PrePersist
    protected void onCreate() {
        if (puntosFidelidad == null) {
            puntosFidelidad = 0;
        }
        if (nivelCliente == null) {
            nivelCliente = "REGULAR";
        }
    }
}
