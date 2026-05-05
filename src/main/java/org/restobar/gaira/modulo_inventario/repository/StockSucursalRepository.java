package org.restobar.gaira.modulo_inventario.repository;

import java.util.List;
import java.util.Optional;

import org.restobar.gaira.modulo_inventario.entity.StockSucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockSucursalRepository extends JpaRepository<StockSucursal, Long> {
    List<StockSucursal> findBySucursalIdSucursal(Long idSucursal);
    Optional<StockSucursal> findByInventarioIdInventarioAndSucursalIdSucursal(Long idInventario, Long idSucursal);
}
