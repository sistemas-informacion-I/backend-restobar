package org.restobar.gaira.modulo_electronico.repository;

import java.util.Optional;

import org.restobar.gaira.modulo_electronico.entity.TransaccionOnline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransaccionOnlineRepository extends JpaRepository<TransaccionOnline, Long> {

    Optional<TransaccionOnline> findByNumeroTransaccion(String numeroTransaccion);

    Optional<TransaccionOnline> findTopByNotaVenta_IdNotaVentaOrderByIdTransaccionDesc(Long idNotaVenta);
}