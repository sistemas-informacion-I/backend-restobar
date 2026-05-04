package org.restobar.gaira.modulo_acceso.repository;

import org.restobar.gaira.modulo_acceso.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    Optional<Empleado> findByUsuario_IdUsuario(Long idUsuario);

    Optional<Empleado> findByCodigoEmpleado(String codigoEmpleado);

    boolean existsByCodigoEmpleado(String codigoEmpleado);

    boolean existsByUsuario_IdUsuario(Long idUsuario);
    
    @org.springframework.data.jpa.repository.Query("SELECT e FROM Empleado e JOIN e.empleadoSucursales es WHERE es.sucursal.idSucursal = :idSucursal AND es.activo = true AND es.fechaFin IS NULL")
    java.util.List<Empleado> findBySucursalId(Long idSucursal);
}