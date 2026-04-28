package org.restobar.gaira.modulo_acceso.dto.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record VerifyCodeRequest(
                @NotBlank(message = "El correo es requerido") 
                @Email(message = "Correo debe ser válido") 
                String correo,

                @NotBlank(message = "El código es requerido") 
                @Size(min = 6, max = 6, message = "El código debe tener 6 caracteres") 
                String codigo) {
}
