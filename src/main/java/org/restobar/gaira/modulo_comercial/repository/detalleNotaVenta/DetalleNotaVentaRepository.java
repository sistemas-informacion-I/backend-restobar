package org.restobar.gaira.modulo_comercial.repository.detalleNotaVenta;

import org.restobar.gaira.modulo_comercial.entity.DetalleNotaVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DetalleNotaVentaRepository extends JpaRepository<DetalleNotaVenta, Long> {

    List<DetalleNotaVenta> findByNotaVenta_IdNotaVenta(Long idNotaVenta);

    void deleteByNotaVenta_IdNotaVenta(Long idNotaVenta);

    @Query("SELECT SUM(d.subTotal) FROM DetalleNotaVenta d WHERE d.notaVenta.idNotaVenta = :idNotaVenta")
    BigDecimal sumSubTotalByNotaVentaId(@Param("idNotaVenta") Long idNotaVenta);

    @Query("SELECT d.productoFinal.id, d.productoFinal.nombre, " +
           "COALESCE(SUM(d.cantidad), 0), COALESCE(SUM(d.subTotal), 0) " +
           "FROM DetalleNotaVenta d WHERE d.notaVenta.estado = 'PAGADA' " +
           "AND (:idSucursal IS NULL OR d.notaVenta.sucursal.id = :idSucursal) " +
           "AND d.notaVenta.fechaEmision BETWEEN :inicio AND :fin " +
           "GROUP BY d.productoFinal.id, d.productoFinal.nombre " +
           "ORDER BY COALESCE(SUM(d.cantidad), 0) DESC")
    List<Object[]> findTopProducts(@Param("idSucursal") Long idSucursal,
                                   @Param("inicio") LocalDateTime inicio,
                                   @Param("fin") LocalDateTime fin);

    @Query("SELECT c.nombre, COALESCE(SUM(d.subTotal), 0) " +
           "FROM DetalleNotaVenta d JOIN d.productoFinal p JOIN p.categoria c " +
           "WHERE d.notaVenta.estado = 'PAGADA' " +
           "AND (:idSucursal IS NULL OR d.notaVenta.sucursal.id = :idSucursal) " +
           "AND d.notaVenta.fechaEmision BETWEEN :inicio AND :fin " +
           "GROUP BY c.nombre ORDER BY COALESCE(SUM(d.subTotal), 0) DESC")
    List<Object[]> findSalesByCategory(@Param("idSucursal") Long idSucursal,
                                       @Param("inicio") LocalDateTime inicio,
                                       @Param("fin") LocalDateTime fin);

    @Query("SELECT COALESCE(SUM((d.precioU - d.costoU) * d.cantidad), 0), " +
           "COALESCE(SUM(d.precioU * d.cantidad), 0) " +
           "FROM DetalleNotaVenta d WHERE d.notaVenta.estado = 'PAGADA' " +
           "AND (:idSucursal IS NULL OR d.notaVenta.sucursal.id = :idSucursal) " +
           "AND d.notaVenta.fechaEmision BETWEEN :inicio AND :fin")
    Object[] findProfit(@Param("idSucursal") Long idSucursal,
                        @Param("inicio") LocalDateTime inicio,
                        @Param("fin") LocalDateTime fin);
}
	