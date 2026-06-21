package org.restobar.gaira.modulo_comercial.repository.caja;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.restobar.gaira.modulo_comercial.entity.Caja;
import org.restobar.gaira.modulo_comercial.entity.Caja.Estado;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CajaRepository extends JpaRepository<Caja, Long> {

    // Caja abierta de una sucursal (a lo sumo una por la regla de negocio)
    Optional<Caja> findFirstBySucursal_IdSucursalAndEstado(Long idSucursal, Estado estado);

    // ¿La sucursal ya tiene una caja en el estado dado?
    boolean existsBySucursal_IdSucursalAndEstado(Long idSucursal, Estado estado);

    // Cajas de una sucursal ordenadas por apertura desc
    List<Caja> findBySucursal_IdSucursalOrderByFechaAperturaDesc(Long idSucursal);

    // Caja con sus movimientos cargados eagerly para evitar N+1
    @EntityGraph(attributePaths = { "movimientos", "sucursal", "empleadoApertura", "empleadoCierre" })
    Optional<Caja> findWithMovimientosByIdCaja(Long idCaja);

    @Query("""
            SELECT c FROM Caja c
            ORDER BY c.fechaApertura DESC
            """)
    List<Caja> findAllByFechaAperturaDesc();

    // Cajas dentro de un rango de fechas de apertura (inclusive)
    List<Caja> findByFechaAperturaBetween(LocalDateTime inicio, LocalDateTime fin);
}
