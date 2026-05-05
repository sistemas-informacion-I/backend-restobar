package org.restobar.gaira.modulo_inventario.dto.inventario;

import org.restobar.gaira.modulo_inventario.entity.Inventario.UnidadMedida;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventarioRequest {
    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "La unidad de medida es obligatoria")
    private UnidadMedida unidadMedida;

    private String marca;

    private Boolean esRehutilizable;
    private Boolean activo;
}
