package org.restobar.gaira.modulo_operaciones.service.sector;

import java.util.List;
import java.util.stream.Collectors;

import org.restobar.gaira.modulo_operaciones.dto.sector.SectorRequestDTO;
import org.restobar.gaira.modulo_operaciones.dto.sector.SectorResponseDTO;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.restobar.gaira.modulo_operaciones.mapper.sector.SectorMapper;
import org.restobar.gaira.modulo_operaciones.repository.SectorRepository;
import org.restobar.gaira.modulo_operaciones.repository.SucursalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SectorService {

    private final SectorRepository sectorRepository;
    private final SucursalRepository sucursalRepository;
    private final SectorMapper sectorMapper;

    @Transactional(readOnly = true)
    public List<SectorResponseDTO> listarTodos() {
        return sectorRepository.findByActivoTrue().stream()
                .map(sectorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SectorResponseDTO obtenerPorId(Long id) {
        return sectorMapper.toResponseDTO(
            sectorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sector no encontrado con id: " + id))
        );
    }

    @Transactional(readOnly = true)
    public List<SectorResponseDTO> listarPorSucursal(Long idSucursal) {
        return sectorRepository.findBySucursal_IdSucursalAndActivoTrue(idSucursal).stream()
                .map(sectorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SectorResponseDTO crear(SectorRequestDTO dto) {
        Sucursal sucursal = sucursalRepository.findById(dto.getIdSucursal())
            .orElseThrow(() -> new RuntimeException("Sucursal no encontrada con id: " + dto.getIdSucursal()));

        if (sectorRepository.existsByNombreAndSucursal_IdSucursal(dto.getNombre(), dto.getIdSucursal())) {
            throw new RuntimeException("Ya existe un sector con ese nombre en esta sucursal");
        }

        var sector = sectorMapper.toEntity(dto, sucursal);
        return sectorMapper.toResponseDTO(sectorRepository.save(sector));
    }

    @Transactional
    public SectorResponseDTO actualizar(Long id, SectorRequestDTO dto) {
        var sector = sectorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sector no encontrado con id: " + id));

        sector.setNombre(dto.getNombre());
        sector.setDescripcion(dto.getDescripcion());
        sector.setTipoSector(dto.getTipoSector());

        return sectorMapper.toResponseDTO(sectorRepository.save(sector));
    }

    @Transactional
    public void eliminar(Long id) {
        var sector = sectorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sector no encontrado con id: " + id));
        sector.setActivo(false);
        sectorRepository.save(sector);
    }
}