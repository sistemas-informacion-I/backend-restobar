package org.restobar.gaira.modulo_inventario.repository;

import java.util.List;
import java.util.Optional;

import org.restobar.gaira.modulo_inventario.entity.StockSucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockSucursalRepository extends JpaRepository<StockSucursal, Long> {
    List<StockSucursal> findBySucursalIdSucursal(Long idSucursal);
    Optional<StockSucursal> findByInventarioIdInventarioAndSucursalIdSucursal(Long idInventario, Long idSucursal);

    @Query("""
            SELECT ss FROM StockSucursal ss
            WHERE ss.sucursal.idSucursal = :idSucursal
                AND ss.inventario.idInventario IN :idsInventario
                AND ss.activo = true
            """)
    List<StockSucursal> findActivosParaCosto(
            @Param("idSucursal") Long idSucursal,
            @Param("idsInventario") List<Long> idsInventario);
}
