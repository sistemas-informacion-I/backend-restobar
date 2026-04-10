package org.restobar.gaira.modulo_acceso.repository;

import org.restobar.gaira.modulo_acceso.entity.LogAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, Long> {

    List<LogAuditoria> findTop100ByOrderByFechaOperacionDesc();
}