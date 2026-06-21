package org.restobar.gaira.modulo_inventario.dto.alerta;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.restobar.gaira.modulo_inventario.entity.AlertaInventario.EstadoAlerta;
import org.restobar.gaira.modulo_inventario.entity.AlertaInventario.TipoAlerta;

import lombok.Data;

@Data
public class AlertaInventarioResponse {
    private Long idAlerta;
    private TipoAlerta tipo;
    private EstadoAlerta estado;
    private LocalDateTime fechaGeneracion;
    private LocalDateTime fechaResolucion;
    private Long idSucursal;
    private String nombreSucursal;
    private Long idStock;
    private Long idInventario;
    private String nombreInventario;
    private Long idLote;
    private String numeroLote;
    private LocalDate fechaVencimiento;
    private String estadoLote;
    private String nombreTipo;
    private String nombreEstado;
    private String cantidadActual;
    private String cantidadMinima;
}
