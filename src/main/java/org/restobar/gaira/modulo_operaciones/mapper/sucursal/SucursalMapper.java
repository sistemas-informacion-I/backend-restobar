package org.restobar.gaira.modulo_operaciones.mapper.sucursal;

import org.restobar.gaira.modulo_operaciones.dto.sucursal.SucursalRequestDTO;
import org.restobar.gaira.modulo_operaciones.dto.sucursal.SucursalResponseDTO;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

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

    public Map<String, Object> toAuditMap(Sucursal sucursal) {
        if (sucursal == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idSucursal", sucursal.getIdSucursal());
        map.put("nombre", sucursal.getNombre());
        map.put("direccion", sucursal.getDireccion());
        map.put("telefono", sucursal.getTelefono());
        map.put("correo", sucursal.getCorreo());
        map.put("horarioApertura", sucursal.getHorarioApertura());
        map.put("horarioCierre", sucursal.getHorarioCierre());
        map.put("ciudad", sucursal.getCiudad());
        map.put("departamento", sucursal.getDepartamento());
        map.put("estadoOperativo", sucursal.getEstadoOperativo());
        map.put("activo", sucursal.getActivo());
        return map;
    }

    public Map<String, Object> toAuditMap(SucursalResponseDTO dto) {
        if (dto == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idSucursal", dto.getIdSucursal());
        map.put("nombre", dto.getNombre());
        map.put("direccion", dto.getDireccion());
        map.put("telefono", dto.getTelefono());
        map.put("correo", dto.getCorreo());
        map.put("horarioApertura", dto.getHorarioApertura());
        map.put("horarioCierre", dto.getHorarioCierre());
        map.put("ciudad", dto.getCiudad());
        map.put("departamento", dto.getDepartamento());
        map.put("estadoOperativo", dto.getEstadoOperativo());
        map.put("activo", dto.getActivo());
        return map;
    }
}

