package org.restobar.gaira.modulo_operaciones.mapper.sector;

import org.restobar.gaira.modulo_operaciones.dto.sector.SectorRequestDTO;
import org.restobar.gaira.modulo_operaciones.dto.sector.SectorResponseDTO;
import org.restobar.gaira.modulo_operaciones.entity.Sector;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.springframework.stereotype.Component;

@Component
public class SectorMapper {

    public SectorResponseDTO toResponseDTO(Sector s) {
        return SectorResponseDTO.builder()
                .idSector(s.getIdSector())
                .nombre(s.getNombre())
                .descripcion(s.getDescripcion())
                .tipoSector(s.getTipoSector())
                .activo(s.getActivo())
                .idSucursal(s.getSucursal() != null ? s.getSucursal().getIdSucursal() : null)
                .nombreSucursal(s.getSucursal() != null ? s.getSucursal().getNombre() : null)
                .build();
    }

    public Sector toEntity(SectorRequestDTO dto, Sucursal sucursal) {
        return Sector.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .tipoSector(dto.getTipoSector())
                .sucursal(sucursal)
                .build();
    }
}