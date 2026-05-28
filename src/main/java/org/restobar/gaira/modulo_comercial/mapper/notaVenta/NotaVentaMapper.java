package org.restobar.gaira.modulo_comercial.mapper.notaVenta;

import org.restobar.gaira.modulo_acceso.entity.Cliente;
import org.restobar.gaira.modulo_acceso.entity.Empleado;
import org.restobar.gaira.modulo_comercial.dto.detalleNotaVenta.DetalleNotaVentaResponse;
import org.restobar.gaira.modulo_comercial.dto.notaVenta.NotaVentaRequestDTO;
import org.restobar.gaira.modulo_comercial.dto.notaVenta.NotaVentaResponseDTO;
import org.restobar.gaira.modulo_comercial.entity.DetalleNotaVenta;
import org.restobar.gaira.modulo_comercial.entity.NotaVenta;
import org.restobar.gaira.modulo_comercial.mapper.detalleNotaVenta.DetalleNotaVentaMapper;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class NotaVentaMapper {

    private final DetalleNotaVentaMapper detalleNotaVentaMapper;

    public NotaVentaMapper(DetalleNotaVentaMapper detalleNotaVentaMapper) {
        this.detalleNotaVentaMapper = detalleNotaVentaMapper;
    }

    public NotaVenta toEntity(NotaVentaRequestDTO request, Cliente cliente, Empleado empleado, Sucursal sucursal,
                              List<DetalleNotaVenta> detalles) {
        if (request == null) return null;

        NotaVenta notaVenta = NotaVenta.builder()
                .cliente(cliente)
                .empleado(empleado)
                .sucursal(sucursal)
                .observaciones(request.getObservaciones() != null ? request.getObservaciones().trim() : null)
                .descuento(request.getDescuento() != null ? request.getDescuento() : BigDecimal.ZERO)
                .propina(request.getPropina() != null ? request.getPropina() : BigDecimal.ZERO)
                .nit(request.getNit() != null ? request.getNit().trim() : null)
                .build();

        if (detalles != null) {
            detalles.forEach(d -> d.setNotaVenta(notaVenta));
            notaVenta.setDetalles(detalles);
        }

        return notaVenta;
    }

    public NotaVentaResponseDTO toResponse(NotaVenta notaVenta) {
        if (notaVenta == null) return null;

        List<DetalleNotaVentaResponse> detallesResponse = Collections.emptyList();
        if (notaVenta.getDetalles() != null) {
            detallesResponse = notaVenta.getDetalles().stream()
                    .map(detalleNotaVentaMapper::toResponse)
                    .collect(Collectors.toList());
        }

        String nombreCliente = null;
        if (notaVenta.getCliente() != null && notaVenta.getCliente().getUsuario() != null) {
            nombreCliente = notaVenta.getCliente().getUsuario().getNombre()
                    + " " + notaVenta.getCliente().getUsuario().getApellido();
        }

        String nombreEmpleado = null;
        if (notaVenta.getEmpleado() != null && notaVenta.getEmpleado().getUsuario() != null) {
            nombreEmpleado = notaVenta.getEmpleado().getUsuario().getNombre()
                    + " " + notaVenta.getEmpleado().getUsuario().getApellido();
        }

        return NotaVentaResponseDTO.builder()
                .idNotaVenta(notaVenta.getIdNotaVenta())
                .idCliente(notaVenta.getCliente().getIdCliente())
                .nombreCliente(nombreCliente)
                .idEmpleado(notaVenta.getEmpleado().getIdEmpleado())
                .nombreEmpleado(nombreEmpleado)
                .idSucursal(notaVenta.getSucursal().getIdSucursal())
                .nombreSucursal(notaVenta.getSucursal().getNombre())
                .fechaEmision(notaVenta.getFechaEmision())
                .subTotal(notaVenta.getSubTotal())
                .descuento(notaVenta.getDescuento())
                .impuesto(notaVenta.getImpuesto())
                .propina(notaVenta.getPropina())
                .total(notaVenta.getTotal())
                .estado(notaVenta.getEstado())
                .observaciones(notaVenta.getObservaciones())
                .fechaPago(notaVenta.getFechaPago())
                .nit(notaVenta.getNit())
                .detalles(detallesResponse)
                .build();
    }

    public void updateEntityFromDto(NotaVenta notaVenta, NotaVentaRequestDTO request,
                                    Cliente cliente, Empleado empleado, Sucursal sucursal) {
        if (notaVenta == null || request == null) return;

        notaVenta.setCliente(cliente);
        notaVenta.setEmpleado(empleado);
        notaVenta.setSucursal(sucursal);
        notaVenta.setObservaciones(request.getObservaciones() != null ? request.getObservaciones().trim() : null);
        notaVenta.setDescuento(request.getDescuento() != null ? request.getDescuento() : BigDecimal.ZERO);
        notaVenta.setPropina(request.getPropina() != null ? request.getPropina() : BigDecimal.ZERO);
        notaVenta.setNit(request.getNit() != null ? request.getNit().trim() : null);
    }

    public Map<String, Object> toAuditMap(NotaVenta notaVenta) {
        if (notaVenta == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idNotaVenta", notaVenta.getIdNotaVenta());
        map.put("idCliente", notaVenta.getCliente().getIdCliente());
        map.put("idEmpleado", notaVenta.getEmpleado().getIdEmpleado());
        map.put("idSucursal", notaVenta.getSucursal().getIdSucursal());
        map.put("fechaEmision", notaVenta.getFechaEmision() != null ? notaVenta.getFechaEmision().toString() : null);
        map.put("subTotal", notaVenta.getSubTotal());
        map.put("descuento", notaVenta.getDescuento());
        map.put("impuesto", notaVenta.getImpuesto());
        map.put("total", notaVenta.getTotal());
        map.put("estado", notaVenta.getEstado() != null ? notaVenta.getEstado().name() : null);
        return map;
    }

    public Map<String, Object> toAuditMap(NotaVentaResponseDTO dto) {
        if (dto == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idNotaVenta", dto.getIdNotaVenta());
        map.put("idCliente", dto.getIdCliente());
        map.put("idEmpleado", dto.getIdEmpleado());
        map.put("idSucursal", dto.getIdSucursal());
        map.put("fechaEmision", dto.getFechaEmision() != null ? dto.getFechaEmision().toString() : null);
        map.put("subTotal", dto.getSubTotal());
        map.put("descuento", dto.getDescuento());
        map.put("impuesto", dto.getImpuesto());
        map.put("total", dto.getTotal());
        map.put("estado", dto.getEstado() != null ? dto.getEstado().name() : null);
        return map;
    }
}
