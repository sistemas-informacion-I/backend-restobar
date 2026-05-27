package org.restobar.gaira.modulo_comercial.mapper.compra;

import org.restobar.gaira.modulo_acceso.entity.Empleado;
import org.restobar.gaira.modulo_comercial.dto.compra.CompraRequestDTO;
import org.restobar.gaira.modulo_comercial.dto.compra.CompraResponseDTO;
import org.restobar.gaira.modulo_comercial.dto.detalleCompra.DetalleCompraResponse;
import org.restobar.gaira.modulo_comercial.entity.Compra;
import org.restobar.gaira.modulo_comercial.entity.DetalleCompra;
import org.restobar.gaira.modulo_comercial.entity.Proveedor;
import org.restobar.gaira.modulo_comercial.mapper.detalleCompra.DetalleCompraMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CompraMapper {

    private final DetalleCompraMapper detalleCompraMapper;

    public CompraMapper(DetalleCompraMapper detalleCompraMapper) {
        this.detalleCompraMapper = detalleCompraMapper;
    }

    // Convierte un request + entidades resueltas a una nueva entidad Compra
    public Compra toEntity(CompraRequestDTO request, Proveedor proveedor, Empleado empleado,
                           Empleado creadoPor, List<DetalleCompra> detalles) {
        if (request == null) return null;

        Compra compra = Compra.builder()
                .proveedor(proveedor)
                .empleado(empleado)
                .nroFactura(request.getNroFactura().trim())
                .fechaCompra(request.getFechaCompra())
                .fechaEntregaProgramada(request.getFechaEntregaProgramada())
                .fechaLimitePago(request.getFechaLimitePago())
                .observaciones(request.getObservaciones() != null ? request.getObservaciones().trim() : null)
                .descuento(request.getDescuento() != null ? request.getDescuento() : BigDecimal.ZERO)
                .creadoPor(creadoPor)
                .build();

        if (detalles != null) {
            detalles.forEach(d -> d.setCompra(compra));
            compra.setDetalles(detalles);
        }

        return compra;
    }

    // Convierte una entidad Compra a su response, incluyendo los detalles y nombres de relaciones
    public CompraResponseDTO toResponse(Compra compra) {
        if (compra == null) return null;

        List<DetalleCompraResponse> detallesResponse = Collections.emptyList();
        if (compra.getDetalles() != null) {
            detallesResponse = compra.getDetalles().stream()
                    .map(detalleCompraMapper::toResponse)
                    .collect(Collectors.toList());
        }

        return CompraResponseDTO.builder()
                .idCompra(compra.getIdCompra())
                .idProveedor(compra.getProveedor().getId())
                .nombreProveedor(compra.getProveedor().getEmpresa())
                .idEmpleado(compra.getEmpleado().getIdEmpleado())
                .nombreEmpleado(compra.getEmpleado().getUsuario() != null
                        ? compra.getEmpleado().getUsuario().getNombre() + " " + compra.getEmpleado().getUsuario().getApellido()
                        : null)
                .nroFactura(compra.getNroFactura())
                .fechaCompra(compra.getFechaCompra())
                .fechaEntregaProgramada(compra.getFechaEntregaProgramada())
                .fechaEntregaReal(compra.getFechaEntregaReal())
                .subTotal(compra.getSubTotal())
                .descuento(compra.getDescuento())
                .impuesto(compra.getImpuesto())
                .total(compra.getTotal())
                .estadoPago(compra.getEstadoPago())
                .fechaLimitePago(compra.getFechaLimitePago())
                .fechaPago(compra.getFechaPago())
                .observaciones(compra.getObservaciones())
                .createdAt(compra.getCreatedAt())
                .updatedAt(compra.getUpdatedAt())
                .detalles(detallesResponse)
                .build();
    }

    // Actualiza los campos editables de una compra existente desde un request
    public void updateEntityFromDto(Compra compra, CompraRequestDTO request,
                                    Proveedor proveedor, Empleado empleado) {
        if (compra == null || request == null) return;

        compra.setProveedor(proveedor);
        compra.setEmpleado(empleado);
        compra.setNroFactura(request.getNroFactura().trim());
        compra.setFechaCompra(request.getFechaCompra());
        compra.setFechaEntregaProgramada(request.getFechaEntregaProgramada());
        compra.setFechaLimitePago(request.getFechaLimitePago());
        compra.setObservaciones(request.getObservaciones() != null ? request.getObservaciones().trim() : null);
        compra.setDescuento(request.getDescuento() != null ? request.getDescuento() : BigDecimal.ZERO);
    }

    // Construye un mapa con los datos de la compra para auditoría
    public Map<String, Object> toAuditMap(Compra compra) {
        if (compra == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idCompra", compra.getIdCompra());
        map.put("nroFactura", compra.getNroFactura());
        map.put("idProveedor", compra.getProveedor().getId());
        map.put("idEmpleado", compra.getEmpleado().getIdEmpleado());
        map.put("fechaCompra", compra.getFechaCompra().toString());
        map.put("subTotal", compra.getSubTotal());
        map.put("descuento", compra.getDescuento());
        map.put("impuesto", compra.getImpuesto());
        map.put("total", compra.getTotal());
        map.put("estadoPago", compra.getEstadoPago().name());
        return map;
    }

    // Construye un mapa desde el response DTO para auditoría
    public Map<String, Object> toAuditMap(CompraResponseDTO dto) {
        if (dto == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idCompra", dto.getIdCompra());
        map.put("nroFactura", dto.getNroFactura());
        map.put("idProveedor", dto.getIdProveedor());
        map.put("idEmpleado", dto.getIdEmpleado());
        map.put("fechaCompra", dto.getFechaCompra().toString());
        map.put("subTotal", dto.getSubTotal());
        map.put("descuento", dto.getDescuento());
        map.put("impuesto", dto.getImpuesto());
        map.put("total", dto.getTotal());
        map.put("estadoPago", dto.getEstadoPago().name());
        return map;
    }
}
