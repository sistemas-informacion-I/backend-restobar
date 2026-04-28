package org.restobar.gaira.modulo_operaciones.repository;

import org.restobar.gaira.modulo_operaciones.entity.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {

    List<Mesa> findBySectorIdSector(Long idSector);

    List<Mesa> findBySectorIdSectorAndActivoTrue(Long idSector);

    @Query("SELECT m FROM Mesa m WHERE m.sector.sucursal.idSucursal = :idSucursal")
    List<Mesa> findBySucursalId(@Param("idSucursal") Long idSucursal);

    boolean existsByNumeroMesaAndSectorIdSector(String numeroMesa, Long idSector);
}