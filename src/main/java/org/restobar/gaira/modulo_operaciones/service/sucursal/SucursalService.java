package org.restobar.gaira.modulo_operaciones.service.sucursal;

import java.util.stream.Collectors;

import java.util.List;

import org.restobar.gaira.modulo_operaciones.dto.sucursal.SucursalResponseDTO;
import org.restobar.gaira.modulo_operaciones.dto.sucursal.SucursalRequestDTO;
import org.restobar.gaira.modulo_operaciones.entity.*;
import org.restobar.gaira.modulo_operaciones.mapper.sucursal.SucursalMapper;
import org.restobar.gaira.modulo_operaciones.repository.SucursalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service // esta clase es un Service, regístrala y adminístrala tú
@RequiredArgsConstructor // Genera automáticamente un constructor con todos los campos que son final
public class SucursalService {

    private final SucursalRepository sucursalRepository;
    private final SucursalMapper sucursalMapper;

    @Transactional(readOnly = true) // solo lectura
    public List<SucursalResponseDTO> listarTodas() {
        return sucursalRepository.findByActivoTrue()
                .stream()
                .map(sucursalMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true) // solo lectura
    public SucursalResponseDTO obtenerPorId(Long id) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada con id: " + id));

        // ya esta validada y envia los datos 
        return sucursalMapper.toResponseDTO(sucursal);
    }

    @Transactional // modifica datos
    public SucursalResponseDTO crear(SucursalRequestDTO dto) {
        if (sucursalRepository.existsByCorreo(dto.getCorreo())) {
            throw new RuntimeException("Ya existe una sucursal con ese correo");
        }
        Sucursal sucursal = sucursalMapper.toEntity(dto);
        return sucursalMapper.toResponseDTO(sucursalRepository.save(sucursal));
    }

    @Transactional // modifica datos
    public SucursalResponseDTO actualizar(Long id, SucursalRequestDTO dto) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada con id: " + id));

        sucursal.setNombre(dto.getNombre());
        sucursal.setDireccion(dto.getDireccion());
        sucursal.setTelefono(dto.getTelefono());
        sucursal.setCorreo(dto.getCorreo());
        sucursal.setHorarioApertura(dto.getHorarioApertura());
        sucursal.setHorarioCierre(dto.getHorarioCierre());
        sucursal.setCiudad(dto.getCiudad());
        sucursal.setDepartamento(dto.getDepartamento());
        if (dto.getEstadoOperativo() != null) {
            sucursal.setEstadoOperativo(dto.getEstadoOperativo());
        }

        return sucursalMapper.toResponseDTO(sucursalRepository.save(sucursal));
    }

    @Transactional // modifica datos
    public void eliminar(Long id) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada con id: " + id));
        sucursal.setActivo(false); // borrado lógico
        sucursalRepository.save(sucursal);
    }
}
