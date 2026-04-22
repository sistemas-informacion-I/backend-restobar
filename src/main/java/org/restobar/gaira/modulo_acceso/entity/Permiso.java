package org.restobar.gaira.modulo_acceso.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "permiso", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permiso")
    private Long idPermiso;

    @NotBlank(message = "Nombre del permiso no puede estar vacío")
    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    @NotBlank(message = "Módulo no puede estar vacío")
    @Column(name = "modulo", nullable = false, length = 50)
    private String modulo;

    @NotBlank(message = "Acción no puede estar vacía")
    @Column(name = "accion", nullable = false, length = 50)
    private String accion;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Builder.Default
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Relaciones
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "permiso", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RolPermiso> rolesPermiso = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
    }
}
