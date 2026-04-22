package org.restobar.gaira.modulo_operaciones.repository;

import java.util.List;
import java.util.Optional;

import org.restobar.gaira.modulo_operaciones.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectorRepository extends JpaRepository<Sector, Long> {

    List<Sector> findByActivoTrue();

    List<Sector> findBySucursal_IdSucursalAndActivoTrue(Long idSucursal);

    boolean existsByNombreAndSucursal_IdSucursal(String nombre, Long idSucursal);

    Optional<Sector> findByNombreAndSucursal_IdSucursal(String nombre, Long idSucursal);
}