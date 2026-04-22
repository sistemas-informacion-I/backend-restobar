package org.restobar.gaira.modulo_operaciones.dto.sector;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class SectorResponseDTO {
    private Long idSector;
    private String nombre;
    private String descripcion;
    private String tipoSector;
    private Boolean activo;
    private Long idSucursal;
    private String nombreSucursal;
}
