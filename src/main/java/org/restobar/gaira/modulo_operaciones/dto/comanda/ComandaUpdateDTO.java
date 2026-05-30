package org.restobar.gaira.modulo_operaciones.dto.comanda;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ComandaUpdateDTO {

    private String estado;

    private String observaciones;

    @PositiveOrZero(message = "Número de personas debe ser cero o mayor")
    private Integer numeroPersonas;

    @Valid
    private List<DetalleComandaItemDTO> items;
}
