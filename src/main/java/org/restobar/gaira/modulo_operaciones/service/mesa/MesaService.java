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
import org.restobar.gaira.security.utils.SecurityUtils;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MesaService implements AuditableService<Long, Object> {

    private final MesaRepository mesaRepository;
    private final SectorRepository sectorRepository;
    private final MesaMapper mesaMapper;
    private final SecurityUtils securityUtils;

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

    @Transactional(readOnly = true)
    public List<MesaDTO> getAllMesas() {
        Usuario currentUser = securityUtils.getCurrentUser();
        if (currentUser == null) return List.of();

        if (currentUser.getTipoUsuario().equals("S")) {
            return mesaRepository.findAll().stream()
                    .map(mesaMapper::toDTO)
                    .collect(Collectors.toList());
        } else {
            Long idSucursal = securityUtils.getCurrentSucursalId();
            if (idSucursal == null) return List.of();
            return mesaRepository.findBySucursalId(idSucursal).stream()
                    .map(mesaMapper::toDTO)
                    .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public List<MesaDTO> getMesasBySector(Long idSector) {
        Sector sector = sectorRepository.findById(idSector)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Sector no encontrado"));
        
        validarPropiedadSector(sector);

        return mesaRepository.findBySectorIdSector(idSector).stream()
                .map(mesaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MesaDTO getMesaById(Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Mesa no encontrada"));
        
        validarPropiedadSector(mesa.getSector());
        return mesaMapper.toDTO(mesa);
    }

    @Transactional
    @Auditable(tabla = "mesa", operacion = "INSERT")
    public MesaDTO createMesa(MesaCreateDTO dto) {
        Sector sector = sectorRepository.findById(dto.getIdSector())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Sector no encontrado"));

        validarPropiedadSector(sector);

        if (mesaRepository.existsByNumeroMesaAndSectorIdSector(dto.getNumeroMesa(), dto.getIdSector())) {
            throw new ResponseStatusException(CONFLICT, "Ya existe una mesa con ese número en el sector");
        }

        Mesa mesa = mesaMapper.toEntity(dto, sector);
        mesa = mesaRepository.save(mesa);
        return mesaMapper.toDTO(mesa);
    }

    @Transactional
    @Auditable(tabla = "mesa", operacion = "UPDATE", idParamName = "id")
    public MesaDTO updateMesa(Long id, MesaUpdateDTO dto) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Mesa no encontrada"));

        validarPropiedadSector(mesa.getSector());

        Sector newSector = sectorRepository.findById(dto.getIdSector())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Sector no encontrado"));

        validarPropiedadSector(newSector);

        if (!mesa.getNumeroMesa().equals(dto.getNumeroMesa()) &&
            mesaRepository.existsByNumeroMesaAndSectorIdSector(dto.getNumeroMesa(), dto.getIdSector())) {
            throw new ResponseStatusException(CONFLICT, "Ya existe una mesa con ese número en el sector");
        }

        mesaMapper.updateEntity(mesa, dto, newSector);
        mesa = mesaRepository.save(mesa);
        return mesaMapper.toDTO(mesa);
    }

    @Transactional
    @Auditable(tabla = "mesa", operacion = "UPDATE", idParamName = "id")
    public void deleteMesa(Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Mesa no encontrada"));
        
        validarPropiedadSector(mesa.getSector());
        
        mesa.setActivo(false);
        mesaRepository.save(mesa);
    }

    private void validarPropiedadSector(Sector sector) {
        Usuario currentUser = securityUtils.getCurrentUser();
        if (currentUser == null || currentUser.getTipoUsuario().equals("S")) return;

        Long idSucursalAdmin = securityUtils.getCurrentSucursalId();
        if (!sector.getSucursal().getIdSucursal().equals(idSucursalAdmin)) {
            throw new ResponseStatusException(BAD_REQUEST, "No tiene permisos sobre recursos de otra sucursal");
        }
    }
}