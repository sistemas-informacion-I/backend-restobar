package org.restobar.gaira.modulo_comercial.service.detalleNotaVenta;

import java.util.List;
import java.util.Map;

import org.restobar.gaira.modulo_comercial.dto.detalleNotaVenta.DetalleNotaVentaResponse;
import org.restobar.gaira.modulo_comercial.entity.DetalleNotaVenta;
import org.restobar.gaira.modulo_comercial.mapper.detalleNotaVenta.DetalleNotaVentaMapper;
import org.restobar.gaira.modulo_comercial.repository.detalleNotaVenta.DetalleNotaVentaRepository;
import org.restobar.gaira.security.audit.util.AuditableService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DetalleNotaVentaService implements AuditableService<Long, Object> {

    private final DetalleNotaVentaRepository detalleNotaVentaRepository;
    private final DetalleNotaVentaMapper detalleNotaVentaMapper;

    @Override
    public Object getEntity(Long id) {
        return detalleNotaVentaRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof DetalleNotaVenta dnv) {
            return detalleNotaVentaMapper.toAuditMap(dnv);
        }
        return Map.of();
    }

    @Transactional(readOnly = true)
    public List<DetalleNotaVentaResponse> findByNotaVentaId(Long idNotaVenta) {
        return detalleNotaVentaRepository.findByNotaVenta_IdNotaVenta(idNotaVenta)
                .stream()
                .map(detalleNotaVentaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DetalleNotaVentaResponse findById(Long id) {
        DetalleNotaVenta detalle = detalleNotaVentaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle de nota de venta no encontrado"));
        return detalleNotaVentaMapper.toResponse(detalle);
    }
}