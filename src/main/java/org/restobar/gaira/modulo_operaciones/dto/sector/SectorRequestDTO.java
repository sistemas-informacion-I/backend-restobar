package org.restobar.gaira.modulo_operaciones.dto.sector;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class SectorRequestDTO {
    
    @NotBlank(message = "Nombre no puede estar vacío")
    private String nombre;

    private String descripcion;

    @NotBlank(message = "Tipo de sector no puede estar vacío")
    private String tipoSector;

    @NotNull(message = "ID de sucursal es requerido")
    private Long idSucursal;
}
