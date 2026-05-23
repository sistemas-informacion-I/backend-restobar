package org.restobar.gaira.modulo_carrito.dto.carrito;

import jakarta.validation.constraints.NotNull;

public record CambiarSucursalRequest(

        @NotNull(message = "id_sucursal es obligatorio")
        Long idSucursal
) {}
