package org.restobar.gaira.operaciones.entity;

import jakarta.persistence.*;
//import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "sector", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sector")
    private Long idSector;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sucursal", nullable = false)
    private Sucursal sucursal;

    @NotBlank(message = "Nombre de sector no puede estar vacío")
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @NotBlank(message = "Tipo de sector no puede estar vacío")
    @Column(name = "tipo_sector", nullable = false, length = 20)
    private String tipoSector;

    @Builder.Default
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    // Relaciones
    @Builder.Default
    @OneToMany(mappedBy = "sector", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Mesa> mesas = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (activo == null) {
            activo = true;
        }
    }
}
