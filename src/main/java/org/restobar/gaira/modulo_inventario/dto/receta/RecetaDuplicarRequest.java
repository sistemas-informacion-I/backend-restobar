package org.restobar.gaira.modulo_inventario.dto.receta;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RecetaDuplicarRequest(
        @NotBlank(message = "El nombre de la nueva receta es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
        String nombre,

        @Size(max = 80, message = "La etiqueta de versión no puede superar 80 caracteres")
        String versionEtiqueta,

        @NotNull(message = "La sucursal de referencia es obligatoria")
        Long idSucursalReferencia,

        LocalDate fechaVigenciaInicio,

        LocalDate fechaVigenciaFin
) {
}
