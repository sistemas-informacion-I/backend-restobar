package org.restobar.gaira.modulo_operaciones.dto.comanda;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetalleComandaUpdateDTO {

    @Positive(message = "Cantidad debe ser mayor a cero")
    private Integer cantidad;

    private String notas;
}
