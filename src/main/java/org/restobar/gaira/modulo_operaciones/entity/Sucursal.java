package org.restobar.gaira.modulo_operaciones.entity;

import java.time.LocalTime; //JPA
import java.util.ArrayList; //validaciones
import java.util.LinkedList; //evita escribir setters y getters
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity // le dice a spring que es una tabla de BD -> 
@Table(name = "sucursal", schema = "public")

@Setter //genera automaticamente setters and getters
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@NoArgsConstructor // constructor no necesario
@AllArgsConstructor //

@Builder // permite crear objetos asi Sucursal s = Sucursal.builder().nombre("X").build();
public class Sucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // hace que se comparen objetos solo por su id
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
    @Column(name = "correo", length = 150) // se aumento a unique
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
    private List<Sector> sectores = new LinkedList<>();

    @Builder.Default
    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EmpleadoSucursal> empleadoSucursales = new ArrayList<>();

    @PrePersist // se ejecuta antes de insertar a la BD
    protected void onCreate() {
        if (estadoOperativo == null) {
            estadoOperativo = "ACTIVO";
        }
        if (activo == null) {
            activo = true;
        }
    }
}