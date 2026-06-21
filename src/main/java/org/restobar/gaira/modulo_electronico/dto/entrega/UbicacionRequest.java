package org.restobar.gaira.modulo_electronico.dto.entrega;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UbicacionRequest(
        @NotNull BigDecimal latitud,
        @NotNull BigDecimal longitud
) {}
