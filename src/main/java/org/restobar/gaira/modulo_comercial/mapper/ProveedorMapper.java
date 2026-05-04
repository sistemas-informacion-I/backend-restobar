package org.restobar.gaira.modulo_comercial.mapper;

import org.restobar.gaira.modulo_comercial.dto.ProveedorCreate;
import org.restobar.gaira.modulo_comercial.dto.ProveedorResponse;
import org.restobar.gaira.modulo_comercial.dto.ProveedorUpdate;
import org.restobar.gaira.modulo_comercial.entity.Proveedor;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
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

    public Proveedor toEntity(ProveedorCreate create, Usuario creadoPor) {
        if (create == null) return null;

        return Proveedor.builder()
                .empresa(create.empresa().trim())
                .nit(create.nit() != null ? create.nit().trim() : null)
                .nombreContacto(create.nombreContacto().trim())
                .telefono(create.telefono().trim())
                .correo(create.correo() != null ? create.correo().trim() : null)
                .direccion(create.direccion())
                .categoriaProductos(create.categoriaProductos())
                .activo(create.activo() == null || create.activo())
                .creadoPor(creadoPor)
                .build();
    }

    public void updateEntityFromDto(Proveedor proveedor, ProveedorUpdate update) {
        if (proveedor == null || update == null) return;

        proveedor.setEmpresa(update.empresa().trim());
        proveedor.setNit(update.nit() != null ? update.nit().trim() : null);
        proveedor.setNombreContacto(update.nombreContacto().trim());
        proveedor.setTelefono(update.telefono().trim());
        proveedor.setCorreo(update.correo() != null ? update.correo().trim() : null);
        proveedor.setDireccion(update.direccion());
        proveedor.setCategoriaProductos(update.categoriaProductos());
        if (update.activo() != null) {
            proveedor.setActivo(update.activo());
        }
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
