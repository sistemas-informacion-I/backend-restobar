package org.restobar.gaira.modulo_operaciones.mapper.sector;

import org.restobar.gaira.modulo_operaciones.dto.sector.SectorRequestDTO;
import org.restobar.gaira.modulo_operaciones.dto.sector.SectorResponseDTO;
import org.restobar.gaira.modulo_operaciones.entity.Sector;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

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

    public Map<String, Object> toAuditMap(Sector sector) {
        if (sector == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idSector", sector.getIdSector());
        map.put("nombre", sector.getNombre());
        map.put("descripcion", sector.getDescripcion());
        map.put("tipoSector", sector.getTipoSector());
        map.put("idSucursal", sector.getSucursal() != null ? sector.getSucursal().getIdSucursal() : null);
        map.put("activo", sector.getActivo());
        return map;
    }

    public Map<String, Object> toAuditMap(SectorResponseDTO dto) {
        if (dto == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idSector", dto.getIdSector());
        map.put("nombre", dto.getNombre());
        map.put("descripcion", dto.getDescripcion());
        map.put("tipoSector", dto.getTipoSector());
        map.put("idSucursal", dto.getIdSucursal());
        map.put("activo", dto.getActivo());
        return map;
    }
}