package org.restobar.gaira.modulo_comercial.service.detalleCompra;

import java.util.List;
import java.util.Map;

import org.restobar.gaira.modulo_comercial.dto.detalleCompra.DetalleCompraResponse;
import org.restobar.gaira.modulo_comercial.entity.DetalleCompra;
import org.restobar.gaira.modulo_comercial.mapper.detalleCompra.DetalleCompraMapper;
import org.restobar.gaira.modulo_comercial.repository.detalleCompra.DetalleCompraRepository;
import org.restobar.gaira.security.audit.util.AuditableService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DetalleCompraService implements AuditableService<Long, Object> {

    private final DetalleCompraRepository detalleCompraRepository;
    private final DetalleCompraMapper detalleCompraMapper;

    // ─── AuditableService ────────────────────────────────────────────────────

    @Override
    public Object getEntity(Long id) {
        return detalleCompraRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof DetalleCompra dc) {
            return detalleCompraMapper.toAuditMap(dc);
        }
        return Map.of();
    }

    // ─── Consultas ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<DetalleCompraResponse> findByCompraId(Long idCompra) {
        return detalleCompraRepository.findByCompra_IdCompra(idCompra)
                .stream()
                .map(detalleCompraMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DetalleCompraResponse findById(Long id) {
        DetalleCompra detalle = detalleCompraRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle de compra no encontrado"));
        return detalleCompraMapper.toResponse(detalle);
    }
}
