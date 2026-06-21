package org.restobar.gaira.modulo_acceso.mapper.usuario;

import org.restobar.gaira.modulo_acceso.dto.usuario.ClienteResponse;
import org.restobar.gaira.modulo_acceso.entity.Cliente;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class ClienteMapper {

    public ClienteResponse toResponse(Cliente cliente) {
        if (cliente == null) return null;
        var usuario = cliente.getUsuario();
        return new ClienteResponse(
                cliente.getIdCliente(),
                usuario != null ? usuario.getIdUsuario() : null,
                cliente.getNit(),
                cliente.getRazonSocial(),
                cliente.getFechaNacimiento(),
                cliente.getPuntosFidelidad(),
                cliente.getNivelCliente(),
                cliente.getObservaciones(),
                buildNombreCompleto(usuario),
                usuario != null ? usuario.getCorreo() : null,
                usuario != null ? usuario.getTelefono() : null);
    }

    private String buildNombreCompleto(org.restobar.gaira.modulo_acceso.entity.Usuario usuario) {
        if (usuario == null) return null;
        String nombre = usuario.getNombre() != null ? usuario.getNombre() : "";
        String apellido = usuario.getApellido() != null ? usuario.getApellido() : "";
        String full = (nombre + " " + apellido).trim();
        return full.isEmpty() ? null : full;
    }

    public Map<String, Object> toAuditMap(Cliente cliente) {
        if (cliente == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idCliente", cliente.getIdCliente());
        map.put("nit", cliente.getNit());
        map.put("razonSocial", cliente.getRazonSocial());
        map.put("nivelCliente", cliente.getNivelCliente());
        map.put("puntosFidelidad", cliente.getPuntosFidelidad());
        return map;
    }

    public Map<String, Object> toAuditMap(ClienteResponse response) {
        if (response == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idCliente", response.idCliente());
        map.put("nit", response.nit());
        map.put("razonSocial", response.razonSocial());
        map.put("nivelCliente", response.nivelCliente());
        map.put("puntosFidelidad", response.puntosFidelidad());
        return map;
    }
}
