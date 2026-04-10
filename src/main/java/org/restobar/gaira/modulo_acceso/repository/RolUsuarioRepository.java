package org.restobar.gaira.acceso.repository;

import java.util.List;

import org.restobar.gaira.acceso.entity.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolUsuarioRepository extends JpaRepository<RolUsuario, Long> {

    List<RolUsuario> findByUsuario_IdUsuario(Long idUsuario);

    boolean existsByUsuario_IdUsuarioAndRol_IdRol(Long idUsuario, Long idRol);
}