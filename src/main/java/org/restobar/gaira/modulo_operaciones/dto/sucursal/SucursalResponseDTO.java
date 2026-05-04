package org.restobar.gaira.modulo_operaciones.dto.sucursal;



import java.time.LocalTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class SucursalResponseDTO { // sin validaciones

    private Long idSucursal;
    private String nombre;
    private String direccion;
    private String telefono;
    private String correo;
    private LocalTime horarioApertura;
    private LocalTime horarioCierre;
    private String ciudad;
    private String departamento;
    private String estadoOperativo;
    private Boolean activo;
    private Long idResponsable;
    private String nombreResponsable;
    private java.util.List<EmpleadoDetalleDTO> empleados;

    @Getter
    @Setter
    @Builder
    public static class EmpleadoDetalleDTO {
        private Long idUsuario;
        private String nombreCompleto;
        private String rol;
    }
}
