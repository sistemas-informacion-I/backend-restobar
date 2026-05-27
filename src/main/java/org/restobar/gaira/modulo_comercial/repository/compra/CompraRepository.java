package org.restobar.gaira.modulo_comercial.repository.compra;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.restobar.gaira.modulo_comercial.entity.Compra;
import org.restobar.gaira.modulo_comercial.entity.Compra.EstadoPago;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    // Busca una compra por su número de factura exacto
    Optional<Compra> findByNroFactura(String nroFactura);

    // Verifica si ya existe una compra con el número de factura dado
    boolean existsByNroFactura(String nroFactura);

    // Obtiene todas las compras realizadas a un proveedor específico
    List<Compra> findByProveedor_Id(Long idProveedor);

    // Filtra compras por su estado de pago (PENDIENTE, PAGADO, PARCIAL, VENCIDO)
    List<Compra> findByEstadoPago(EstadoPago estadoPago);

    // Busca compras dentro de un rango de fechas (inclusive)
    List<Compra> findByFechaCompraBetween(LocalDate inicio, LocalDate fin);

    // Obtiene una compra con sus detalles, proveedor y empleado cargados eagerly para evitar N+1
    @EntityGraph(attributePaths = {"detalles", "proveedor", "empleado"})
    Optional<Compra> findByIdCompra(Long idCompra);

    @Query("""
            SELECT c FROM Compra c
            ORDER BY c.fechaCompra DESC
            """)
    List<Compra> findAllByFechaDesc();
}
