package org.restobar.gaira.modulo_inventario.service.stock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.restobar.gaira.modulo_inventario.dto.stock.StockSucursalRequest;
import org.restobar.gaira.modulo_inventario.dto.stock.StockSucursalResponse;
import org.restobar.gaira.modulo_inventario.dto.stock.StockAjusteRequest;
import org.restobar.gaira.modulo_inventario.entity.LoteInventario.EstadoLote;
import org.restobar.gaira.modulo_inventario.entity.Inventario;
import org.restobar.gaira.modulo_inventario.entity.LoteInventario;
import org.restobar.gaira.modulo_inventario.entity.StockSucursal;
import org.restobar.gaira.modulo_inventario.mapper.lote.LoteMapper;
import org.restobar.gaira.modulo_inventario.mapper.stock.StockSucursalMapper;
import org.restobar.gaira.modulo_inventario.repository.InventarioRepository;
import org.restobar.gaira.modulo_inventario.repository.LoteInventarioRepository;
import org.restobar.gaira.modulo_inventario.repository.StockSucursalRepository;
import org.restobar.gaira.modulo_inventario.service.alerta.AlertaInventarioService;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.restobar.gaira.modulo_operaciones.repository.SucursalRepository;
import org.restobar.gaira.security.audit.annotation.Auditable;
import org.restobar.gaira.security.audit.util.AuditableService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import jakarta.persistence.OptimisticLockException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockSucursalService implements AuditableService<Long, Object> {

    private final StockSucursalRepository stockRepository;
    private final InventarioRepository inventarioRepository;
    private final SucursalRepository sucursalRepository;
    private final LoteInventarioRepository loteRepository;
    private final AlertaInventarioService alertaService;
    private final StockSucursalMapper stockMapper;
    private final LoteMapper loteMapper;

    @Override
    public Object getEntity(Long id) {
        return stockRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof StockSucursal s) {
            return stockMapper.toAuditMap(s);
        } else if (entity instanceof StockSucursalResponse sr) {
            return stockMapper.toAuditMap(sr);
        }
        return Map.of();
    }

    @Transactional(readOnly = true)
    public List<StockSucursalResponse> listarPorSucursal(Long idSucursal) {
        return stockRepository.findBySucursalIdSucursal(idSucursal).stream()
                .map(stockMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Auditable(tabla = "stock_sucursal", operacion = "INSERT")
    public StockSucursalResponse establecerStockInicial(StockSucursalRequest dto) {
        stockRepository.findByInventarioIdInventarioAndSucursalIdSucursal(dto.getIdInventario(), dto.getIdSucursal())
                .ifPresent(s -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Ya existe stock para este insumo en esta sucursal");
                });

        Inventario inventario = inventarioRepository.findById(dto.getIdInventario())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Insumo no encontrado"));

        Sucursal sucursal = sucursalRepository.findById(dto.getIdSucursal())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada"));

        StockSucursal stock = stockMapper.toEntity(dto, inventario, sucursal);
        StockSucursal savedStock = stockRepository.save(stock);
        alertaService.generarAlertaStockMinimoParaStock(savedStock);
        return stockMapper.toResponse(savedStock);
    }

    @Transactional
    @Auditable(tabla = "stock_sucursal", operacion = "UPDATE", idParamName = "idStock")
    // Ajusta el stock por producto: compra crea lote nuevo (cantidad positiva),
    // salida consume lotes existentes (cantidad negativa).
    public StockSucursalResponse ajustarStock(Long idStock, StockAjusteRequest dto) {
        if (dto == null || dto.getIdInventario() == null || dto.getCantidad() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idInventario y cantidad son obligatorios");
        }

        if (dto.getCantidad().compareTo(BigDecimal.ZERO) == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad no puede ser cero");
        }

        if (dto.getCantidad().compareTo(BigDecimal.ZERO) > 0 && dto.getIdLote() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Para compras no se debe enviar idLote; el ajuste creará un lote nuevo");
        }

        int attempts = 0;
        int maxAttempts = 3;

        while (true) {
            try {
                StockSucursal stock = stockRepository.findById(idStock)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Registro de stock no encontrado"));

                if (stock.getInventario() == null
                        || !stock.getInventario().getIdInventario().equals(dto.getIdInventario())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "El stock no corresponde al producto indicado");
                }

                if (dto.getCantidad().compareTo(BigDecimal.ZERO) > 0) {
                    // Compra: siempre se registra como un lote nuevo para conservar trazabilidad.
                    crearLoteNuevoPorCompra(stock, dto);
                } else {
                    BigDecimal cantidadAbs = dto.getCantidad().abs();
                    if (dto.getIdLote() != null) {
                        // Si viene lote, la salida se aplica contra ese lote específico.
                        consumirDeLoteEspecifico(stock, dto.getIdLote(), cantidadAbs);
                    } else {
                        // Si no viene lote, se descuenta por FIFO: primero el lote más antiguo
                        // disponible.
                        consumirPorFIFO(stock, cantidadAbs);
                    }
                }

                // Después de mover lotes, recalculamos el stock total y precios desde los lotes
                // activos.
                stock = recalcularStockDesdeLotes(stock.getIdStock());
                alertaService.resolverAlertasPorStock(stock);
                alertaService.generarAlertaStockMinimoParaStock(stock);
                return stockMapper.toResponse(stock);

            } catch (OptimisticLockException | ObjectOptimisticLockingFailureException e) {
                attempts++;
                if (attempts >= maxAttempts) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "No se pudo ajustar el stock por conflictos de concurrencia. Reintente.");
                }
                // Reintenta automáticamente en el siguiente ciclo del while
            }
        }
    }

    // Crea un lote nuevo cuando la cantidad representa una compra.
    private void crearLoteNuevoPorCompra(StockSucursal stock, StockAjusteRequest dto) {
        if (dto.getPrecioCompra() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "precioCompra es obligatorio para compras");
        }

        if (dto.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La compra debe tener cantidad positiva");
        }

        LoteInventario lote = loteMapper.toEntity(dto, stock);

        loteRepository.save(lote);
    }

    // Descuenta una salida contra un lote concreto si el caller lo especifica.
    private void consumirDeLoteEspecifico(StockSucursal stock, Long idLote, BigDecimal cantidad) {
        LoteInventario lote = obtenerLoteDisponibleDelStock(stock.getIdStock(), idLote);

        if (lote.getCantidad().compareTo(cantidad) < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La cantidad supera el disponible del lote indicado");
        }

        lote.setCantidad(lote.getCantidad().subtract(cantidad));
        if (lote.getCantidad().compareTo(BigDecimal.ZERO) == 0) {
            lote.setEstado(EstadoLote.AGOTADO);
        }
        loteRepository.save(lote);
    }

    // Descuenta una salida usando FIFO sobre los lotes más antiguos disponibles.
    private void consumirPorFIFO(StockSucursal stock, BigDecimal cantidad) {
        List<LoteInventario> lotesDisponibles = loteRepository
                .findByStockSucursalIdStockAndEstadoOrderByFechaIngresoAscIdLoteAsc(stock.getIdStock(),
                        EstadoLote.DISPONIBLE);

        BigDecimal totalDisponible = lotesDisponibles.stream()
                .map(LoteInventario::getCantidad)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalDisponible.compareTo(cantidad) < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No hay stock suficiente en lotes disponibles para aplicar el ajuste");
        }

        BigDecimal restante = cantidad;
        for (LoteInventario lote : new ArrayList<>(lotesDisponibles)) {
            if (restante.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal aConsumir = lote.getCantidad().min(restante);
            lote.setCantidad(lote.getCantidad().subtract(aConsumir));
            if (lote.getCantidad().compareTo(BigDecimal.ZERO) == 0) {
                lote.setEstado(EstadoLote.AGOTADO);
            }
            loteRepository.save(lote);
            restante = restante.subtract(aConsumir);
        }
    }

    // Valida que el lote exista y pertenezca al stock indicado.
    private LoteInventario obtenerLoteDelStock(Long idStock, Long idLote) {
        if (idLote == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe especificarse un lote");
        }

        LoteInventario lote = loteRepository.findById(idLote)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote no encontrado"));

        if (lote.getStockSucursal() == null || !lote.getStockSucursal().getIdStock().equals(idStock)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El lote no pertenece al stock indicado");
        }

        return lote;
    }

    // Exige que el lote esté disponible antes de aplicarle una salida.
    private LoteInventario obtenerLoteDisponibleDelStock(Long idStock, Long idLote) {
        LoteInventario lote = obtenerLoteDelStock(idStock, idLote);
        if (lote.getEstado() != EstadoLote.DISPONIBLE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El lote no está disponible");
        }
        return lote;
    }

    // Recalcula el stock total y precios a partir de los lotes que siguen
    // disponibles.
    @Transactional
    public StockSucursal recalcularStockDesdeLotes(Long idStock) {
        StockSucursal stock = stockRepository.findById(idStock)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Registro de stock no encontrado"));

        List<LoteInventario> lotesDisponibles = loteRepository
                .findByStockSucursalIdStockAndEstadoOrderByFechaIngresoAscIdLoteAsc(idStock, EstadoLote.DISPONIBLE);

        BigDecimal cantidadTotal = BigDecimal.ZERO;
        BigDecimal valorTotal = BigDecimal.ZERO;
        BigDecimal ultimoPrecio = BigDecimal.ZERO;

        for (LoteInventario lote : lotesDisponibles) {
            cantidadTotal = cantidadTotal.add(lote.getCantidad());
            valorTotal = valorTotal.add(lote.getCantidad().multiply(lote.getPrecioCompra()));
            ultimoPrecio = lote.getPrecioCompra();
        }

        stock.setCantidad(cantidadTotal);
        stock.setPrecioUnitario(ultimoPrecio);

        if (cantidadTotal.compareTo(BigDecimal.ZERO) > 0) {
            stock.setPrecioPromedio(valorTotal.divide(cantidadTotal, 4, RoundingMode.HALF_UP));
        } else {
            stock.setPrecioPromedio(BigDecimal.ZERO);
        }

        return stockRepository.save(stock);
    }
}
