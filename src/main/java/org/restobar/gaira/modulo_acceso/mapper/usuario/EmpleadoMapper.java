package org.restobar.gaira.modulo_acceso.mapper.usuario;

import org.restobar.gaira.modulo_acceso.dto.usuario.EmpleadoResponse;
import org.restobar.gaira.modulo_acceso.entity.Empleado;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class EmpleadoMapper {

    public EmpleadoResponse toResponse(Empleado empleado) {
        if (empleado == null) return null;
        return new EmpleadoResponse(
                empleado.getIdEmpleado(),
                empleado.getUsuario() != null ? empleado.getUsuario().getIdUsuario() : null,
                empleado.getCodigoEmpleado(),
                empleado.getSalario(),
                empleado.getFechaContratacion(),
                empleado.getFechaFinalizacion());
    }

    public Map<String, Object> toAuditMap(Empleado empleado) {
        if (empleado == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idEmpleado", empleado.getIdEmpleado());
        map.put("codigoEmpleado", empleado.getCodigoEmpleado());
        map.put("salario", empleado.getSalario());
        map.put("fechaContratacion", empleado.getFechaContratacion());
        return map;
    }

    public Map<String, Object> toAuditMap(EmpleadoResponse response) {
        if (response == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idEmpleado", response.idEmpleado());
        map.put("codigoEmpleado", response.codigoEmpleado());
        map.put("salario", response.salario());
        map.put("fechaContratacion", response.fechaContratacion());
        return map;
    }
}
