package org.restobar.gaira.modulo_inventario.dto.notasalida;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.restobar.gaira.modulo_inventario.entity.NotaSalida;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotaSalidaRequestDTO {

    @NotNull
    private Long idSucursal;

    private Long idEmpleado; // Si el cajero/admin que lo crea es el mismo

    @NotNull
    private NotaSalida.TipoGasto tipoGasto;

    private String descripcion;
    private String observaciones;

    @NotNull
    private List<DetalleNotaSalidaRequestDTO> detalles;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleNotaSalidaRequestDTO {

        private Long idStockSucursal; // Required only if tipoGasto == PERDIDA

        @NotBlank
        private String descripcion;

        @NotNull
        @Positive
        private BigDecimal cantidad;

        @NotNull
        @Positive
        private BigDecimal monto;
    }
}
