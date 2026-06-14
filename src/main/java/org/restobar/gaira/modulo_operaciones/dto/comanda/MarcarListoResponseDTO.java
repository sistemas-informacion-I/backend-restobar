package org.restobar.gaira.modulo_operaciones.dto.comanda;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para respuesta al marcar un item como LISTO
 * Puede incluir información de descuentos de inventario o errores
 */
@Getter
@Setter
@Builder
public class MarcarListoResponseDTO {

    private Long idDetalleComanda;
    private Long idComanda;
    private String estado; // LISTO
    private String estadoComanda; // LISTA si todos los items están LISTO
    private Boolean exitoso;
    private String mensaje;
    private DescuentoInventarioDTO descuentoInventario;

    @Getter
    @Setter
    @Builder
    public static class DescuentoInventarioDTO {
        private Boolean exitoso;
        private String mensaje;
        private Integer ingredientesDescontados;
        private Integer ingredientesConError;
    }
}
