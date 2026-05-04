package org.restobar.gaira.modulo_comercial.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.restobar.gaira.modulo_comercial.entity.CategoriaProducto;
import lombok.Builder;

@Builder
public record ProveedorCreate(
        @NotBlank(message = "El nombre de la empresa es obligatorio")
        @Size(max = 150, message = "El nombre de la empresa no puede superar 150 caracteres")
        String empresa,

        @Size(max = 30, message = "El NIT no puede superar 30 caracteres")
        String nit,

        @NotBlank(message = "El nombre del contacto es obligatorio")
        @Size(max = 100, message = "El nombre del contacto no puede superar 100 caracteres")
        String nombreContacto,

        @NotBlank(message = "El teléfono es obligatorio")
        @Size(max = 20, message = "El teléfono no puede superar 20 caracteres")
        String telefono,

        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 100, message = "El correo no puede superar 100 caracteres")
        String correo,

        @Size(max = 200, message = "La dirección no puede superar 200 caracteres")
        String direccion,

        CategoriaProducto categoriaProductos,

        Boolean activo
) {}
