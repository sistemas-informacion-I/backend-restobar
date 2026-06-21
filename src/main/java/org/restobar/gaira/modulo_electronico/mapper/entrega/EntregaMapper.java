package org.restobar.gaira.modulo_electronico.mapper.entrega;

import org.restobar.gaira.modulo_acceso.entity.Cliente;
import org.restobar.gaira.modulo_acceso.entity.Empleado;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_acceso.repository.EmpleadoRepository;
import org.restobar.gaira.modulo_acceso.repository.UsuarioRepository;
import org.restobar.gaira.modulo_electronico.dto.entrega.EntregaResponse;
import org.restobar.gaira.modulo_electronico.entity.Entrega;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.springframework.stereotype.Component;

@Component
public class EntregaMapper {

    private final EmpleadoRepository empleadoRepository;
    private final UsuarioRepository usuarioRepository;

    public EntregaMapper(EmpleadoRepository empleadoRepository, UsuarioRepository usuarioRepository) {
        this.empleadoRepository = empleadoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public EntregaResponse toResponse(Entrega entrega) {
        if (entrega == null) return null;

        Sucursal sucursal = entrega.getComanda() != null ? entrega.getComanda().getSucursal() : null;
        Cliente cliente = entrega.getComanda() != null ? entrega.getComanda().getCliente() : null;

        String nombreEmpleado = null;
        Long idEmpleado = null;

        if (entrega.getIdUsuarioRepartidor() != null) {
            Empleado empleado = empleadoRepository.findByUsuario_IdUsuario(entrega.getIdUsuarioRepartidor()).orElse(null);
            if (empleado != null) {
                idEmpleado = empleado.getIdEmpleado();
                Usuario u = empleado.getUsuario();
                nombreEmpleado = u.getNombre() + " " + u.getApellido();
            } else {
                Usuario u = usuarioRepository.findById(entrega.getIdUsuarioRepartidor()).orElse(null);
                if (u != null) {
                    nombreEmpleado = u.getNombre() + " " + u.getApellido();
                }
            }
        } else if (entrega.getEmpleado() != null) {
            // backward compatibility
            Empleado emp = entrega.getEmpleado();
            idEmpleado = emp.getIdEmpleado();
            Usuario u = emp.getUsuario();
            nombreEmpleado = u != null ? u.getNombre() + " " + u.getApellido() : null;
        }

        String nombreCliente = null;
        String telefonoCliente = null;
        if (cliente != null && cliente.getUsuario() != null) {
            Usuario u = cliente.getUsuario();
            nombreCliente = u.getNombre() + " " + u.getApellido();
            telefonoCliente = u.getTelefono();
        }

        return new EntregaResponse(
                entrega.getIdEntrega(),
                entrega.getComanda() != null ? entrega.getComanda().getIdComanda() : null,
                entrega.getComanda() != null ? entrega.getComanda().getNumeroComanda() : null,
                idEmpleado,
                nombreEmpleado,
                entrega.getDireccionEntrega(),
                entrega.getLatitud(),
                entrega.getLongitud(),
                entrega.getLatitudActual(),
                entrega.getLongitudActual(),
                entrega.getDistanciaKm(),
                entrega.getTiempoEstimadoMin(),
                entrega.getCostoEnvio(),
                entrega.getEstado().name(),
                entrega.getFechaAsignacion(),
                entrega.getFechaEntrega(),
                entrega.getObservaciones(),
                sucursal != null ? sucursal.getIdSucursal() : null,
                sucursal != null ? sucursal.getNombre() : null,
                sucursal != null ? sucursal.getDireccion() : null,
                sucursal != null ? sucursal.getLatitud() : null,
                sucursal != null ? sucursal.getLongitud() : null,
                nombreCliente,
                telefonoCliente
        );
    }
}
