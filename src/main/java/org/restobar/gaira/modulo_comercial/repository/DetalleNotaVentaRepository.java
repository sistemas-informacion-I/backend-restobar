package org.restobar.gaira.modulo_comercial.repository;

import org.restobar.gaira.modulo_comercial.entity.DetalleNotaVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleNotaVentaRepository extends JpaRepository<DetalleNotaVenta, Long> {
}