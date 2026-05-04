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
                proveedor.getId(),
                proveedor.getEmpresa(),
                proveedor.getNit(),
                proveedor.getNombreContacto(),
                proveedor.getTelefono(),
                proveedor.getCorreo(),
                proveedor.getDireccion(),
                proveedor.getCategoriaProductos(),
                proveedor.getActivo(),
                proveedor.getCreadoPor() != null ? proveedor.getCreadoPor().getIdUsuario() : null,
                proveedor.getCreatedAt(),
                proveedor.getUpdatedAt());
    }

    public Map<String, Object> toAuditMap(Proveedor proveedor) {
        if (proveedor == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idProveedor", proveedor.getId());
        map.put("empresa", proveedor.getEmpresa());
        map.put("nit", proveedor.getNit());
        map.put("nombreContacto", proveedor.getNombreContacto());
        map.put("telefono", proveedor.getTelefono());
        map.put("correo", proveedor.getCorreo());
        map.put("direccion", proveedor.getDireccion());
        map.put("categoriaProductos", proveedor.getCategoriaProductos());
        map.put("activo", proveedor.getActivo());
        map.put("creadoPor", proveedor.getCreadoPor() != null ? proveedor.getCreadoPor().getIdUsuario() : null);
        return map;
    }

    public Map<String, Object> toAuditMap(ProveedorResponse response) {
        if (response == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idProveedor", response.idProveedor());
        map.put("empresa", response.empresa());
        map.put("nit", response.nit());
        map.put("nombreContacto", response.nombreContacto());
        map.put("telefono", response.telefono());
        map.put("correo", response.correo());
        map.put("direccion", response.direccion());
        map.put("categoriaProductos", response.categoriaProductos());
        map.put("activo", response.activo());
        map.put("creadoPor", response.creadoPor());
        return map;
    }
}
