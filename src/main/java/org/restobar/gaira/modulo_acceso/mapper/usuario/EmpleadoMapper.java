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
                        .toList() : java.util.Collections.emptyList());
    }

    public Map<String, Object> toAuditMap(Empleado empleado) {
        if (empleado == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idEmpleado", empleado.getIdEmpleado());
        map.put("codigoEmpleado", empleado.getCodigoEmpleado());
        map.put("salario", empleado.getSalario());
        map.put("turno", empleado.getTurno());
        map.put("fechaContratacion", empleado.getFechaContratacion());
        return map;
    }
}
