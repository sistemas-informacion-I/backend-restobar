package org.restobar.gaira.modulo_inventario.service.inventario;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.restobar.gaira.modulo_inventario.dto.inventario.InventarioRequest;
import org.restobar.gaira.modulo_inventario.dto.inventario.InventarioResponse;
import org.restobar.gaira.modulo_inventario.entity.Inventario;
import org.restobar.gaira.modulo_inventario.mapper.inventario.InventarioMapper;
import org.restobar.gaira.modulo_inventario.repository.InventarioRepository;
import org.restobar.gaira.security.audit.annotation.Auditable;
import org.restobar.gaira.security.audit.util.AuditableService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventarioService implements AuditableService<Long, Object> {

    private final InventarioRepository inventarioRepository;
    private final InventarioMapper inventarioMapper;

    @Override
    public Object getEntity(Long id) {
        return inventarioRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof Inventario inv) {
            return inventarioMapper.mapToAudit(inv);
        }
        return Map.of();
    }

    @Transactional(readOnly = true)
    public List<InventarioResponse> listarTodos() {
        return inventarioRepository.findAll().stream()
                .map(inventarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InventarioResponse obtenerPorId(Long id) {
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Insumo no encontrado"));
        return inventarioMapper.toResponse(inventario);
    }

    @Transactional
    @Auditable(tabla = "inventario", operacion = "INSERT")
    public InventarioResponse crear(InventarioRequest dto) {
        if (inventarioRepository.existsByCodigo(dto.getCodigo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un insumo con ese código");
        }
        Inventario inventario = inventarioMapper.toEntity(dto);
        return inventarioMapper.toResponse(inventarioRepository.save(inventario));
    }

    @Transactional
    @Auditable(tabla = "inventario", operacion = "UPDATE", idParamName = "id")
    public InventarioResponse actualizar(Long id, InventarioRequest dto) {
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Insumo no encontrado"));

        inventario.setNombre(dto.getNombre());
        inventario.setDescripcion(dto.getDescripcion());
        inventario.setUnidadMedida(dto.getUnidadMedida());
        inventario.setMarca(dto.getMarca());
        inventario.setEsRehutilizable(dto.getEsRehutilizable());
        inventario.setActivo(dto.getActivo());

        return inventarioMapper.toResponse(inventarioRepository.save(inventario));
    }

    @Transactional
    @Auditable(tabla = "inventario", operacion = "DELETE", idParamName = "id")
    public void eliminar(Long id) {
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Insumo no encontrado"));
        inventario.setActivo(false);
        inventarioRepository.save(inventario);
    }
}
