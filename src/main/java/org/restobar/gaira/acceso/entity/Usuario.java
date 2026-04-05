package org.restobar.gaira.acceso.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuario", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @NotBlank(message = "CI no puede estar vacío")
    @Column(name = "ci", nullable = false, unique = true, length = 20)
    private String ci;

    @NotBlank(message = "Nombre no puede estar vacío")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "Apellido no puede estar vacío")
    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @NotBlank(message = "Username no puede estar vacío")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "Password no puede estar vacío")
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @NotNull(message = "Sexo no puede ser nulo")
    @Pattern(regexp = "[MFO]", message = "Sexo debe ser M, F u O")
    @Column(name = "sexo", nullable = false, length = 1)
    private String sexo;

    @Email(message = "Correo debe ser válido")
    @Column(name = "correo", unique = true, length = 150)
    private String correo;

    @Column(name = "direccion", columnDefinition = "TEXT")
    private String direccion;

    @PositiveOrZero(message = "Intentos fallidos no puede ser negativo")
    @Builder.Default
    @Column(name = "intentos_fallidos", nullable = false)
    private Integer intentosFallidos = 0;

    @Builder.Default
    @Column(name = "estado_acceso", nullable = false, length = 20)
    private String estadoAcceso = "HABILITADO";

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Builder.Default
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    // Relaciones
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Cliente cliente;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Empleado empleado;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Proveedor proveedor;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<RolUsuario> rolesUsuario = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Sesion> sesiones = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
        if (intentosFallidos == null) {
            intentosFallidos = 0;
        }
        if (estadoAcceso == null) {
            estadoAcceso = "HABILITADO";
        }
    }
}
