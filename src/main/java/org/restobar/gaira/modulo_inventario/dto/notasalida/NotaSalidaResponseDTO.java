package org.restobar.gaira.modulo_inventario.dto.notasalida;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.restobar.gaira.modulo_inventario.entity.NotaSalida;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotaSalidaResponseDTO {

    private Long idNotaSalida;
    private Long idSucursal;
    private Long idEmpleado;
    private String nombreEmpleado;
    private NotaSalida.TipoGasto tipoGasto;
    private NotaSalida.EstadoNota estado;
    private BigDecimal montoTotal;
    private LocalDateTime fecha;
    private String descripcion;
    private String observaciones;

    private List<DetalleNotaSalidaResponseDTO> detalles;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleNotaSalidaResponseDTO {
        private Long idDetalle;
        private Long idStockSucursal;
        private String nombreInventario;
        private String descripcion;
        private BigDecimal cantidad;
        private BigDecimal monto;
    }
}
