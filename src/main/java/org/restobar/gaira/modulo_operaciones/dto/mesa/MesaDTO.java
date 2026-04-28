package org.restobar.gaira.modulo_operaciones.dto.mesa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MesaDTO {
    private Long idMesa;
    private String numeroMesa;
    private Integer capacidadPersonas;
    private String disponibilidad;
    private Boolean activo;
    private Long idSector;
    private String nombreSector;
}