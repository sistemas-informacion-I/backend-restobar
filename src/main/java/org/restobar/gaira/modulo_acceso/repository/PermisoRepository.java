package org.restobar.gaira.modulo_acceso.repository;

import org.restobar.gaira.modulo_acceso.entity.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermisoRepository extends JpaRepository<Permiso, Long> {

    Optional<Permiso> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}