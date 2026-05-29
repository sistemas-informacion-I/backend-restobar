package org.restobar.gaira.modulo_comercial.service.notaVenta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.restobar.gaira.modulo_acceso.entity.Cliente;
import org.restobar.gaira.modulo_acceso.entity.Empleado;
import org.restobar.gaira.modulo_acceso.repository.ClienteRepository;
import org.restobar.gaira.modulo_acceso.repository.EmpleadoRepository;
import org.restobar.gaira.modulo_comercial.dto.detalleNotaVenta.DetalleNotaVentaRequest;
import org.restobar.gaira.modulo_comercial.dto.notaVenta.NotaVentaRequestDTO;
import org.restobar.gaira.modulo_comercial.dto.notaVenta.NotaVentaResponseDTO;
import org.restobar.gaira.modulo_comercial.entity.DetalleNotaVenta;
import org.restobar.gaira.modulo_comercial.entity.NotaVenta;
import org.restobar.gaira.modulo_comercial.entity.NotaVenta.Estado;
import org.restobar.gaira.modulo_comercial.entity.ProductoFinal;
import org.restobar.gaira.modulo_comercial.mapper.detalleNotaVenta.DetalleNotaVentaMapper;
import org.restobar.gaira.modulo_comercial.mapper.notaVenta.NotaVentaMapper;
import org.restobar.gaira.modulo_comercial.repository.detalleNotaVenta.DetalleNotaVentaRepository;
import org.restobar.gaira.modulo_comercial.repository.ProductoFinalRepository;
import org.restobar.gaira.modulo_comercial.repository.notaVenta.NotaVentaRepository;
import org.restobar.gaira.modulo_electronico.entity.MetodoPago;
import org.restobar.gaira.modulo_electronico.repository.MetodoPagoRepository;
import org.restobar.gaira.modulo_operaciones.entity.Comanda;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.restobar.gaira.modulo_operaciones.repository.SucursalRepository;
import org.restobar.gaira.security.audit.annotation.Auditable;
import org.restobar.gaira.security.audit.util.AuditableService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class NotaVentaService implements AuditableService<Long, Object> {

    private final NotaVentaRepository notaVentaRepository;
    private final ClienteRepository clienteRepository;
    private final EmpleadoRepository empleadoRepository;
    private final SucursalRepository sucursalRepository;
    private final ProductoFinalRepository productoFinalRepository;
    private final NotaVentaMapper notaVentaMapper;
    private final DetalleNotaVentaMapper detalleNotaVentaMapper;
    private final DetalleNotaVentaRepository detalleNotaVentaRepository;
    private final MetodoPagoRepository metodoPagoRepository;

    @Override
    public Object getEntity(Long id) {
        return notaVentaRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof NotaVenta nv) {
            return notaVentaMapper.toAuditMap(nv);
        } else if (entity instanceof NotaVentaResponseDTO dto) {
            return notaVentaMapper.toAuditMap(dto);
        }
        return Map.of();
    }

    @Transactional(readOnly = true)
    public List<NotaVentaResponseDTO> findAll(Long idCliente, Long idSucursal,
                                              Estado estado, LocalDate fechaDesde,
                                              LocalDate fechaHasta) {
        return notaVentaRepository.findAllByFechaDesc()
                .stream()
                .filter(n -> idCliente == null || n.getCliente().getIdCliente().equals(idCliente))
                .filter(n -> idSucursal == null || n.getSucursal().getIdSucursal().equals(idSucursal))
                .filter(n -> estado == null || n.getEstado() == estado)
                .filter(n -> fechaDesde == null || (n.getFechaEmision() != null && !n.getFechaEmision().toLocalDate().isBefore(fechaDesde)))
                .filter(n -> fechaHasta == null || (n.getFechaEmision() != null && !n.getFechaEmision().toLocalDate().isAfter(fechaHasta)))
                .map(notaVentaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public NotaVentaResponseDTO findById(Long id) {
        NotaVenta notaVenta = notaVentaRepository.findByIdNotaVenta(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nota de venta no encontrada"));
        return notaVentaMapper.toResponse(notaVenta);
    }

    @Transactional(readOnly = true)
    public List<NotaVentaResponseDTO> findByCliente(Long idCliente) {
        return notaVentaRepository.findByCliente_IdCliente(idCliente)
                .stream()
                .map(notaVentaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotaVentaResponseDTO> findByEstado(Estado estado) {
        return notaVentaRepository.findByEstado(estado)
                .stream()
                .map(notaVentaMapper::toResponse)
                .toList();
    }

    @Transactional
    @Auditable(tabla = "nota_venta", operacion = "INSERT")
    public NotaVentaResponseDTO create(NotaVentaRequestDTO request) {
        Cliente cliente = clienteRepository.findById(request.getIdCliente())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        Empleado empleado = empleadoRepository.findById(request.getIdEmpleado())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado"));

        Sucursal sucursal = sucursalRepository.findById(request.getIdSucursal())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada"));

        List<DetalleNotaVenta> detalles = mapDetalles(request.getDetalles());

        NotaVenta notaVenta = notaVentaMapper.toEntity(request, cliente, empleado, sucursal, detalles);

        BigDecimal subTotalCalc = detalles.stream()
                .map(DetalleNotaVenta::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal impuestoCalc = subTotalCalc.multiply(new BigDecimal("0.13"));
        notaVenta.setSubTotal(subTotalCalc);
        notaVenta.setImpuesto(impuestoCalc);
        notaVenta.setTotal(subTotalCalc.subtract(notaVenta.getDescuento()).add(impuestoCalc).add(notaVenta.getPropina()));
        notaVenta.setFechaEmision(LocalDateTime.now());

        notaVenta = notaVentaRepository.save(notaVenta);
        return notaVentaMapper.toResponse(notaVenta);
    }

    @Transactional
    @Auditable(tabla = "nota_venta", operacion = "UPDATE", idParamName = "id")
    public NotaVentaResponseDTO update(Long id, NotaVentaRequestDTO request) {
        NotaVenta notaVenta = notaVentaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nota de venta no encontrada"));

        if (notaVenta.getEstado() == Estado.ANULADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede modificar una nota de venta anulada");
        }

        Cliente cliente = clienteRepository.findById(request.getIdCliente())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        Empleado empleado = empleadoRepository.findById(request.getIdEmpleado())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado"));

        Sucursal sucursal = sucursalRepository.findById(request.getIdSucursal())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada"));

        notaVentaMapper.updateEntityFromDto(notaVenta, request, cliente, empleado, sucursal);

        notaVenta.getDetalles().clear();

        if (request.getDetalles() != null) {
            List<DetalleNotaVenta> nuevosDetalles = mapDetalles(request.getDetalles());
            for (DetalleNotaVenta d : nuevosDetalles) {
                d.setNotaVenta(notaVenta);
            }
            notaVenta.getDetalles().addAll(nuevosDetalles);
        }

        BigDecimal subTotalCalc = notaVenta.getDetalles().stream()
                .map(DetalleNotaVenta::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal impuestoCalc = subTotalCalc.multiply(new BigDecimal("0.13"));
        notaVenta.setSubTotal(subTotalCalc);
        notaVenta.setImpuesto(impuestoCalc);
        notaVenta.setDescuento(request.getDescuento() != null ? request.getDescuento() : BigDecimal.ZERO);
        notaVenta.setPropina(request.getPropina() != null ? request.getPropina() : BigDecimal.ZERO);
        notaVenta.setTotal(subTotalCalc.subtract(notaVenta.getDescuento()).add(impuestoCalc).add(notaVenta.getPropina()));

        notaVenta = notaVentaRepository.save(notaVenta);
        return notaVentaMapper.toResponse(notaVenta);
    }

    @Transactional
    @Auditable(tabla = "nota_venta", operacion = "DELETE", idParamName = "id")
    public void delete(Long id) {
        NotaVenta notaVenta = notaVentaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nota de venta no encontrada"));

        if (notaVenta.getEstado() == Estado.PAGADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede eliminar una nota de venta pagada");
        }

        notaVentaRepository.delete(notaVenta);
    }

    @Transactional
    @Auditable(tabla = "nota_venta", operacion = "UPDATE", idParamName = "id")
    public NotaVentaResponseDTO cambiarEstado(Long id, Estado nuevoEstado) {
        NotaVenta notaVenta = notaVentaRepository.findByIdNotaVenta(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nota de venta no encontrada"));

        if (notaVenta.getEstado() == Estado.ANULADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede cambiar el estado de una nota anulada");
        }

        notaVenta.setEstado(nuevoEstado);
        if (nuevoEstado == Estado.PAGADA) {
            notaVenta.setFechaPago(LocalDateTime.now());
        }

        notaVenta = notaVentaRepository.save(notaVenta);
        return notaVentaMapper.toResponse(notaVenta);
    }

    /**
     * Obtiene los pedidos del cliente autenticado para la funcionalidad de pagos en l&iacute;nea.
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerMisPedidos(String username) {
        List<NotaVenta> notasVenta = notaVentaRepository.findByClienteUsername(username);
        return notasVenta.stream()
                .map(notaVentaMapper::toResponseMap)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una nota de venta por ID con datos de pago para la funcionalidad de pagos en l&iacute;nea.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerPorId(Long id) {
        NotaVenta notaVenta = notaVentaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Nota de venta no encontrada"));
        return notaVentaMapper.toResponseMap(notaVenta);
    }

    /**
     * Obtiene una nota de venta por ID validando que pertenezca al cliente autenticado.
     * Usa el username del principal para validar pertenencia (sin requerir permisos ventas:*).
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerMiNota(String username, Long idNotaVenta) {
        NotaVenta notaVenta = notaVentaRepository.findById(idNotaVenta)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Nota de venta no encontrada"));

        if (notaVenta.getCliente() == null
                || notaVenta.getCliente().getUsuario() == null
                || !notaVenta.getCliente().getUsuario().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tienes permiso para ver esta nota");
        }

        return notaVentaMapper.toResponseMap(notaVenta);
    }

    /**
     * Crea una NotaVenta desde una comanda para la funcionalidad de pagos en l&iacute;nea.
     */
    @Transactional
    public NotaVenta crearDesdeComanda(
            Comanda comanda,
            Long idSucursal,
            Long idMetodoPago,
            BigDecimal subTotal,
            BigDecimal descuento,
            BigDecimal impuesto,
            BigDecimal total,
            List<ItemData> items) {

        MetodoPago metodoPago = metodoPagoRepository.findById(idMetodoPago)
                .orElseThrow(() -> new IllegalArgumentException("Método de pago no encontrado: " + idMetodoPago));

        Sucursal sucursal = sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> new IllegalArgumentException("Sucursal no encontrada: " + idSucursal));

        NotaVenta notaVenta = NotaVenta.builder()
                .comanda(comanda)
                .sucursal(sucursal)
                .cliente(comanda.getCliente())
                .metodoPago(metodoPago)
                .subTotal(subTotal)
                .descuento(descuento)
                .impuesto(impuesto)
                .propina(BigDecimal.ZERO)
                .total(total)
                .estado(NotaVenta.Estado.EMITIDA)
                .build();

        notaVenta = notaVentaRepository.save(notaVenta);

        for (ItemData item : items) {
            ProductoFinal producto = productoFinalRepository.findById(item.idProductoFinal())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + item.idProductoFinal()));

            BigDecimal costoUnitario = obtenerCostoUnitario(item.idProductoFinal(), idSucursal);
            BigDecimal itemSubtotal = item.precioUnitario().multiply(BigDecimal.valueOf(item.cantidad()));

            DetalleNotaVenta detalle = DetalleNotaVenta.builder()
                    .notaVenta(notaVenta)
                    .productoFinal(producto)
                    .cantidad(item.cantidad())
                    .precioU(item.precioUnitario())
                    .costoU(costoUnitario)
                    .descuento(BigDecimal.ZERO)
                    .subTotal(itemSubtotal)
                    .descripcion(item.notas())
                    .build();

            detalleNotaVentaRepository.save(detalle);
        }

        log.info("NotaVenta {} creada desde comanda {} con {} items",
                notaVenta.getIdNotaVenta(), comanda.getNumeroComanda(), items.size());
        return notaVenta;
    }

    private BigDecimal obtenerCostoUnitario(Long idProductoFinal, Long idSucursal) {
        return BigDecimal.ZERO;
    }

    public record ItemData(Long idProductoFinal, Integer cantidad, BigDecimal precioUnitario, String notas) {}

    private List<DetalleNotaVenta> mapDetalles(List<DetalleNotaVentaRequest> detallesRequest) {
        if (detallesRequest == null || detallesRequest.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La nota de venta debe tener al menos un detalle");
        }

        List<DetalleNotaVenta> detalles = new ArrayList<>();
        for (DetalleNotaVentaRequest detReq : detallesRequest) {
            ProductoFinal producto = productoFinalRepository.findById(detReq.getIdProductoFinal())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Producto final no encontrado con ID: " + detReq.getIdProductoFinal()));
            DetalleNotaVenta detalle = detalleNotaVentaMapper.toEntity(detReq, null, producto);
            detalles.add(detalle);
        }
        return detalles;
    }
}