package org.restobar.gaira.modulo_inventario.dto.alerta;

import org.restobar.gaira.modulo_inventario.entity.AlertaInventario.EstadoAlerta;
import org.restobar.gaira.modulo_inventario.entity.AlertaInventario.TipoAlerta;

import lombok.Data;

@Data
public class AlertaInventarioRequest {
    private Long idSucursal;
    private TipoAlerta tipo;
    private EstadoAlerta estado;
}
