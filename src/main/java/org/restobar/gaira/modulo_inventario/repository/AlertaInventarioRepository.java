package org.restobar.gaira.modulo_inventario.repository;

import java.util.List;

import org.restobar.gaira.modulo_inventario.entity.AlertaInventario;
import org.restobar.gaira.modulo_inventario.entity.AlertaInventario.EstadoAlerta;
import org.restobar.gaira.modulo_inventario.entity.AlertaInventario.TipoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertaInventarioRepository extends JpaRepository<AlertaInventario, Long>, JpaSpecificationExecutor<AlertaInventario> {

    boolean existsByTipoAndStockSucursalIdStockAndEstadoIn(TipoAlerta tipo, Long idStock, List<EstadoAlerta> estados);

    boolean existsByTipoAndLoteInventarioIdLoteAndEstadoIn(TipoAlerta tipo, Long idLote, List<EstadoAlerta> estados);

    List<AlertaInventario> findByEstadoIn(List<EstadoAlerta> estados);

    long countByEstadoIn(List<EstadoAlerta> estados);

    long countByEstadoInAndSucursalIdSucursal(List<EstadoAlerta> estados, Long idSucursal);
}
