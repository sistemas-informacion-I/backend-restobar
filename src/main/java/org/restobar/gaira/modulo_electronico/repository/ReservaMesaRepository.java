package org.restobar.gaira.modulo_electronico.repository;

import org.restobar.gaira.modulo_electronico.entity.ReservaMesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaMesaRepository extends JpaRepository<ReservaMesa, Long> {
}
