package org.restobar.gaira.modulo_acceso.repository;

import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByCi(String ci);

    boolean existsByCorreo(String correo);

    boolean existsByUsername(String username);

    Optional<Usuario> findByCi(String ci);

    Optional<Usuario> findByCorreo(String correo);

    Optional<Usuario> findByUsername(String username);

    /**
     * Carga un usuario activo con todos sus roles y permisos en un solo fetch
     * para construir el principal de seguridad sin N+1 queries.
     */
    @Query("""
            select u from Usuario u
            left join fetch u.rolesUsuario ru
            left join fetch ru.rol r
            left join fetch r.rolesPermiso rp
            left join fetch rp.permiso
            where u.username = :username
              and u.activo = true
            """)
    Optional<Usuario> findActiveByUsernameWithAuthorities(@Param("username") String username);

    /**
     * Carga todos los usuarios con sus roles en un solo fetch.
     */
    @Query("""
            select distinct u from Usuario u
            left join fetch u.rolesUsuario ru
            left join fetch ru.rol r
            left join fetch r.rolesPermiso rp
            left join fetch rp.permiso
            """)
    List<Usuario> findAllWithRoles();

    /**
     * Carga un usuario por ID con sus roles en un solo fetch.
     */
    @Query("""
            select u from Usuario u
            left join fetch u.rolesUsuario ru
            left join fetch ru.rol r
            left join fetch r.rolesPermiso rp
            left join fetch rp.permiso
            where u.idUsuario = :id
            """)
    Optional<Usuario> findByIdWithRoles(@Param("id") Long id);
}
