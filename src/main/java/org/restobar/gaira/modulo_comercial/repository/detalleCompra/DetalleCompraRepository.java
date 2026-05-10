package org.restobar.gaira.modulo_comercial.repository.detalleCompra;

import org.restobar.gaira.modulo_comercial.entity.DetalleCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleCompraRepository extends JpaRepository<DetalleCompra, Long> {

    // Obtiene todos los detalles asociados a una compra específica
    List<DetalleCompra> findByCompra_IdCompra(Long idCompra);

    // Elimina todos los detalles de una compra (útil al actualizar/eliminar una compra)
    void deleteByCompra_IdCompra(Long idCompra);

    // Calcula la suma total de subtotales de los detalles de una compra
    @Query("SELECT SUM(d.subTotal) FROM DetalleCompra d WHERE d.compra.idCompra = :idCompra")
    java.math.BigDecimal sumSubTotalByCompraId(@Param("idCompra") Long idCompra);
}
