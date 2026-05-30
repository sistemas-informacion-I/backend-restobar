package org.restobar.gaira.modulo_operaciones.dto.comanda;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Respuesta agrupada de la cola de preparación
 * Agrupa items por comanda para que el cocinero/bartender vea el orden completo
 */
@Getter
@Setter
@Builder
public class PreparacionQueueResponseDTO {

    private Long idComanda;
    private String numeroComanda;
    private String mesaNombre;
    private String tipoServicio;
    private String estadoComanda;
    private Integer totalItems;
    private Integer itemsPendientes;
    private Integer itemsEnPreparacion;
    private Integer itemsListos;
    private List<PreparacionQueueItemDTO> items;
}
