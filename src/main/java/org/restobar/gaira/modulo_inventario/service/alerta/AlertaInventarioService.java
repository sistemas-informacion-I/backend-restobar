package org.restobar.gaira.modulo_inventario.service.alerta;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.restobar.gaira.modulo_inventario.dto.alerta.AlertaInventarioRequest;
import org.restobar.gaira.modulo_inventario.dto.alerta.AlertaInventarioResponse;
import org.restobar.gaira.modulo_inventario.entity.AlertaInventario;
import org.restobar.gaira.modulo_inventario.entity.AlertaInventario.EstadoAlerta;
import org.restobar.gaira.modulo_inventario.entity.AlertaInventario.TipoAlerta;
import org.restobar.gaira.modulo_inventario.entity.LoteInventario;
import org.restobar.gaira.modulo_inventario.entity.StockSucursal;
import org.restobar.gaira.modulo_inventario.mapper.alerta.AlertaInventarioMapper;
import org.restobar.gaira.modulo_inventario.repository.AlertaInventarioRepository;
import org.restobar.gaira.modulo_inventario.repository.LoteInventarioRepository;
import org.restobar.gaira.modulo_inventario.repository.StockSucursalRepository;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.restobar.gaira.security.audit.annotation.Auditable;
import org.restobar.gaira.security.audit.util.AuditableService;
import org.restobar.gaira.security.utils.SecurityUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertaInventarioService implements AuditableService<Long, Object> {

    private final AlertaInventarioRepository alertaRepository;
    private final StockSucursalRepository stockRepository;
    private final LoteInventarioRepository loteRepository;
    private final AlertaInventarioMapper mapper;
    private final SecurityUtils securityUtils;

    private static final List<EstadoAlerta> ALERTAS_ACTIVAS = List.of(EstadoAlerta.NO_LEIDA, EstadoAlerta.LEIDA);
    private static final int DEFAULT_DIAS_AVISO = 7;

    @Override
    public Object getEntity(Long id) {
        return alertaRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof AlertaInventario alerta) {
            return mapper.toAuditMap(alerta);
        }
        return Map.of();
    }

    @Transactional(readOnly = true)
    public List<AlertaInventarioResponse> listarAlertas(AlertaInventarioRequest request) {
        Long idSucursal = request.getIdSucursal();
        String tipo = request.getTipo() != null ? request.getTipo().name() : null;
        String estado = request.getEstado() != null ? request.getEstado().name() : null;

        if (!securityUtils.isSuperUser()) {
            idSucursal = securityUtils.getCurrentSucursalId();
        }

        Specification<AlertaInventario> specification = Specification.where(porSucursal(idSucursal))
                .and(porTipo(parseTipo(tipo)))
                .and(porEstado(parseEstado(estado)));

        return alertaRepository.findAll(specification).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long contarAlertasPendientes(Long idSucursal) {
        if (!securityUtils.isSuperUser()) {
            idSucursal = securityUtils.getCurrentSucursalId();
        }

        if (idSucursal == null) {
            return alertaRepository.countByEstadoIn(List.of(EstadoAlerta.NO_LEIDA, EstadoAlerta.LEIDA));
        }

        return alertaRepository.countByEstadoInAndSucursalIdSucursal(ALERTAS_ACTIVAS, idSucursal);
    }

    @Transactional
    @Auditable(tabla = "alerta_inv", operacion = "UPDATE", idParamName = "idAlerta")
    public AlertaInventarioResponse marcarComoLeida(Long idAlerta) {
        AlertaInventario alerta = alertaRepository.findById(idAlerta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alerta no encontrada"));

        if (alerta.getEstado() == EstadoAlerta.RESUELTA) {
            return mapper.toResponse(alerta);
        }

        alerta.setEstado(EstadoAlerta.LEIDA);
        return mapper.toResponse(alertaRepository.save(alerta));
    }

    @Transactional
    @Scheduled(fixedDelayString = "${app.inventory.alerts.fixed-delay:86400000}")
    public void generarYResolverAlertasProgramadas() {
        generarAlertasAutomaticas();
        resolverAlertasPendientes();
    }

    @Transactional
    public void generarAlertasAutomaticas() {
        List<StockSucursal> stocks = stockRepository.findAll().stream()
                .filter(stock -> Boolean.TRUE.equals(stock.getActivo()))
                .filter(stock -> stock.getCantidad() != null && stock.getCantidadMinima() != null)
                .filter(stock -> stock.getCantidad().compareTo(stock.getCantidadMinima()) <= 0)
                .collect(Collectors.toList());

        for (StockSucursal stock : stocks) {
            generarAlertaStockMinimo(stock);
        }

        LocalDate hoy = LocalDate.now();
        loteRepository.findAll().stream()
                .filter(lote -> lote.getFechaVencimiento() != null)
                .filter(lote -> lote.getEstado() == LoteInventario.EstadoLote.DISPONIBLE)
                .forEach(lote -> generarAlertaVencimientoProximo(lote, hoy));
    }

    @Transactional
    public void resolverAlertasPendientes() {
        List<AlertaInventario> pendientes = alertaRepository.findByEstadoIn(ALERTAS_ACTIVAS);
        LocalDate hoy = LocalDate.now();

        for (AlertaInventario alerta : pendientes) {
            if (alerta.getTipo() == TipoAlerta.STOCK_MINIMO) {
                if (debeResolverAlertaStock(alerta)) {
                    resolverAlerta(alerta);
                }
            } else if (alerta.getTipo() == TipoAlerta.VENCIMIENTO_PROXIMO) {
                if (debeResolverAlertaLote(alerta, hoy)) {
                    resolverAlerta(alerta);
                }
            }
        }
    }

    public void resolverAlertasPorStock(StockSucursal stock) {
        if (stock == null) return;
        alertaRepository.findAll(Specification.where(porTipo(TipoAlerta.STOCK_MINIMO))
                .and(porStock(stock.getIdStock()))
                .and(porEstadoIn(ALERTAS_ACTIVAS))).stream()
                .filter(this::debeResolverAlertaStock)
                .forEach(this::resolverAlerta);
    }

    public void resolverAlertasPorLote(LoteInventario lote) {
        if (lote == null) return;
        alertaRepository.findAll(Specification.where(porTipo(TipoAlerta.VENCIMIENTO_PROXIMO))
                .and(porLote(lote.getIdLote()))
                .and(porEstadoIn(ALERTAS_ACTIVAS))).stream()
                .filter(alerta -> debeResolverAlertaLote(alerta, LocalDate.now()))
                .forEach(this::resolverAlerta);
    }

    public void generarAlertaStockMinimoParaStock(StockSucursal stock) {
        if (stock == null || !Boolean.TRUE.equals(stock.getActivo())
                || stock.getCantidad() == null || stock.getCantidadMinima() == null) {
            return;
        }

        if (stock.getCantidad().compareTo(stock.getCantidadMinima()) > 0) {
            return;
        }

        if (alertaRepository.existsByTipoAndStockSucursalIdStockAndEstadoIn(TipoAlerta.STOCK_MINIMO,
                stock.getIdStock(), ALERTAS_ACTIVAS)) {
            return;
        }

        AlertaInventario alerta = AlertaInventario.builder()
                .sucursal(stock.getSucursal())
                .stockSucursal(stock)
                .tipo(TipoAlerta.STOCK_MINIMO)
                .estado(EstadoAlerta.NO_LEIDA)
                .build();
        alertaRepository.save(alerta);
    }

    public void generarAlertaVencimientoProximoParaLote(LoteInventario lote) {
        if (lote == null || lote.getFechaVencimiento() == null
                || lote.getStockSucursal() == null
                || lote.getStockSucursal().getSucursal() == null
                || lote.getEstado() != LoteInventario.EstadoLote.DISPONIBLE) {
            return;
        }

        generarAlertaVencimientoProximo(lote, LocalDate.now());
    }

    private void generarAlertaStockMinimo(StockSucursal stock) {
        if (alertaRepository.existsByTipoAndStockSucursalIdStockAndEstadoIn(TipoAlerta.STOCK_MINIMO,
                stock.getIdStock(), ALERTAS_ACTIVAS)) {
            return;
        }

        AlertaInventario alerta = AlertaInventario.builder()
                .sucursal(stock.getSucursal())
                .stockSucursal(stock)
                .tipo(TipoAlerta.STOCK_MINIMO)
                .estado(EstadoAlerta.NO_LEIDA)
                .build();
        alertaRepository.save(alerta);
    }

    private void generarAlertaVencimientoProximo(LoteInventario lote, LocalDate hoy) {
        if (lote.getStockSucursal() == null || lote.getStockSucursal().getSucursal() == null) {
            return;
        }

        Integer diasAlerta = Optional.ofNullable(lote.getStockSucursal().getSucursal().getDiasAlertaVencimiento())
                .orElse(DEFAULT_DIAS_AVISO);
        LocalDate fechaLimite = hoy.plusDays(diasAlerta);

        if (lote.getFechaVencimiento().isAfter(fechaLimite)) {
            return;
        }

        if (alertaRepository.existsByTipoAndLoteInventarioIdLoteAndEstadoIn(TipoAlerta.VENCIMIENTO_PROXIMO,
                lote.getIdLote(), ALERTAS_ACTIVAS)) {
            return;
        }

        AlertaInventario alerta = AlertaInventario.builder()
                .sucursal(lote.getStockSucursal().getSucursal())
                .stockSucursal(lote.getStockSucursal())
                .loteInventario(lote)
                .tipo(TipoAlerta.VENCIMIENTO_PROXIMO)
                .estado(EstadoAlerta.NO_LEIDA)
                .build();
        alertaRepository.save(alerta);
    }

    private boolean debeResolverAlertaStock(AlertaInventario alerta) {
        return Optional.ofNullable(alerta.getStockSucursal())
                .map(StockSucursal::getIdStock)
                .map(stockRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(stock -> stock.getCantidad() != null && stock.getCantidadMinima() != null
                        && stock.getCantidad().compareTo(stock.getCantidadMinima()) >= 0)
                .orElse(true);
    }

    private boolean debeResolverAlertaLote(AlertaInventario alerta, LocalDate hoy) {
        if (alerta == null || alerta.getLoteInventario() == null) {
            return true;
        }

        Optional<LoteInventario> loteOpt = loteRepository.findById(alerta.getLoteInventario().getIdLote());
        if (loteOpt.isEmpty()) {
            return true;
        }

        LoteInventario lote = loteOpt.get();
        return lote.getEstado() != LoteInventario.EstadoLote.DISPONIBLE
                || lote.getFechaVencimiento() == null
                || lote.getFechaVencimiento().isBefore(hoy);
    }

    private void resolverAlerta(AlertaInventario alerta) {
        alerta.setEstado(EstadoAlerta.RESUELTA);
        alerta.setFechaResolucion(LocalDateTime.now());
        alertaRepository.save(alerta);
    }

    private TipoAlerta parseTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return null;
        }
        try {
            return TipoAlerta.valueOf(tipo.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private EstadoAlerta parseEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return null;
        }
        try {
            return EstadoAlerta.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Specification<AlertaInventario> porSucursal(Long idSucursal) {
        return (root, query, cb) -> idSucursal == null ? null
                : cb.equal(root.get("sucursal").get("idSucursal"), idSucursal);
    }

    private Specification<AlertaInventario> porTipo(TipoAlerta tipo) {
        return (root, query, cb) -> tipo == null ? null : cb.equal(root.get("tipo"), tipo);
    }

    private Specification<AlertaInventario> porEstado(EstadoAlerta estado) {
        return (root, query, cb) -> estado == null ? null : cb.equal(root.get("estado"), estado);
    }

    private Specification<AlertaInventario> porEstadoIn(List<EstadoAlerta> estados) {
        return (root, query, cb) -> estados == null || estados.isEmpty() ? null : root.get("estado").in(estados);
    }

    private Specification<AlertaInventario> porStock(Long idStock) {
        return (root, query, cb) -> idStock == null ? null
                : cb.equal(root.get("stockSucursal").get("idStock"), idStock);
    }

    private Specification<AlertaInventario> porLote(Long idLote) {
        return (root, query, cb) -> idLote == null ? null
                : cb.equal(root.get("loteInventario").get("idLote"), idLote);
    }
}
