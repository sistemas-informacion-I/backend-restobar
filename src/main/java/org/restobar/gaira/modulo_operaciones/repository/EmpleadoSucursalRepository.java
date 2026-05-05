package org.restobar.gaira.modulo_operaciones.repository;

import org.restobar.gaira.modulo_operaciones.entity.EmpleadoSucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para gestionar la relación entre empleados y sucursales.
 */
@Repository
public interface EmpleadoSucursalRepository extends JpaRepository<EmpleadoSucursal, Long> {

    /**
     * Busca la asignación activa de un empleado a una sucursal por el ID de usuario.
     * @param idUsuario ID del usuario vinculado al empleado.
     * @return Opcional con la relación EmpleadoSucursal.
     */
    Optional<EmpleadoSucursal> findByEmpleado_Usuario_IdUsuarioAndActivoTrue(Long idUsuario);

    java.util.List<EmpleadoSucursal> findByEmpleado_IdEmpleado(Long idEmpleado);

    java.util.List<EmpleadoSucursal> findByEmpleado_Usuario_IdUsuario(Long idUsuario);
}
