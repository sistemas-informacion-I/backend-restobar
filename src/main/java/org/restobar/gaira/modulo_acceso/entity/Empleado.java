package org.restobar.gaira.acceso.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.restobar.gaira.operaciones.entity.EmpleadoSucursal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "empleado", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado")
    private Long idEmpleado;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    @NotBlank(message = "Código de empleado no puede estar vacío")
    @Column(name = "codigo_empleado", nullable = false, unique = true, length = 20)
    private String codigoEmpleado;

    @NotNull(message = "Salario no puede ser nulo")
    @DecimalMin(value = "0", inclusive = false, message = "Salario debe ser mayor a 0")
    @Column(name = "salario", nullable = false, precision = 10, scale = 2)
    private BigDecimal salario;

    @Column(name = "fecha_contratacion", nullable = false)
    private LocalDate fechaContratacion;

    @Column(name = "fecha_finalizacion")
    private LocalDate fechaFinalizacion;

    // Relaciones
    @Builder.Default
    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EmpleadoSucursal> empleadoSucursales = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (fechaContratacion == null) {
            fechaContratacion = LocalDate.now();
        }
    }
}
