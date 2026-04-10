package org.restobar.gaira.acceso.repository;

import org.restobar.gaira.acceso.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    Optional<Proveedor> findByUsuario_IdUsuario(Long idUsuario);

    Optional<Proveedor> findByNit(String nit);

    boolean existsByNit(String nit);
}