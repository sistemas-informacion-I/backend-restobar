package org.restobar.gaira.modulo_carrito.repository;

import org.restobar.gaira.modulo_carrito.entity.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {

    Optional<ItemCarrito> findByCarrito_IdCarritoAndIdProductoFinal(
            Long idCarrito, Long idProductoFinal);
}
