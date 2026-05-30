package org.restobar.gaira.modulo_operaciones.dto.comanda;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ComandaResponseDTO {

    private Long idComanda;
    private String numeroComanda;
    private Long idSucursal;
    private String nombreSucursal;
    private Long idCliente;
    private String clienteNombre;
    private Long idEmpleado;
    private String empleadoNombre;
    private Long idMesa;
    private String mesaNombre;
    private String tipoServicio;
    private String estado;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private Integer numeroPersonas;
    private String observaciones;
    private List<DetalleComandaResponseDTO> items;

    @Getter
    @Setter
    @Builder
    public static class DetalleComandaResponseDTO {
        private Long idDetalleComanda;
        private Long idProductoFinal;
        private String nombreProducto;
        private BigDecimal precioUnitario;
        private Integer cantidad;
        private String notas;
        private String estado;
        private String estacionPreparacion;
        private LocalDateTime fechaAceptacion;
        private String empleadoAsignado;
    }
}
