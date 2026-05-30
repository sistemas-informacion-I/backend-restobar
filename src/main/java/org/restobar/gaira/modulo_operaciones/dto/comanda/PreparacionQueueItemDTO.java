package org.restobar.gaira.modulo_operaciones.dto.comanda;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO para un item individual en la cola de preparación (Kitchen Display Screen)
 */
@Getter
@Setter
@Builder
public class PreparacionQueueItemDTO {

    private Long idDetalleComanda;
    private Long idComanda;
    private String numeroComanda;
    private String mesaNombre;
    private String tipoServicio;
    private String nombreProducto;
    private Integer cantidad;
    private String notas;
    private String estado; // PENDIENTE, EN_PREPARACION, LISTO
    private String estacionPreparacion; // COCINA, BARRA
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaAceptacion; // Cuando se tomó el item
    private String empleadoAsignado; // Quién está preparando
    private Integer tiempoTranscurrido; // En segundos
}
