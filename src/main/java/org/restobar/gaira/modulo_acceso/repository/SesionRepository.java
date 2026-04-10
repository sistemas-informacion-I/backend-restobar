package org.restobar.gaira.acceso.repository;

import org.restobar.gaira.acceso.entity.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SesionRepository extends JpaRepository<Sesion, Long> {

    /**
     * Sesión activa = refresh_token válido y fecha_cierre IS NULL.
     */
    Optional<Sesion> findByRefreshTokenAndFechaCierreIsNull(String refreshToken);

    /**
     * Sesión activa = token_sesion válido y fecha_cierre IS NULL.
     */
    Optional<Sesion> findByTokenSesionAndFechaCierreIsNull(String tokenSesion);

    List<Sesion> findByUsuario_IdUsuarioOrderByFechaInicioDesc(Long idUsuario);
}