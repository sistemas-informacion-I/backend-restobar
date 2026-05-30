package org.restobar.gaira.modulo_electronico.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "metodo_pago", schema = "public")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_metodo_pago")
    private Long idMetodoPago;

    @NotBlank(message = "Nombre no puede estar vacío")
    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @DecimalMin(value = "0", inclusive = true, message = "La comisión porcentual debe ser mayor o igual a 0")
    @DecimalMax(value = "100", inclusive = true, message = "La comisión porcentual no puede superar 100")
    @Column(name = "comision_porcentaje", precision = 5, scale = 2)
    private BigDecimal comisionPorcentaje;

    @Builder.Default
    @DecimalMin(value = "0", inclusive = true, message = "La comisión fija no puede ser negativa")
    @Column(name = "comision_fija", nullable = false, precision = 10, scale = 2)
    private BigDecimal comisionFija = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @PrePersist
    protected void onCreate() {
        if (comisionFija == null) {
            comisionFija = BigDecimal.ZERO;
        }
        if (activo == null) {
            activo = true;
        }
    }
}