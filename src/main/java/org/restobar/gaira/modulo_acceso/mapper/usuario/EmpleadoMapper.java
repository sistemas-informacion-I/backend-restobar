package org.restobar.gaira.modulo_acceso.mapper.usuario;

import org.restobar.gaira.modulo_acceso.dto.usuario.EmpleadoResponse;
import org.restobar.gaira.modulo_acceso.entity.Empleado;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class EmpleadoMapper {

    public EmpleadoResponse toResponse(Empleado empleado) {
        if (empleado == null) return null;
        Usuario usuario = empleado.getUsuario();
        return new EmpleadoResponse(
                empleado.getIdEmpleado(),
                usuario != null ? usuario.getIdUsuario() : null,
                usuario != null ? usuario.getCi() : null,
                usuario != null ? usuario.getNombre() : null,
                usuario != null ? usuario.getApellido() : null,
                usuario != null ? usuario.getUsername() : null,
                usuario != null ? usuario.getTelefono() : null,
                usuario != null ? usuario.getSexo() : null,
                usuario != null ? usuario.getCorreo() : null,
                usuario != null ? usuario.getDireccion() : null,
                usuario != null ? usuario.getActivo() : null,
                usuario != null ? usuario.getEstadoAcceso() : null,
                empleado.getCodigoEmpleado(),
                empleado.getSalario(),
                empleado.getTurno(),
                empleado.getFechaContratacion(),
                empleado.getFechaFinalizacion(),
                usuario != null ? usuario.getRolesUsuario().stream()
                        .map(ru -> ru.getRol().getNombre())
                        .toList() : java.util.Collections.emptyList(),
                empleado.getEmpleadoSucursales().stream()
                        .filter(es -> Boolean.TRUE.equals(es.getActivo()) && es.getFechaFin() == null)
                        .map(es -> es.getSucursal().getIdSucursal())
                        .findFirst().orElse(null),
                empleado.getEmpleadoSucursales().stream()
                        .filter(es -> Boolean.TRUE.equals(es.getActivo()) && es.getFechaFin() == null)
                        .map(es -> es.getSucursal().getNombre())
                        .findFirst().orElse(null));
    }

    public Map<String, Object> toAuditMap(Empleado empleado) {
        if (empleado == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idEmpleado", empleado.getIdEmpleado());
        map.put("codigoEmpleado", empleado.getCodigoEmpleado());
        map.put("salario", empleado.getSalario());
        map.put("turno", empleado.getTurno());
        map.put("fechaContratacion", empleado.getFechaContratacion());
        map.put("fechaFinalizacion", empleado.getFechaFinalizacion());
        
        Usuario usuario = empleado.getUsuario();
        if (usuario != null) {
            map.put("idUsuario", usuario.getIdUsuario());
            map.put("ci", usuario.getCi());
            map.put("username", usuario.getUsername());
            map.put("nombre", usuario.getNombre());
            map.put("apellido", usuario.getApellido());
            map.put("correo", usuario.getCorreo());
            map.put("telefono", usuario.getTelefono());
            map.put("sexo", usuario.getSexo());
            map.put("direccion", usuario.getDireccion());
            map.put("estadoAcceso", usuario.getEstadoAcceso());
            map.put("activo", usuario.getActivo());
        }
        return map;
    }

    public Map<String, Object> toAuditMap(EmpleadoResponse dto) {
        if (dto == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idEmpleado", dto.idEmpleado());
        map.put("idUsuario", dto.idUsuario());
        map.put("codigoEmpleado", dto.codigoEmpleado());
        map.put("salario", dto.salario());
        map.put("turno", dto.turno());
        map.put("fechaContratacion", dto.fechaContratacion());
        map.put("fechaFinalizacion", dto.fechaFinalizacion());
        map.put("ci", dto.ci());
        map.put("username", dto.username());
        map.put("nombre", dto.nombre());
        map.put("apellido", dto.apellido());
        map.put("correo", dto.correo());
        map.put("telefono", dto.telefono());
        map.put("sexo", dto.sexo());
        map.put("direccion", dto.direccion());
        map.put("estadoAcceso", dto.estadoAcceso());
        map.put("activo", dto.activo());
        return map;
    }

}


