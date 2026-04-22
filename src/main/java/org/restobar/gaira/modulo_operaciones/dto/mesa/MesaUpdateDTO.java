package org.restobar.gaira.modulo_operaciones.dto.mesa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MesaUpdateDTO {
    @NotBlank(message = "Número de mesa es requerido")
    private String numeroMesa;

    @NotNull(message = "Capacidad es requerida")
    @Positive(message = "Capacidad debe ser mayor a 0")
    private Integer capacidadPersonas;

    @NotNull(message = "ID de sector es requerido")
    private Long idSector;

    private Boolean activo;
}