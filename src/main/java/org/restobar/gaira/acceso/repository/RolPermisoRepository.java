package org.restobar.gaira.acceso.repository;

import java.util.List;

import org.restobar.gaira.acceso.entity.RolPermiso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolPermisoRepository extends JpaRepository<RolPermiso, Long> {

    List<RolPermiso> findByRol_IdRol(Long idRol);

    boolean existsByRol_IdRolAndPermiso_IdPermiso(Long idRol, Long idPermiso);
}