package org.restobar.gaira.modulo_operaciones.mapper.mesa;

import org.restobar.gaira.modulo_operaciones.dto.mesa.MesaCreateDTO;
import org.restobar.gaira.modulo_operaciones.dto.mesa.MesaDTO;
import org.restobar.gaira.modulo_operaciones.dto.mesa.MesaUpdateDTO;
import org.restobar.gaira.modulo_operaciones.entity.Mesa;
import org.restobar.gaira.modulo_operaciones.entity.Sector;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MesaMapper {

    public MesaDTO toDTO(Mesa mesa) {
        if (mesa == null) return null;

        return MesaDTO.builder()
                .idMesa(mesa.getIdMesa())
                .numeroMesa(mesa.getNumeroMesa())
                .capacidadPersonas(mesa.getCapacidadPersonas())
                .disponibilidad(mesa.getDisponibilidad())
                .activo(mesa.getActivo())
                .idSector(mesa.getSector() != null ? mesa.getSector().getIdSector() : null)
                .nombreSector(mesa.getSector() != null ? mesa.getSector().getNombre() : null)
                .build();
    }

    public Mesa toEntity(MesaCreateDTO dto, Sector sector) {
        if (dto == null) return null;

        return Mesa.builder()
                .numeroMesa(dto.getNumeroMesa())
                .capacidadPersonas(dto.getCapacidadPersonas())
                .sector(sector)
                .build();
    }

    public void updateEntity(Mesa mesa, MesaUpdateDTO dto, Sector sector) {
        if (mesa == null || dto == null) return;

        mesa.setNumeroMesa(dto.getNumeroMesa());
        mesa.setCapacidadPersonas(dto.getCapacidadPersonas());
        mesa.setSector(sector);
        if (dto.getActivo() != null) {
            mesa.setActivo(dto.getActivo());
        }
    }

    public Map<String, Object> toAuditMap(Mesa mesa) {
        if (mesa == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idMesa", mesa.getIdMesa());
        map.put("numeroMesa", mesa.getNumeroMesa());
        map.put("capacidad", mesa.getCapacidadPersonas());
        map.put("disponibilidad", mesa.getDisponibilidad());
        map.put("idSector", mesa.getSector() != null ? mesa.getSector().getIdSector() : null);
        map.put("activo", mesa.getActivo());
        return map;
    }

    public Map<String, Object> toAuditMap(MesaDTO dto) {
        if (dto == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idMesa", dto.getIdMesa());
        map.put("numeroMesa", dto.getNumeroMesa());
        map.put("capacidad", dto.getCapacidadPersonas());
        map.put("disponibilidad", dto.getDisponibilidad());
        map.put("idSector", dto.getIdSector());
        map.put("activo", dto.getActivo());
        return map;
    }
}