package org.restobar.gaira.modulo_comercial.repository;

import java.util.List;
import java.util.Optional;

import org.restobar.gaira.modulo_comercial.entity.NotaVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotaVentaRepository extends JpaRepository<NotaVenta, Long> {

    Optional<NotaVenta> findByComanda_IdComanda(Long idComanda);

    List<NotaVenta> findByClienteIdClienteOrderByFechaEmisionDesc(Long idCliente);

    @Query("SELECT n FROM NotaVenta n WHERE n.cliente.usuario.username = :username ORDER BY n.fechaEmision DESC")
    List<NotaVenta> findByClienteUsername(@Param("username") String username);
}