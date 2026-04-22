package org.restobar.gaira.modulo_operaciones.dto.sucursal;




import java.time.LocalTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SucursalRequestDTO { // con validaciones, llega del front

    @NotBlank(message = "Nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "Dirección no puede estar vacía")
    private String direccion;

    private String telefono;

    @Email(message = "Correo debe ser válido")
    private String correo;

    private LocalTime horarioApertura;
    private LocalTime horarioCierre;
    private String ciudad;
    private String departamento;
    private String estadoOperativo;
}
