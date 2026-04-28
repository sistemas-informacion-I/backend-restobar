package org.restobar.gaira.modulo_operaciones.service.mesa;

import lombok.RequiredArgsConstructor;
import org.restobar.gaira.modulo_operaciones.dto.mesa.MesaCreateDTO;
import org.restobar.gaira.modulo_operaciones.dto.mesa.MesaDTO;
import org.restobar.gaira.modulo_operaciones.dto.mesa.MesaUpdateDTO;
import org.restobar.gaira.modulo_operaciones.entity.Mesa;
import org.restobar.gaira.modulo_operaciones.entity.Sector;
import org.restobar.gaira.modulo_operaciones.mapper.mesa.MesaMapper;

import org.restobar.gaira.modulo_operaciones.repository.MesaRepository;
import org.restobar.gaira.modulo_operaciones.repository.SectorRepository;
import org.restobar.gaira.security.audit.annotation.Auditable;
import org.restobar.gaira.security.audit.util.AuditableService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MesaService implements AuditableService<Long, Object> {

    private final MesaRepository mesaRepository;
    private final SectorRepository sectorRepository;
    private final MesaMapper mesaMapper;

    @Override
    public Object getEntity(Long id) {
        return mesaRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof Mesa m) {
            return mesaMapper.toAuditMap(m);
        } else if (entity instanceof MesaDTO md) {
            return mesaMapper.toAuditMap(md);
        }
        return Map.of();
    }

    @Transactional(readOnly = true) // solo lectura
    public List<MesaDTO> getAllMesas() {
        return mesaRepository.findAll().stream()
                .map(mesaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true) // solo lectura
    public List<MesaDTO> getMesasBySector(Long idSector) {
        return mesaRepository.findBySectorIdSector(idSector).stream()
                .map(mesaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true) // solo lectura
    public MesaDTO getMesaById(Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Mesa no encontrada"));
        return mesaMapper.toDTO(mesa);
    }

    @Transactional // modifica datos
    @Auditable(tabla = "mesa", operacion = "INSERT")
    public MesaDTO createMesa(MesaCreateDTO dto) {
        // Verificar que el sector existe
        Sector sector = sectorRepository.findById(dto.getIdSector())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Sector no encontrado"));

        // Verificar que no exista otra mesa con el mismo número en el sector
        if (mesaRepository.existsByNumeroMesaAndSectorIdSector(dto.getNumeroMesa(), dto.getIdSector())) {
            throw new ResponseStatusException(CONFLICT, "Ya existe una mesa con ese número en el sector");
        }

        Mesa mesa = mesaMapper.toEntity(dto, sector);
        mesa = mesaRepository.save(mesa);
        return mesaMapper.toDTO(mesa);
    }

    @Transactional // modifica datos
    @Auditable(tabla = "mesa", operacion = "UPDATE", idParamName = "id")
    public MesaDTO updateMesa(Long id, MesaUpdateDTO dto) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Mesa no encontrada"));

        // Verificar que el sector existe
        Sector sector = sectorRepository.findById(dto.getIdSector())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Sector no encontrado"));

        // Verificar que no exista otra mesa con el mismo número en el sector (excluyendo la actual)
        if (!mesa.getNumeroMesa().equals(dto.getNumeroMesa()) &&
            mesaRepository.existsByNumeroMesaAndSectorIdSector(dto.getNumeroMesa(), dto.getIdSector())) {
            throw new ResponseStatusException(CONFLICT, "Ya existe una mesa con ese número en el sector");
        }

        mesaMapper.updateEntity(mesa, dto, sector);
        mesa = mesaRepository.save(mesa);
        return mesaMapper.toDTO(mesa);
    }

    @Transactional // modifica datos
    @Auditable(tabla = "mesa", operacion = "DELETE", idParamName = "id")
    public void deleteMesa(Long id) {
        if (!mesaRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Mesa no encontrada");
        }
        mesaRepository.deleteById(id);
    }
}