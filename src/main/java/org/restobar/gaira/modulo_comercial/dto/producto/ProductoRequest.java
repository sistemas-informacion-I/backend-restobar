package org.restobar.gaira.modulo_comercial.dto.producto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ProductoRequest(
    @NotBlank(message = "El código es obligatorio")
    String codigo,

    @NotBlank(message = "El nombre es obligatorio")
    String nombre,

    String descripcion,
    Long idCategoria,
    Integer tiempoPreparacion,
    String imagenUrl,
    Boolean activo
) {}
