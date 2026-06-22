package org.restobar.gaira.modulo_inventario.service.lote;

import java.math.BigDecimal;
import java.util.Map;

import org.restobar.gaira.modulo_inventario.dto.lote.LoteRequest;
import org.restobar.gaira.modulo_inventario.dto.lote.LoteResponse;
import org.restobar.gaira.modulo_inventario.entity.LoteInventario;
import org.restobar.gaira.modulo_inventario.entity.LoteInventario.EstadoLote;
import org.restobar.gaira.modulo_inventario.entity.StockSucursal;
import org.restobar.gaira.modulo_inventario.mapper.lote.LoteMapper;
import org.restobar.gaira.modulo_inventario.repository.LoteInventarioRepository;
import org.restobar.gaira.modulo_inventario.repository.StockSucursalRepository;
import org.restobar.gaira.modulo_inventario.service.alerta.AlertaInventarioService;
import org.restobar.gaira.modulo_inventario.service.stock.StockSucursalService;
import org.restobar.gaira.security.audit.annotation.Auditable;
import org.restobar.gaira.security.audit.util.AuditableService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoteInventarioService implements AuditableService<Long, Object> {

    private final StockSucursalRepository stockRepository;
    private final LoteInventarioRepository loteRepository;
    private final AlertaInventarioService alertaService;
    private final LoteMapper loteMapper;
    private final StockSucursalService stockSucursalService;

    @Override
    public Object getEntity(Long id) {
        return loteRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof LoteInventario lote) {
            return loteMapper.toAuditMap(lote);
        }
        if (entity instanceof LoteResponse response) {
            return loteMapper.toAuditMap(response);
        }
        return Map.of();
    }

    @Transactional
    @Auditable(tabla = "lote_inventario", operacion = "INSERT")
    public LoteResponse agregarLote(LoteRequest dto) {
        StockSucursal stock = stockRepository.findById(dto.getIdStock())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro de stock no encontrado"));

        if (dto.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad del lote debe ser mayor a cero");
        }

        if (dto.getFechaVencimiento() != null && dto.getFechaVencimiento().isBefore(java.time.LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se puede registrar un lote con fecha de vencimiento pasada");
        }

        LoteInventario lote = loteMapper.toEntity(dto, stock);
        lote = loteRepository.save(lote);
        StockSucursal actualizedStock = stockSucursalService.recalcularStockDesdeLotes(stock.getIdStock());
        alertaService.resolverAlertasPorStock(actualizedStock);
        alertaService.generarAlertaStockMinimoParaStock(actualizedStock);
        alertaService.generarAlertaVencimientoProximoParaLote(lote);

        return loteMapper.toResponse(lote);
    }

    @Transactional
    @Auditable(tabla = "lote_inventario", operacion = "UPDATE", idParamName = "idLote")
    public LoteResponse actualizarEstadoLote(Long idLote, EstadoLote nuevoEstado) {
        LoteInventario lote = loteRepository.findById(idLote)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote no encontrado"));

        lote.setEstado(nuevoEstado);
        lote = loteRepository.save(lote);
        StockSucursal actualizedStock = stockSucursalService.recalcularStockDesdeLotes(lote.getStockSucursal().getIdStock());
        alertaService.resolverAlertasPorStock(actualizedStock);
        alertaService.generarAlertaStockMinimoParaStock(actualizedStock);
        alertaService.resolverAlertasPorLote(lote);
        alertaService.generarAlertaVencimientoProximoParaLote(lote);

        return loteMapper.toResponse(lote);
    }

    @Transactional(readOnly = true)
    public Page<LoteResponse> listarLotesPorStock(Long idStock, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("fechaIngreso").descending().and(Sort.by("idLote").descending()));
        return loteRepository.findByStockSucursalIdStock(idStock, pageable)
                .map(loteMapper::toResponse);
    }
}
