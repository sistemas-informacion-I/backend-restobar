package org.restobar.gaira.modulo_comercial.repository.caja;

import java.math.BigDecimal;
import java.util.List;

import org.restobar.gaira.modulo_comercial.entity.MovimientoCaja;
import org.restobar.gaira.modulo_comercial.entity.MovimientoCaja.Tipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, Long> {

    // Movimientos de una caja, más recientes primero
    List<MovimientoCaja> findByCaja_IdCajaOrderByFechaDesc(Long idCaja);

    // Suma de montos de una caja por tipo (INGRESO / EGRESO); 0 si no hay
    @Query("""
            SELECT COALESCE(SUM(m.monto), 0)
            FROM MovimientoCaja m
            WHERE m.caja.idCaja = :idCaja AND m.tipo = :tipo
            """)
    BigDecimal sumMontoByCajaAndTipo(@Param("idCaja") Long idCaja, @Param("tipo") Tipo tipo);

    // Cantidad de movimientos de una caja
    long countByCaja_IdCaja(Long idCaja);
}
