package org.restobar.gaira.modulo_operaciones.dto.comanda;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DetalleComandaItemDTO {

    @NotNull(message = "ID de producto es requerido")
    private Long idProductoFinal;

    @NotNull(message = "Cantidad es requerida")
    @Positive(message = "Cantidad debe ser mayor a cero")
    private Integer cantidad;

    private String notas;
}
