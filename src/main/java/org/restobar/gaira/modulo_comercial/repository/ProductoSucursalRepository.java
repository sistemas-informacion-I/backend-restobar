package org.restobar.gaira.modulo_comercial.repository;

import java.util.List;
import java.util.Optional;
import org.restobar.gaira.modulo_comercial.entity.ProductoSucursal;
import org.restobar.gaira.modulo_comercial.entity.ProductoSucursalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoSucursalRepository extends JpaRepository<ProductoSucursal, ProductoSucursalId> {
    List<ProductoSucursal> findByIdSucursal(Long idSucursal);
    Optional<ProductoSucursal> findByIdProductoFinalAndIdSucursal(Long idProductoFinal, Long idSucursal);
    List<ProductoSucursal> findByIdProductoFinal(Long idProductoFinal);
}
