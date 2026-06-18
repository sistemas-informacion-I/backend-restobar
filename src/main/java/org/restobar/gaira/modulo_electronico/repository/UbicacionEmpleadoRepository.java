package org.restobar.gaira.modulo_electronico.repository;

import org.restobar.gaira.modulo_electronico.entity.UbicacionEmpleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UbicacionEmpleadoRepository extends JpaRepository<UbicacionEmpleado, Long> {

    @Query("SELECT u FROM UbicacionEmpleado u WHERE u.empleado.idEmpleado = :idEmpleado ORDER BY u.fechaRegistro DESC LIMIT 1")
    Optional<UbicacionEmpleado> findLatestByEmpleadoId(@Param("idEmpleado") Long idEmpleado);
}
