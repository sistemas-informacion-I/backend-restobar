package org.restobar.gaira.modulo_inventario.repository;

import java.util.List;

import org.restobar.gaira.modulo_inventario.entity.LoteInventario;
import org.restobar.gaira.modulo_inventario.entity.LoteInventario.EstadoLote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoteInventarioRepository extends JpaRepository<LoteInventario, Long> {
    Page<LoteInventario> findByStockSucursalIdStock(Long idStock, Pageable pageable);

    List<LoteInventario> findByStockSucursalIdStockAndEstadoOrderByFechaIngresoAscIdLoteAsc(Long idStock,
            EstadoLote estado);
}
