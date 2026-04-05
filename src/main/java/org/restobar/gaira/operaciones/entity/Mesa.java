package org.restobar.gaira.operaciones.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "mesa", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mesa")
    private Long idMesa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sector", nullable = false)
    private Sector sector;

    @NotBlank(message = "Número de mesa no puede estar vacío")
    @Column(name = "numero_mesa", nullable = false, length = 20)
    private String numeroMesa;

    @NotNull(message = "Capacidad de personas no puede ser nula")
    @Positive(message = "Capacidad debe ser mayor a 0")
    @Column(name = "capacidad_personas", nullable = false)
    private Integer capacidadPersonas;

    @Builder.Default
    @Column(name = "disponibilidad", nullable = false, length = 20)
    private String disponibilidad = "DISPONIBLE";

    @Builder.Default
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @PrePersist
    protected void onCreate() {
        if (disponibilidad == null) {
            disponibilidad = "DISPONIBLE";
        }
        if (activo == null) {
            activo = true;
        }
    }
}
