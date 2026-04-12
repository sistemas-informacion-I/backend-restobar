package org.restobar.gaira.modulo_acceso.repository;

import java.util.Optional;

import org.restobar.gaira.modulo_acceso.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(String nombre);

    @Query("""
            select distinct r from Rol r
            left join fetch r.rolesPermiso rp
            left join fetch rp.permiso
            where r.idRol = :id
            """)
    Optional<Rol> findByIdWithPermisos(@Param("id") Long id);

    boolean existsByNombre(String nombre);
}