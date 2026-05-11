package org.restobar.gaira.modulo_comercial.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProductoSucursalId implements Serializable {
    private Long idProductoFinal;
    private Long idSucursal;
}
