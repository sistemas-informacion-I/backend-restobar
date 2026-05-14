package org.restobar.gaira.modulo_inventario.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.restobar.gaira.modulo_comercial.entity.ProductoFinal;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Entity
@Table(name = "receta", schema = "public")
@Getter
@Setter
@ToString(exclude = "ingredientes")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_receta")
    private Long idReceta;

    @NotNull(message = "El producto final es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto_final", nullable = false)
    private ProductoFinal productoFinal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sucursal_referencia")
    private Sucursal sucursalReferencia;

    @NotBlank(message = "Nombre de receta no puede estar vacio")
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "tiempo_preparacion")
    private Integer tiempoPreparacion;

    @Column(name = "instrucciones", columnDefinition = "TEXT")
    private String instrucciones;

    @Column(name = "version_etiqueta", length = 80)
    private String versionEtiqueta;

    @Column(name = "fecha_vigencia_inicio")
    private LocalDate fechaVigenciaInicio;

    @Column(name = "fecha_vigencia_fin")
    private LocalDate fechaVigenciaFin;

    @PositiveOrZero(message = "El costo total no puede ser negativo")
    @Column(name = "costo_total", nullable = false)
    private BigDecimal costoTotal;

    @Builder.Default
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Builder.Default
    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IngredienteReceta> ingredientes = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    public void replaceIngredientes(List<IngredienteReceta> nuevosIngredientes) {
        ingredientes.clear();
        if (nuevosIngredientes != null) {
            nuevosIngredientes.forEach(this::addIngrediente);
        }
    }

    public void addIngrediente(IngredienteReceta ingrediente) {
        if (ingrediente != null) {
            ingrediente.setReceta(this);
            ingredientes.add(ingrediente);
        }
    }

    @PrePersist
    protected void onCreate() {
        if (activo == null) activo = true;
        if (costoTotal == null) costoTotal = BigDecimal.ZERO;
    }
}
