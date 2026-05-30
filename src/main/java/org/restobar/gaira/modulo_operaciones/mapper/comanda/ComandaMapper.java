package org.restobar.gaira.modulo_operaciones.mapper.comanda;

import java.util.List;
import java.util.stream.Collectors;

import org.restobar.gaira.modulo_acceso.entity.Cliente;
import org.restobar.gaira.modulo_operaciones.dto.comanda.ComandaResponseDTO;
import org.restobar.gaira.modulo_operaciones.entity.Comanda;
import org.restobar.gaira.modulo_operaciones.entity.DetalleComanda;
import org.springframework.stereotype.Component;

@Component
public class ComandaMapper {

    public ComandaResponseDTO toResponseDTO(Comanda comanda, List<DetalleComanda> detalles) {
        return ComandaResponseDTO.builder()
                .idComanda(comanda.getIdComanda())
                .numeroComanda(comanda.getNumeroComanda())
                .idSucursal(comanda.getSucursal().getIdSucursal())
                .nombreSucursal(comanda.getSucursal().getNombre())
                .idCliente(comanda.getCliente() != null ? comanda.getCliente().getIdCliente() : null)
                .clienteNombre(buildClienteNombre(comanda.getCliente()))
                .idEmpleado(comanda.getEmpleado() != null ? comanda.getEmpleado().getIdEmpleado() : null)
                .empleadoNombre(buildEmpleadoNombre(comanda.getEmpleado()))
                .idMesa(comanda.getMesa() != null ? comanda.getMesa().getIdMesa() : null)
                .mesaNombre(comanda.getMesa() != null ? comanda.getMesa().getNumeroMesa() : null)
                .idSector(resolveIdSector(comanda))
                .nombreSector(resolveNombreSector(comanda))
                .tipoServicio(comanda.getTipoServicio())
                .estado(comanda.getEstado())
                .fechaApertura(comanda.getFechaApertura())
                .fechaCierre(comanda.getFechaCierre())
                .numeroPersonas(comanda.getNumeroPersonas())
                .observaciones(comanda.getObservaciones())
                .items(detalles.stream().map(this::toDetalleResponseDTO).collect(Collectors.toList()))
                .build();
    }

    public ComandaResponseDTO.DetalleComandaResponseDTO toDetalleResponseDTO(DetalleComanda detalle) {
        return ComandaResponseDTO.DetalleComandaResponseDTO.builder()
                .idDetalleComanda(detalle.getIdDetalleComanda())
                .idProductoFinal(detalle.getProductoFinal().getIdProductoFinal())
                .nombreProducto(detalle.getProductoFinal().getNombre())
                .precioUnitario(detalle.getPrecioUnitario())
                .cantidad(detalle.getCantidad())
                .notas(detalle.getNotas())
                .estado(detalle.getEstado())
                .estacionPreparacion(detalle.getEstacionPreparacion())
                .fechaAceptacion(detalle.getFechaAceptacion())
                .empleadoAsignado(detalle.getEmpleadoAsignado())
                .build();
    }

    private Long resolveIdSector(Comanda comanda) {
        if (comanda.getMesa() == null || comanda.getMesa().getSector() == null) {
            return null;
        }
        return comanda.getMesa().getSector().getIdSector();
    }

    private String resolveNombreSector(Comanda comanda) {
        if (comanda.getMesa() == null || comanda.getMesa().getSector() == null) {
            return null;
        }
        return comanda.getMesa().getSector().getNombre();
    }

    private String buildClienteNombre(Cliente cliente) {
        if (cliente == null || cliente.getUsuario() == null) {
            return null;
        }
        String nombre = cliente.getUsuario().getNombre();
        String apellido = cliente.getUsuario().getApellido();
        return (nombre != null ? nombre : "") + " " + (apellido != null ? apellido : "").trim();
    }

    private String buildEmpleadoNombre(org.restobar.gaira.modulo_acceso.entity.Empleado empleado) {
        if (empleado == null || empleado.getUsuario() == null) {
            return null;
        }
        String nombre = empleado.getUsuario().getNombre();
        String apellido = empleado.getUsuario().getApellido();
        return (nombre != null ? nombre : "") + " " + (apellido != null ? apellido : "").trim();
    }
}
