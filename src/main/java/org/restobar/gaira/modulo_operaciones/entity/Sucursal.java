package org.restobar.gaira.modulo_operaciones.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sucursal", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sucursal")
    private Long idSucursal;

    @NotBlank(message = "Nombre de sucursal no puede estar vacío")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "Dirección no puede estar vacía")
    @Column(name = "direccion", nullable = false, columnDefinition = "TEXT")
    private String direccion;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Email(message = "Correo de sucursal debe ser válido")
    @Column(name = "correo", length = 150)
    private String correo;

    @Column(name = "horario_apertura")
    private LocalTime horarioApertura;

    @Column(name = "horario_cierre")
    private LocalTime horarioCierre;

    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @Column(name = "departamento", length = 100)
    private String departamento;

    @Builder.Default
    @Column(name = "estado_operativo", nullable = false, length = 20)
    private String estadoOperativo = "ACTIVO";

    @Builder.Default
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    // Relaciones
    @Builder.Default
    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Sector> sectores = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EmpleadoSucursal> empleadoSucursales = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (estadoOperativo == null) {
            estadoOperativo = "ACTIVO";
        }
        if (activo == null) {
            activo = true;
        }
    }
}
