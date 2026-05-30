package org.restobar.gaira.modulo_electronico.repository;

import java.util.List;

import org.restobar.gaira.modulo_electronico.entity.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {

    List<MetodoPago> findByActivoTrue();
}