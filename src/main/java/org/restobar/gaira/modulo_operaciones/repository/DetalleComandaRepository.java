package org.restobar.gaira.modulo_operaciones.repository;

import java.util.List;

import org.restobar.gaira.modulo_operaciones.entity.DetalleComanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleComandaRepository extends JpaRepository<DetalleComanda, Long> {
    List<DetalleComanda> findByComandaIdComanda(Long idComanda);

    /**
     * Obtiene todos los detalles de comanda de una sucursal y estación, ordenados por fecha de creación
     */
    @Query("SELECT d FROM DetalleComanda d WHERE d.comanda.sucursal.idSucursal = :idSucursal " +
            "AND d.estacionPreparacion = :estacion " +
            "ORDER BY d.fechaCreacion ASC")
    List<DetalleComanda> findByComandaSucursalIdSucursalAndEstacionPreparacionOrderByFechaCreacionAsc(
            @Param("idSucursal") Long idSucursal,
            @Param("estacion") String estacion);

    /**
     * Obtiene todos los detalles de comanda de una sucursal ordenados por fecha de creación
     */
    @Query("SELECT d FROM DetalleComanda d WHERE d.comanda.sucursal.idSucursal = :idSucursal " +
            "ORDER BY d.fechaCreacion ASC")
    List<DetalleComanda> findByComandaSucursalIdSucursalOrderByFechaCreacionAsc(
            @Param("idSucursal") Long idSucursal);
}
