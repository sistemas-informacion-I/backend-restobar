package org.restobar.gaira.modulo_electronico.dto.carrito;

import jakarta.validation.constraints.NotNull;

public record CambiarSucursalRequest(

        @NotNull(message = "id_sucursal es obligatorio")
        Long idSucursal
) {}
