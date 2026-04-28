package org.restobar.gaira.modulo_acceso.dto.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SendCodeRequest(
                @NotBlank(message = "El correo es requerido") 
                @Email(message = "Correo debe ser válido") 
                String correo) {
}
