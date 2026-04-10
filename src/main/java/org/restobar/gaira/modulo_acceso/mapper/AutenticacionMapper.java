package org.restobar.gaira.modulo_acceso.mapper;

import org.restobar.gaira.modulo_acceso.dto.auditoria.LogAuditoriaResponse;
import org.restobar.gaira.modulo_acceso.dto.permiso.PermisoResponse;
import org.restobar.gaira.modulo_acceso.dto.rol.RolResponse;
import org.restobar.gaira.modulo_acceso.dto.sesion.SesionResponse;
import org.restobar.gaira.modulo_acceso.dto.usuario.ClienteResponse;
import org.restobar.gaira.modulo_acceso.dto.usuario.EmpleadoResponse;
import org.restobar.gaira.modulo_acceso.dto.usuario.ProveedorResponse;
import org.restobar.gaira.modulo_acceso.dto.usuario.UsuarioResponse;
import org.restobar.gaira.modulo_acceso.entity.*;

public final class AutenticacionMapper {

    private AutenticacionMapper() {
    }

    public static UsuarioResponse toUsuarioResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getCi(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getUsername(),
                usuario.getTelefono(),
                usuario.getSexo(),
                usuario.getCorreo(),
                usuario.getDireccion(),
                usuario.getIntentosFallidos(),
                usuario.getEstadoAcceso(),
                usuario.getFechaRegistro(),
                usuario.getActivo(),
                usuario.getRolesUsuario() != null ? 
                    usuario.getRolesUsuario().stream()
                           .filter(ru -> Boolean.TRUE.equals(ru.getActivo()))
                           .map(ru -> toRolResponse(ru.getRol()))
                           .toList() 
                    : java.util.Collections.emptyList()
        );
    }

    public static RolResponse toRolResponse(Rol rol) {
        return new RolResponse(
                rol.getIdRol(),
                rol.getNombre(),
                rol.getDescripcion(),
                rol.getNivelAcceso(),
                rol.getActivo(),
                rol.getFechaCreacion(),
                rol.getRolesPermiso() != null ?
                    rol.getRolesPermiso().stream()
                       .filter(rp -> Boolean.TRUE.equals(rp.getActivo()))
                       .map(rp -> toPermisoResponse(rp.getPermiso()))
                       .toList()
                    : java.util.Collections.emptyList()
        );
    }

    public static PermisoResponse toPermisoResponse(Permiso permiso) {
        return new PermisoResponse(
                permiso.getIdPermiso(),
                permiso.getNombre(),
                permiso.getModulo(),
                permiso.getAccion(),
                permiso.getDescripcion(),
                permiso.getActivo(),
                permiso.getFechaCreacion()
        );
    }

    public static ClienteResponse toClienteResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getIdCliente(),
                cliente.getUsuario() != null ? cliente.getUsuario().getIdUsuario() : null,
                cliente.getNit(),
                cliente.getRazonSocial(),
                cliente.getFechaNacimiento(),
                cliente.getPuntosFidelidad(),
                cliente.getNivelCliente(),
                cliente.getObservaciones()
        );
    }

    public static EmpleadoResponse toEmpleadoResponse(Empleado empleado) {
        return new EmpleadoResponse(
                empleado.getIdEmpleado(),
                empleado.getUsuario() != null ? empleado.getUsuario().getIdUsuario() : null,
                empleado.getCodigoEmpleado(),
                empleado.getSalario(),
                empleado.getFechaContratacion(),
                empleado.getFechaFinalizacion()
        );
    }

    public static ProveedorResponse toProveedorResponse(Proveedor proveedor) {
        return new ProveedorResponse(
                proveedor.getIdProveedor(),
                proveedor.getUsuario() != null ? proveedor.getUsuario().getIdUsuario() : null,
                proveedor.getEmpresa(),
                proveedor.getNit(),
                proveedor.getNombreContacto(),
                proveedor.getTelefonoContacto(),
                proveedor.getCorreoContacto(),
                proveedor.getCategoriaProducto()
        );
    }

    public static SesionResponse toSesionResponse(Sesion sesion) {
        return new SesionResponse(
                sesion.getIdSesion(),
                sesion.getUsuario() != null ? sesion.getUsuario().getIdUsuario() : null,
                sesion.getFechaInicio(),
                sesion.getFechaExpiracion(),
                sesion.getIpOrigen(),
                sesion.getUserAgent(),
                sesion.getFechaCierre(),
                sesion.isActiva()
        );
    }

    public static LogAuditoriaResponse toLogAuditoriaResponse(LogAuditoria log) {
        return new LogAuditoriaResponse(
                log.getIdLog(),
                log.getTabla(),
                log.getOperacion(),
                log.getIdRegistro(),
                log.getDatosAnteriores(),
                log.getDatosNuevos(),
                log.getUsuario() != null ? log.getUsuario().getIdUsuario() : null,
                log.getIdSucursal(),
                log.getIpOrigen(),
                log.getUserAgent(),
                log.getFechaOperacion()
        );
    }
}
