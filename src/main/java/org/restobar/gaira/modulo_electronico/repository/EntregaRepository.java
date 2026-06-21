package org.restobar.gaira.modulo_electronico.repository;

import org.restobar.gaira.modulo_electronico.entity.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntregaRepository extends JpaRepository<Entrega, Long> {

    Optional<Entrega> findByComandaIdComanda(Long idComanda);

    List<Entrega> findByEmpleadoIdEmpleado(Long idEmpleado);

    List<Entrega> findByEstado(Entrega.EstadoEntrega estado);

    List<Entrega> findByEstadoIn(List<Entrega.EstadoEntrega> estados);

    boolean existsByEmpleadoIdEmpleadoAndEstadoIn(Long idEmpleado, List<Entrega.EstadoEntrega> estados);

    List<Entrega> findByEmpleadoIdEmpleadoAndEstadoIn(Long idEmpleado, List<Entrega.EstadoEntrega> estados);

    @Query("SELECT e FROM Entrega e WHERE e.estado = 'PENDIENTE'")
    List<Entrega> findPendientes();

    // --- Queries por id_usuario_repartidor  ---

    List<Entrega> findByIdUsuarioRepartidor(Long idUsuario);

    boolean existsByIdUsuarioRepartidorAndEstadoIn(Long idUsuario, List<Entrega.EstadoEntrega> estados);

    List<Entrega> findByIdUsuarioRepartidorAndEstadoIn(Long idUsuario, List<Entrega.EstadoEntrega> estados);
}
