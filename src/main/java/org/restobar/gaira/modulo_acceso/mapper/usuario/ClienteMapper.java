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
        return new ClienteResponse(
                cliente.getIdCliente(),
                cliente.getUsuario() != null ? cliente.getUsuario().getIdUsuario() : null,
                cliente.getNit(),
                cliente.getRazonSocial(),
                cliente.getFechaNacimiento(),
                cliente.getPuntosFidelidad(),
                cliente.getNivelCliente(),
                cliente.getObservaciones());
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
