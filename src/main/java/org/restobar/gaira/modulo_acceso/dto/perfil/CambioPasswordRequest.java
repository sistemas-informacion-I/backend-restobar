package org.restobar.gaira.modulo_acceso.dto.perfil;

import jakarta.validation.constraints.NotBlank;

public record CambioPasswordRequest(
    @NotBlank(message = "La contraseña actual es obligatoria") String passwordActual,
    @NotBlank(message = "La nueva contraseña es obligatoria") String passwordNuevo
) {}
