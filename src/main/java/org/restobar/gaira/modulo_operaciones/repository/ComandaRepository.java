package org.restobar.gaira.modulo_operaciones.repository;

import java.util.List;
import java.util.Optional;

import org.restobar.gaira.modulo_operaciones.entity.Comanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComandaRepository extends JpaRepository<Comanda, Long> {

    Optional<Comanda> findByNumeroComanda(String numeroComanda);

    List<Comanda> findBySucursalIdSucursal(Long idSucursal);

    Optional<Comanda> findByIdComandaAndSucursalIdSucursal(Long idComanda, Long idSucursal);

    List<Comanda> findByMesa_IdMesa(Long idMesa);

    List<Comanda> findByEstado(String estado);
}