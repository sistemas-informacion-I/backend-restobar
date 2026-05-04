package org.restobar.gaira.modulo_acceso.dto.usuario;

import java.time.LocalDateTime;

public record ProveedorResponse(
        Long idProveedor,
        String empresa,
        String nit,
        String nombreContacto,
        String telefono,
        String correo,
        String direccion,
        String categoriaProductos,
        Boolean activo,
        Long creadoPor,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
