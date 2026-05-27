package org.restobar.gaira.modulo_comercial.service.compra;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.restobar.gaira.modulo_acceso.entity.Empleado;
import org.restobar.gaira.modulo_acceso.repository.EmpleadoRepository;
import org.restobar.gaira.modulo_comercial.dto.compra.CompraRequestDTO;
import org.restobar.gaira.modulo_comercial.dto.compra.CompraResponseDTO;
import org.restobar.gaira.modulo_comercial.dto.detalleCompra.DetalleCompraRequest;
import org.restobar.gaira.modulo_comercial.entity.Compra;
import org.restobar.gaira.modulo_comercial.entity.Compra.EstadoPago;
import org.restobar.gaira.modulo_comercial.entity.DetalleCompra;
import org.restobar.gaira.modulo_comercial.entity.Proveedor;
import org.restobar.gaira.modulo_comercial.mapper.compra.CompraMapper;
import org.restobar.gaira.modulo_comercial.mapper.detalleCompra.DetalleCompraMapper;
import org.restobar.gaira.modulo_comercial.repository.ProveedorRepository;
import org.restobar.gaira.modulo_comercial.repository.compra.CompraRepository;
import org.restobar.gaira.modulo_inventario.dto.stock.StockAjusteRequest;
import org.restobar.gaira.modulo_inventario.entity.StockSucursal;
import org.restobar.gaira.modulo_inventario.repository.StockSucursalRepository;
import org.restobar.gaira.modulo_inventario.service.stock.StockSucursalService;
import org.restobar.gaira.security.audit.annotation.Auditable;
import org.restobar.gaira.security.audit.util.AuditableService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CompraService implements AuditableService<Long, Object> {

    private final CompraRepository compraRepository;
    private final ProveedorRepository proveedorRepository;
    private final EmpleadoRepository empleadoRepository;
    private final StockSucursalRepository stockSucursalRepository;
    private final StockSucursalService stockSucursalService;
    private final CompraMapper compraMapper;
    private final DetalleCompraMapper detalleCompraMapper;

    // ─── AuditableService ────────────────────────────────────────────────────

    @Override
    public Object getEntity(Long id) {
        return compraRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof Compra c) {
            return compraMapper.toAuditMap(c);
        } else if (entity instanceof CompraResponseDTO dto) {
            return compraMapper.toAuditMap(dto);
        }
        return Map.of();
    }

    // ─── Consultas ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<CompraResponseDTO> findAll(String nroFactura, Long idProveedor,
                                           EstadoPago estadoPago, LocalDate fechaDesde,
                                           LocalDate fechaHasta) {
        return compraRepository.findAllByFechaDesc()
                .stream()
                .filter(c -> nroFactura == null || c.getNroFactura().toLowerCase().contains(nroFactura.toLowerCase()))
                .filter(c -> idProveedor == null || c.getProveedor().getId().equals(idProveedor))
                .filter(c -> estadoPago == null || c.getEstadoPago() == estadoPago)
                .filter(c -> fechaDesde == null || !c.getFechaCompra().isBefore(fechaDesde))
                .filter(c -> fechaHasta == null || !c.getFechaCompra().isAfter(fechaHasta))
                .map(compraMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CompraResponseDTO findById(Long id) {
        Compra compra = compraRepository.findByIdCompra(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra no encontrada"));
        return compraMapper.toResponse(compra);
    }

    @Transactional(readOnly = true)
    public List<CompraResponseDTO> findByProveedor(Long idProveedor) {
        return compraRepository.findByProveedor_Id(idProveedor)
                .stream()
                .map(compraMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CompraResponseDTO> findByEstadoPago(EstadoPago estadoPago) {
        return compraRepository.findByEstadoPago(estadoPago)
                .stream()
                .map(compraMapper::toResponse)
                .toList();
    }

    // ─── Escritura ────────────────────────────────────────────────────────────

    @Transactional
    @Auditable(tabla = "compra", operacion = "INSERT")
    public CompraResponseDTO create(CompraRequestDTO request, Long idUsuarioActual) {
        if (compraRepository.existsByNroFactura(request.getNroFactura())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una compra con ese número de factura");
        }

        Proveedor proveedor = proveedorRepository.findById(request.getIdProveedor())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado"));

        Empleado empleado = empleadoRepository.findById(request.getIdEmpleado())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado"));

        Empleado creadoPor = empleadoRepository.findByUsuario_IdUsuario(idUsuarioActual)
                .orElse(null);

        List<DetalleCompra> detalles = mapDetalles(request.getDetalles());

        Compra compra = compraMapper.toEntity(request, proveedor, empleado, creadoPor, detalles);

        BigDecimal subTotalCalc = detalles.stream()
                .map(DetalleCompra::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal impuestoCalc = subTotalCalc.multiply(new BigDecimal("0.13"));
        compra.setSubTotal(subTotalCalc);
        compra.setImpuesto(impuestoCalc);
        compra.setTotal(subTotalCalc.subtract(compra.getDescuento()).add(impuestoCalc));

        compra = compraRepository.save(compra);
        return compraMapper.toResponse(compra);
    }

    @Transactional
    @Auditable(tabla = "compra", operacion = "UPDATE", idParamName = "id")
    public CompraResponseDTO update(Long id, CompraRequestDTO request) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra no encontrada"));

        validarFacturaUnica(request.getNroFactura(), compra);

        Proveedor proveedor = proveedorRepository.findById(request.getIdProveedor())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado"));

        Empleado empleado = empleadoRepository.findById(request.getIdEmpleado())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado"));

        compraMapper.updateEntityFromDto(compra, request, proveedor, empleado);

        // Reemplazar detalles
        compra.getDetalles().clear();
        
        if (request.getDetalles() != null) {
            List<DetalleCompra> nuevosDetalles = mapDetalles(request.getDetalles());
            
            for(DetalleCompra d : nuevosDetalles ){
                d.setCompra(compra);
            }
            
            compra.getDetalles().addAll(nuevosDetalles);
        }

        BigDecimal subTotalCalc = compra.getDetalles().stream()
                .map(DetalleCompra::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal impuestoCalc = subTotalCalc.multiply(new BigDecimal("0.13"));
        compra.setSubTotal(subTotalCalc);
        compra.setImpuesto(impuestoCalc);
        compra.setDescuento(request.getDescuento() != null ? request.getDescuento() : BigDecimal.ZERO);
        compra.setTotal(subTotalCalc.subtract(compra.getDescuento()).add(impuestoCalc));

        compra = compraRepository.save(compra);
        return compraMapper.toResponse(compra);
    }

    @Transactional
    @Auditable(tabla = "compra", operacion = "DELETE", idParamName = "id")
    public void delete(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra no encontrada"));
        compraRepository.delete(compra);
    }

    @Transactional
    @Auditable(tabla = "compra", operacion = "UPDATE", idParamName = "id")
    public CompraResponseDTO cambiarEstadoPago(Long id, EstadoPago nuevoEstado) {
        Compra compra = compraRepository.findByIdCompra(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra no encontrada"));

        boolean fuePagado = compra.getEstadoPago() == EstadoPago.PAGADO;
        boolean ahoraPagado = nuevoEstado == EstadoPago.PAGADO;

        compra.setEstadoPago(nuevoEstado);
        compra = compraRepository.save(compra);

        if (!fuePagado && ahoraPagado && compra.getDetalles() != null) {
            for (DetalleCompra detalle : compra.getDetalles()) {
                StockAjusteRequest ajuste = new StockAjusteRequest();
                ajuste.setIdInventario(detalle.getStock().getInventario().getIdInventario());
                ajuste.setCantidad(BigDecimal.valueOf(detalle.getCantidad()));
                ajuste.setPrecioCompra(detalle.getPrecioUnitario());
                ajuste.setFechaIngreso(compra.getFechaCompra());
                stockSucursalService.ajustarStock(detalle.getStock().getIdStock(), ajuste);
            }
        }

        return compraMapper.toResponse(compra);
    }

    // ─── Métodos privados ─────────────────────────────────────────────────────

    private List<DetalleCompra> mapDetalles(List<DetalleCompraRequest> detallesRequest) {
        if (detallesRequest == null || detallesRequest.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La compra debe tener al menos un detalle");
        }

        List<DetalleCompra> detalles = new ArrayList<>();
        for (DetalleCompraRequest detReq : detallesRequest) {
            StockSucursal stock = stockSucursalRepository.findById(detReq.getIdStock())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Stock no encontrado con ID: " + detReq.getIdStock()));
            DetalleCompra detalle = detalleCompraMapper.toEntity(detReq, null, stock);
            detalles.add(detalle);
        }
        return detalles;
    }

    private void validarFacturaUnica(String nroFactura, Compra compraActual) {
        if (nroFactura != null && !nroFactura.isBlank()) {
            boolean facturaCambio = !nroFactura.equalsIgnoreCase(compraActual.getNroFactura());
            if (facturaCambio && compraRepository.existsByNroFactura(nroFactura)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una compra con ese número de factura");
            }
        }
    }
}
