package org.restobar.gaira.modulo_operaciones.mapper.sucursal;

import org.restobar.gaira.modulo_operaciones.dto.sucursal.SucursalRequestDTO;
import org.restobar.gaira.modulo_operaciones.dto.sucursal.SucursalResponseDTO;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.springframework.stereotype.Component;


@Component
public class SucursalMapper {
    

        public SucursalResponseDTO toResponseDTO(Sucursal s) {
        return SucursalResponseDTO.builder()
                .idSucursal(s.getIdSucursal())
                .nombre(s.getNombre())
                .direccion(s.getDireccion())
                .telefono(s.getTelefono())
                .correo(s.getCorreo())
                .horarioApertura(s.getHorarioApertura())
                .horarioCierre(s.getHorarioCierre())
                .ciudad(s.getCiudad())
                .departamento(s.getDepartamento())
                .estadoOperativo(s.getEstadoOperativo())
                .activo(s.getActivo())
                .build();
    }

    public Sucursal toEntity(SucursalRequestDTO dto) {
        return Sucursal.builder()
                .nombre(dto.getNombre())
                .direccion(dto.getDireccion())
                .telefono(dto.getTelefono())
                .correo(dto.getCorreo())
                .horarioApertura(dto.getHorarioApertura())
                .horarioCierre(dto.getHorarioCierre())
                .ciudad(dto.getCiudad())
                .departamento(dto.getDepartamento())
                .estadoOperativo(dto.getEstadoOperativo())
                .build();
    }
}
