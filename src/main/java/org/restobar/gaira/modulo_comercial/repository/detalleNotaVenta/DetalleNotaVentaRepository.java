package org.restobar.gaira.modulo_comercial.repository.detalleNotaVenta;

import org.restobar.gaira.modulo_comercial.entity.DetalleNotaVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleNotaVentaRepository extends JpaRepository<DetalleNotaVenta, Long> {

    List<DetalleNotaVenta> findByNotaVenta_IdNotaVenta(Long idNotaVenta);

    void deleteByNotaVenta_IdNotaVenta(Long idNotaVenta);

    @Query("SELECT SUM(d.subTotal) FROM DetalleNotaVenta d WHERE d.notaVenta.idNotaVenta = :idNotaVenta")
    java.math.BigDecimal sumSubTotalByNotaVentaId(@Param("idNotaVenta") Long idNotaVenta);
}
