package org.restobar.gaira.modulo_electronico.dto.item;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AgregarItemRequest(

        @NotNull(message = "id_producto_final es obligatorio")
        Long idProductoFinal,

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a cero")
        Integer cantidad,

        String notasEspeciales
) {}
