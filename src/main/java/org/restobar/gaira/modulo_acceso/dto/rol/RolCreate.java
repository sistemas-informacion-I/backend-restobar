package org.restobar.gaira.modulo_acceso.dto.rol;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

@Builder
public record RolCreate(
                @NotBlank(message = "Nombre es requerido") String nombre,
                String descripcion,
                @Positive(message = "Nivel de acceso debe ser positivo") Integer nivelAcceso,
                Boolean activo,
                List<Long> permisos) {
}
