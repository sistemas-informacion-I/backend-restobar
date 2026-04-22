package org.restobar.gaira.modulo_acceso.mapper.usuario;

import org.restobar.gaira.modulo_acceso.dto.usuario.ProveedorResponse;
import org.restobar.gaira.modulo_acceso.entity.Proveedor;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProveedorMapper {

    public ProveedorResponse toResponse(Proveedor proveedor) {
        if (proveedor == null) return null;
        return new ProveedorResponse(
                proveedor.getIdProveedor(),
                proveedor.getUsuario() != null ? proveedor.getUsuario().getIdUsuario() : null,
                proveedor.getEmpresa(),
                proveedor.getNit(),
                proveedor.getNombreContacto(),
                proveedor.getTelefonoContacto(),
                proveedor.getCorreoContacto(),
                proveedor.getCategoriaProducto());
    }

    public Map<String, Object> toAuditMap(Proveedor proveedor) {
        if (proveedor == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idProveedor", proveedor.getIdProveedor());
        map.put("empresa", proveedor.getEmpresa());
        map.put("nit", proveedor.getNit());
        map.put("contacto", proveedor.getNombreContacto());
        return map;
    }

    public Map<String, Object> toAuditMap(ProveedorResponse response) {
        if (response == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idProveedor", response.idProveedor());
        map.put("empresa", response.empresa());
        map.put("nit", response.nit());
        map.put("contacto", response.nombreContacto());
        return map;
    }
}
