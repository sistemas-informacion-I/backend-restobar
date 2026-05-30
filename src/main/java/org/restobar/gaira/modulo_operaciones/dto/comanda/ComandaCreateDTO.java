package org.restobar.gaira.modulo_operaciones.dto.comanda;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ComandaCreateDTO {

    private Long idSucursal;

    @NotBlank(message = "Tipo de servicio es requerido")
    private String tipoServicio;

    private Long idMesa;

    private Long idReserva;

    private Long idCliente;

    @PositiveOrZero(message = "Número de personas debe ser cero o mayor")
    private Integer numeroPersonas;

    private String observaciones;

    @NotEmpty(message = "Se requiere al menos un producto en la comanda")
    @Valid
    private List<DetalleComandaItemDTO> items;
}
