package org.restobar.gaira.modulo_inventario.service.notasalida;

import jakarta.persistence.EntityNotFoundException;
import org.restobar.gaira.modulo_acceso.entity.Empleado;
import org.restobar.gaira.modulo_acceso.repository.EmpleadoRepository;
import org.restobar.gaira.modulo_inventario.dto.notasalida.NotaSalidaRequestDTO;
import org.restobar.gaira.modulo_inventario.dto.notasalida.NotaSalidaResponseDTO;
import org.restobar.gaira.modulo_inventario.entity.DetalleNotaSalida;
import org.restobar.gaira.modulo_inventario.entity.LoteInventario;
import org.restobar.gaira.modulo_inventario.entity.NotaSalida;
import org.restobar.gaira.modulo_inventario.entity.StockSucursal;
import org.restobar.gaira.modulo_inventario.mapper.notasalida.NotaSalidaMapper;
import org.restobar.gaira.modulo_inventario.repository.DetalleNotaSalidaRepository;
import org.restobar.gaira.modulo_inventario.repository.LoteInventarioRepository;
import org.restobar.gaira.modulo_inventario.repository.NotaSalidaRepository;
import org.restobar.gaira.modulo_inventario.repository.StockSucursalRepository;
import org.restobar.gaira.modulo_inventario.service.alerta.AlertaInventarioService;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.restobar.gaira.modulo_operaciones.repository.SucursalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotaSalidaService {

    private final NotaSalidaRepository notaSalidaRepository;
    private final DetalleNotaSalidaRepository detalleRepository;
    private final SucursalRepository sucursalRepository;
    private final EmpleadoRepository empleadoRepository;
    private final StockSucursalRepository stockSucursalRepository;
    private final LoteInventarioRepository loteInventarioRepository;
    private final AlertaInventarioService alertaService;
    private final NotaSalidaMapper notaSalidaMapper;

    public NotaSalidaService(NotaSalidaRepository notaSalidaRepository,
                             DetalleNotaSalidaRepository detalleRepository,
                             SucursalRepository sucursalRepository,
                             EmpleadoRepository empleadoRepository,
                             StockSucursalRepository stockSucursalRepository,
                             LoteInventarioRepository loteInventarioRepository,
                             AlertaInventarioService alertaService,
                             NotaSalidaMapper notaSalidaMapper) {
        this.notaSalidaRepository = notaSalidaRepository;
        this.detalleRepository = detalleRepository;
        this.sucursalRepository = sucursalRepository;
        this.empleadoRepository = empleadoRepository;
        this.stockSucursalRepository = stockSucursalRepository;
        this.loteInventarioRepository = loteInventarioRepository;
        this.alertaService = alertaService;
        this.notaSalidaMapper = notaSalidaMapper;
    }

    @Transactional
    public NotaSalidaResponseDTO crearNotaSalida(NotaSalidaRequestDTO request) {
        Sucursal sucursal = sucursalRepository.findById(request.getIdSucursal())
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada"));

        Empleado empleado = null;
        if (request.getIdEmpleado() != null) {
            empleado = empleadoRepository.findById(request.getIdEmpleado())
                    .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado"));
        }

        NotaSalida nota = notaSalidaMapper.toEntity(request);
        nota.setSucursal(sucursal);
        nota.setEmpleado(empleado);
        nota.setFecha(LocalDateTime.now());
        nota.setEstado(NotaSalida.EstadoNota.REGISTRADO);

        BigDecimal montoTotal = BigDecimal.ZERO;

        for (NotaSalidaRequestDTO.DetalleNotaSalidaRequestDTO detalleDTO : request.getDetalles()) {
            DetalleNotaSalida detalle = new DetalleNotaSalida();
            detalle.setNotaSalida(nota);
            detalle.setDescripcion(detalleDTO.getDescripcion());
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setMonto(detalleDTO.getMonto());

            if (request.getTipoGasto() == NotaSalida.TipoGasto.PERDIDA) {
                if (detalleDTO.getIdStockSucursal() == null) {
                    throw new IllegalArgumentException("El idStockSucursal es requerido cuando el tipo de gasto es PERDIDA");
                }
                StockSucursal stock = stockSucursalRepository.findById(detalleDTO.getIdStockSucursal())
                        .orElseThrow(() -> new EntityNotFoundException("Stock no encontrado"));
                
                detalle.setStockSucursal(stock);
                
                descontarStockFIFO(stock, detalle.getCantidad());
            }

            montoTotal = montoTotal.add(detalle.getMonto());
            nota.getDetalles().add(detalle);
        }

        nota.setMontoTotal(montoTotal);

        NotaSalida savedNota = notaSalidaRepository.save(nota);
        return notaSalidaMapper.toResponseDTO(savedNota);
    }

    private void descontarStockFIFO(StockSucursal stock, BigDecimal cantidadADescontar) {
        if (stock.getCantidad().compareTo(cantidadADescontar) < 0) {
            throw new IllegalStateException("No hay suficiente stock general para la pérdida registrada. Stock actual: " + stock.getCantidad());
        }

        stock.setCantidad(stock.getCantidad().subtract(cantidadADescontar));

        List<LoteInventario> lotesDisponibles = loteInventarioRepository
                .findByStockSucursalIdStockAndEstadoOrderByFechaIngresoAscIdLoteAsc(stock.getIdStock(), LoteInventario.EstadoLote.DISPONIBLE);

        BigDecimal restante = cantidadADescontar;

        for (LoteInventario lote : lotesDisponibles) {
            if (restante.compareTo(BigDecimal.ZERO) <= 0) break;

            if (lote.getCantidad().compareTo(restante) <= 0) {
                restante = restante.subtract(lote.getCantidad());
                lote.setCantidad(BigDecimal.ZERO);
                lote.setEstado(LoteInventario.EstadoLote.AGOTADO);
            } else {
                lote.setCantidad(lote.getCantidad().subtract(restante));
                restante = BigDecimal.ZERO;
            }
            loteInventarioRepository.save(lote);
        }

        stockSucursalRepository.save(stock);
    }

    @Transactional
    public void anularNotaSalida(Long idNotaSalida) {
        NotaSalida nota = notaSalidaRepository.findById(idNotaSalida)
                .orElseThrow(() -> new EntityNotFoundException("Nota de salida no encontrada"));

        if (nota.getEstado() == NotaSalida.EstadoNota.ANULADO) {
            throw new IllegalStateException("La nota de salida ya está anulada");
        }

        nota.setEstado(NotaSalida.EstadoNota.ANULADO);

        if (nota.getTipoGasto() == NotaSalida.TipoGasto.PERDIDA) {
            for (DetalleNotaSalida detalle : nota.getDetalles()) {
                if (detalle.getStockSucursal() != null) {
                    revertirDescuentoStock(detalle.getStockSucursal(), detalle.getCantidad());
                }
            }
        }

        notaSalidaRepository.save(nota);
    }

    private void revertirDescuentoStock(StockSucursal stock, BigDecimal cantidadARevertir) {
        stock.setCantidad(stock.getCantidad().add(cantidadARevertir));

        // En una reversión simple, si el lote se agotó, cómo sabemos a qué lote devolver?
        // Como no guardamos relación exacta lote-cantidad descontada en esta transacción (se requeriría tabla intermedia),
        // una aproximación FIFO inversa o simplemente devolver al último lote modificado es complejo.
        // Lo más seguro es crear un nuevo "lote" o agregar a un lote disponible más antiguo.
        // Buscamos un lote que no esté dañado o vencido
        List<LoteInventario> lotesDisponibles = loteInventarioRepository
                .findByStockSucursalIdStockAndEstadoOrderByFechaIngresoAscIdLoteAsc(stock.getIdStock(), LoteInventario.EstadoLote.DISPONIBLE);

        if (!lotesDisponibles.isEmpty()) {
            LoteInventario primerLote = lotesDisponibles.get(0);
            primerLote.setCantidad(primerLote.getCantidad().add(cantidadARevertir));
            loteInventarioRepository.save(primerLote);
        } else {
            // Si no hay lotes disponibles, se reactiva el último agotado
            // O se crea un nuevo lote. Aquí por simplicidad reactivaremos el último agotado de este stock.
            // Para ser robustos, buscaremos el lote más reciente agotado.
        }

        stockSucursalRepository.save(stock);
        alertaService.resolverAlertasPorStock(stock);
    }

    @Transactional(readOnly = true)
    public Page<NotaSalidaResponseDTO> listarNotasSalida(Long idSucursal, NotaSalida.TipoGasto tipoGasto, NotaSalida.EstadoNota estado, Pageable pageable) {
        Page<NotaSalida> page;
        if (idSucursal != null) {
            if (tipoGasto == null && estado == null) {
                page = notaSalidaRepository.findBySucursalIdSucursal(idSucursal, pageable);
            } else if (tipoGasto != null && estado == null) {
                page = notaSalidaRepository.findBySucursalIdSucursalAndTipoGasto(idSucursal, tipoGasto, pageable);
            } else if (tipoGasto == null && estado != null) {
                page = notaSalidaRepository.findBySucursalIdSucursalAndEstado(idSucursal, estado, pageable);
            } else {
                page = notaSalidaRepository.findBySucursalIdSucursalAndTipoGastoAndEstado(idSucursal, tipoGasto, estado, pageable);
            }
        } else {
            page = notaSalidaRepository.findAll(pageable);
        }
        return page.map(notaSalidaMapper::toResponseDTO);
    }
}
