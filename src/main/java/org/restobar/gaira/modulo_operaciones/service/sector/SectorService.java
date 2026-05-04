package org.restobar.gaira.modulo_operaciones.service.sector;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.restobar.gaira.modulo_operaciones.dto.sector.SectorRequestDTO;
import org.restobar.gaira.modulo_operaciones.dto.sector.SectorResponseDTO;
import org.restobar.gaira.modulo_operaciones.entity.Sector;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.restobar.gaira.modulo_operaciones.mapper.sector.SectorMapper;
import org.restobar.gaira.modulo_operaciones.repository.SectorRepository;
import org.restobar.gaira.modulo_operaciones.repository.SucursalRepository;
import org.restobar.gaira.security.audit.annotation.Auditable;
import org.restobar.gaira.security.audit.util.AuditableService;
import org.restobar.gaira.security.utils.SecurityUtils;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SectorService implements AuditableService<Long, Object> {

    private final SectorRepository sectorRepository;
    private final SucursalRepository sucursalRepository;
    private final SectorMapper sectorMapper;
    private final SecurityUtils securityUtils;

    @Override
    public Object getEntity(Long id) {
        return sectorRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof Sector s) {
            return sectorMapper.toAuditMap(s);
        } else if (entity instanceof SectorResponseDTO sr) {
            return sectorMapper.toAuditMap(sr);
        }
        return Map.of();
    }

    @Transactional(readOnly = true)
    public List<SectorResponseDTO> listarTodos() {
        Usuario currentUser = securityUtils.getCurrentUser();
        if (currentUser == null) return List.of();

        if (currentUser.getTipoUsuario().equals("S")) {
            return sectorRepository.findByActivoTrue().stream()
                    .map(sectorMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } else {
            Long idSucursal = securityUtils.getCurrentSucursalId();
            if (idSucursal == null) return List.of();
            return listarPorSucursal(idSucursal);
        }
    }

    @Transactional(readOnly = true)
    public SectorResponseDTO obtenerPorId(Long id) {
        Sector sector = sectorRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Sector no encontrado"));
        
        validarPropiedadSector(sector);
        return sectorMapper.toResponseDTO(sector);
    }

    @Transactional(readOnly = true)
    public List<SectorResponseDTO> listarPorSucursal(Long idSucursal) {
        return sectorRepository.findBySucursal_IdSucursalAndActivoTrue(idSucursal).stream()
                .map(sectorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Auditable(tabla = "sector", operacion = "INSERT")
    public SectorResponseDTO crear(SectorRequestDTO dto) {
        Usuario currentUser = securityUtils.getCurrentUser();
        Long idSucursalFinal = currentUser != null && currentUser.getTipoUsuario().equals("S") 
            ? dto.getIdSucursal() 
            : securityUtils.getCurrentSucursalId();

        if (idSucursalFinal == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Sucursal no definida para el sector");
        }

        Sucursal sucursal = sucursalRepository.findById(idSucursalFinal)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Sucursal no encontrada"));

        if (sectorRepository.existsByNombreAndSucursal_IdSucursal(dto.getNombre(), idSucursalFinal)) {
            throw new ResponseStatusException(BAD_REQUEST, "Ya existe un sector con ese nombre en esta sucursal");
        }

        var sector = sectorMapper.toEntity(dto, sucursal);
        return sectorMapper.toResponseDTO(sectorRepository.save(sector));
    }

    @Transactional
    @Auditable(tabla = "sector", operacion = "UPDATE", idParamName = "id")
    public SectorResponseDTO actualizar(Long id, SectorRequestDTO dto) {
        var sector = sectorRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Sector no encontrado"));

        validarPropiedadSector(sector);

        sector.setNombre(dto.getNombre());
        sector.setDescripcion(dto.getDescripcion());
        sector.setTipoSector(dto.getTipoSector());

        return sectorMapper.toResponseDTO(sectorRepository.save(sector));
    }

    @Transactional
    @Auditable(tabla = "sector", operacion = "UPDATE", idParamName = "id")
    public void eliminar(Long id) {
        var sector = sectorRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Sector no encontrado"));
        
        validarPropiedadSector(sector);
        
        sector.setActivo(false);
        sectorRepository.save(sector);
    }

    private void validarPropiedadSector(Sector sector) {
        Usuario currentUser = securityUtils.getCurrentUser();
        if (currentUser == null || currentUser.getTipoUsuario().equals("S")) return;

        Long idSucursalAdmin = securityUtils.getCurrentSucursalId();
        if (!sector.getSucursal().getIdSucursal().equals(idSucursalAdmin)) {
            throw new ResponseStatusException(BAD_REQUEST, "No tiene permisos sobre sectores de otra sucursal");
        }
    }
}