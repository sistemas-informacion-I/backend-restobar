package org.restobar.gaira.modulo_comercial.repository.notaVenta;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.restobar.gaira.modulo_comercial.entity.NotaVenta;
import org.restobar.gaira.modulo_comercial.entity.NotaVenta.Estado;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotaVentaRepository extends JpaRepository<NotaVenta, Long> {

    List<NotaVenta> findByCliente_IdCliente(Long idCliente);

    List<NotaVenta> findByEstado(Estado estado);

    List<NotaVenta> findByFechaEmisionBetween(LocalDate inicio, LocalDate fin);

    @EntityGraph(attributePaths = {"detalles", "cliente", "empleado", "sucursal"})
    Optional<NotaVenta> findByIdNotaVenta(Long idNotaVenta);

    @Query("SELECT n FROM NotaVenta n ORDER BY n.fechaEmision DESC")
    List<NotaVenta> findAllByFechaDesc();

    List<NotaVenta> findByClienteIdClienteOrderByFechaEmisionDesc(Long idCliente);

    Optional<NotaVenta> findByComanda_IdComanda(Long idComanda);

    @Query("SELECT n FROM NotaVenta n WHERE n.cliente.usuario.username = :username ORDER BY n.fechaEmision DESC")
    List<NotaVenta> findByClienteUsername(@Param("username") String username);
}
