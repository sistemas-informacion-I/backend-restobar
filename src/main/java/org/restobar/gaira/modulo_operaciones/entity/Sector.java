package org.restobar.gaira.modulo_operaciones.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "sector", schema = "public")

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
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
