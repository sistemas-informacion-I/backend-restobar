package org.restobar.gaira.modulo_electronico.dto.catalogo;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record CatalogoUpdateRequest(

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    BigDecimal precio,

    @NotNull(message = "La disponibilidad es obligatoria")
    Boolean disponible
) {}
