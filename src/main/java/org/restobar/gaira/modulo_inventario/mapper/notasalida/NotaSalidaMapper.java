package org.restobar.gaira.modulo_inventario.mapper.notasalida;

import org.restobar.gaira.modulo_inventario.dto.notasalida.NotaSalidaRequestDTO;
import org.restobar.gaira.modulo_inventario.dto.notasalida.NotaSalidaResponseDTO;
import org.restobar.gaira.modulo_inventario.entity.DetalleNotaSalida;
import org.restobar.gaira.modulo_inventario.entity.NotaSalida;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class NotaSalidaMapper {

    public NotaSalidaResponseDTO toResponseDTO(NotaSalida notaSalida) {
        if (notaSalida == null) return null;

        NotaSalidaResponseDTO dto = new NotaSalidaResponseDTO();
        dto.setIdNotaSalida(notaSalida.getIdNotaSalida());
        if (notaSalida.getSucursal() != null) {
            dto.setIdSucursal(notaSalida.getSucursal().getIdSucursal());
        }
        if (notaSalida.getEmpleado() != null) {
            dto.setIdEmpleado(notaSalida.getEmpleado().getIdEmpleado());
            dto.setNombreEmpleado(notaSalida.getEmpleado().getUsuario() != null ? notaSalida.getEmpleado().getUsuario().getNombre() : null);
        }
        dto.setTipoGasto(notaSalida.getTipoGasto());
        dto.setEstado(notaSalida.getEstado());
        dto.setMontoTotal(notaSalida.getMontoTotal());
        dto.setFecha(notaSalida.getFecha());
        dto.setDescripcion(notaSalida.getDescripcion());
        dto.setObservaciones(notaSalida.getObservaciones());

        if (notaSalida.getDetalles() != null) {
            dto.setDetalles(notaSalida.getDetalles().stream()
                    .map(this::toDetalleResponseDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public NotaSalidaResponseDTO.DetalleNotaSalidaResponseDTO toDetalleResponseDTO(DetalleNotaSalida detalle) {
        if (detalle == null) return null;

        NotaSalidaResponseDTO.DetalleNotaSalidaResponseDTO dto = new NotaSalidaResponseDTO.DetalleNotaSalidaResponseDTO();
        dto.setIdDetalle(detalle.getIdDetalle());
        dto.setDescripcion(detalle.getDescripcion());
        dto.setCantidad(detalle.getCantidad());
        dto.setMonto(detalle.getMonto());

        if (detalle.getStockSucursal() != null) {
            dto.setIdStockSucursal(detalle.getStockSucursal().getIdStock());
            if (detalle.getStockSucursal().getInventario() != null) {
                dto.setNombreInventario(detalle.getStockSucursal().getInventario().getNombre());
            }
        }

        return dto;
    }

    public NotaSalida toEntity(NotaSalidaRequestDTO request) {
        if (request == null) return null;

        NotaSalida notaSalida = new NotaSalida();
        notaSalida.setTipoGasto(request.getTipoGasto());
        notaSalida.setDescripcion(request.getDescripcion());
        notaSalida.setObservaciones(request.getObservaciones());

        return notaSalida;
    }
}
