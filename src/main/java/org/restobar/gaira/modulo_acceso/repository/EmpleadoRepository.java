package org.restobar.gaira.modulo_acceso.repository;

import org.restobar.gaira.modulo_acceso.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    Optional<Empleado> findByUsuario_IdUsuario(Long idUsuario);

    Optional<Empleado> findByCodigoEmpleado(String codigoEmpleado);

    boolean existsByCodigoEmpleado(String codigoEmpleado);

    boolean existsByUsuario_IdUsuario(Long idUsuario);
}