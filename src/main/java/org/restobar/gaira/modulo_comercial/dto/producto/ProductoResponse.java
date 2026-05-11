package org.restobar.gaira.modulo_comercial.dto.producto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ProductoResponse(
    Long idProductoFinal,
    String codigo,
    String nombre,
    String descripcion,
    Long idCategoria,
    String nombreCategoria,
    Integer tiempoPreparacion,
    String imagenUrl,
    Boolean activo,
    LocalDateTime fechaCreacion
) {}
