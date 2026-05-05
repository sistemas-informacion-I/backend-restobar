package org.restobar.gaira.modulo_inventario.dto.inventario;

import java.time.LocalDateTime;

import org.restobar.gaira.modulo_inventario.entity.Inventario.UnidadMedida;

import lombok.Data;

@Data
public class InventarioResponse {
    private Long idInventario;
    private String codigo;
    private String nombre;
    private String descripcion;
    private UnidadMedida unidadMedida;
    private String marca;
    private Boolean esRehutilizable;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}
