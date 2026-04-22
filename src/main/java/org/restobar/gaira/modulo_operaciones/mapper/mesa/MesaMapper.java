package org.restobar.gaira.modulo_operaciones.mapper.mesa;

import org.restobar.gaira.modulo_operaciones.dto.mesa.MesaCreateDTO;
import org.restobar.gaira.modulo_operaciones.dto.mesa.MesaDTO;
import org.restobar.gaira.modulo_operaciones.dto.mesa.MesaUpdateDTO;
import org.restobar.gaira.modulo_operaciones.entity.Mesa;
import org.restobar.gaira.modulo_operaciones.entity.Sector;
import org.springframework.stereotype.Component;

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
}