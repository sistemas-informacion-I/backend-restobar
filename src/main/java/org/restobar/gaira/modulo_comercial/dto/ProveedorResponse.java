package org.restobar.gaira.modulo_comercial.dto;

import java.time.LocalDateTime;
import org.restobar.gaira.modulo_comercial.entity.CategoriaProducto;

public record ProveedorResponse(
        Long idProveedor,
        String empresa,
        String nit,
        String nombreContacto,
        String telefono,
        String correo,
        String direccion,
        CategoriaProducto categoriaProductos,
        Boolean activo,
        Long creadoPor,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
