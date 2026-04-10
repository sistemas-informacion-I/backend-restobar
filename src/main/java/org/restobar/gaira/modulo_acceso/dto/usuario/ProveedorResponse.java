package org.restobar.gaira.acceso.dto.usuario;

public record ProveedorResponse(
        Long idProveedor,
        Long idUsuario,
        String empresa,
        String nit,
        String nombreContacto,
        String telefonoContacto,
        String correoContacto,
        String categoriaProducto
) {
}
