package org.restobar.gaira.modulo_electronico.repository;

import org.restobar.gaira.modulo_electronico.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByIdSucursalAndFechaReservaOrderByHoraInicioAsc(Long idSucursal, LocalDate fechaReserva);

    List<Reserva> findByIdClienteOrderByFechaReservaDescHoraInicioDesc(Long idCliente);

    @Query("""
            SELECT DISTINCT r
            FROM Reserva r
            JOIN FETCH r.mesas rm
            JOIN FETCH rm.mesa m
            WHERE r.idSucursal = :idSucursal
              AND r.fechaReserva = :fechaReserva
              AND (:estado IS NULL OR r.estado = :estado)
            ORDER BY r.horaInicio ASC
            """)
    List<Reserva> findPanelReservas(
            @Param("idSucursal") Long idSucursal,
            @Param("fechaReserva") LocalDate fechaReserva,
            @Param("estado") String estado);

    @Query("""
            SELECT COUNT(r) > 0
            FROM Reserva r
            JOIN r.mesas rm
            WHERE rm.mesa.idMesa = :idMesa
              AND r.fechaReserva = :fechaReserva
              AND r.estado IN :estadosActivos
              AND r.horaInicio < :horaFin
              AND r.horaFin > :horaInicio
            """)
    boolean existsReservaActivaParaMesa(
            @Param("idMesa") Long idMesa,
            @Param("fechaReserva") LocalDate fechaReserva,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin,
            @Param("estadosActivos") Collection<String> estadosActivos);
}
