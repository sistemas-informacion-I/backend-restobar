package org.restobar.gaira.modulo_carrito.repository;

import org.restobar.gaira.modulo_carrito.entity.CarritoCompras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CarritoComprasRepository extends JpaRepository<CarritoCompras, Long> {

    /** Carrito activo de un cliente autenticado en una sucursal específica. */
    Optional<CarritoCompras> findByIdClienteAndIdSucursalAndEstado(
            Long idCliente, Long idSucursal, String estado);

    /** Carrito activo de sesión anónima en una sucursal específica. */
    Optional<CarritoCompras> findBySessionIdAndIdSucursalAndEstado(
            String sessionId, Long idSucursal, String estado);

    /**
     * Carga el carrito con sus ítems en un único JOIN para evitar N+1.
     */
    @Query("""
            SELECT c FROM CarritoCompras c
            LEFT JOIN FETCH c.items
            WHERE c.idCarrito = :idCarrito
            """)
    Optional<CarritoCompras> findByIdWithItems(@Param("idCarrito") Long idCarrito);
}
