package org.restobar.gaira.modulo_carrito.dto.item;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ActualizarItemRequest(

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a cero")
        Integer cantidad
) {}
