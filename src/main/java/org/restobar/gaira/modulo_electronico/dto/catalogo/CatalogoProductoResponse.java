package org.restobar.gaira.modulo_electronico.dto.catalogo;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record CatalogoProductoResponse(
    Long idProductoFinal,
    String codigo,
    String nombre,
    String descripcion,
    String imagenUrl,
    Integer tiempoPreparacion,
    Long idCategoria,
    String nombreCategoria,
    BigDecimal precio,
    boolean disponible,
    boolean hayStock
) {}
