package org.restobar.gaira.modulo_comercial.repository;

import java.util.List;
import java.util.Optional;
import org.restobar.gaira.modulo_comercial.entity.ProductoFinal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoFinalRepository extends JpaRepository<ProductoFinal, Long> {
    Optional<ProductoFinal> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);

    @Query("""
            SELECT pf FROM ProductoFinal pf
            WHERE (:idCategoria IS NULL OR pf.categoria.id = :idCategoria)
                AND (:activo IS NULL OR pf.activo = :activo)
            ORDER BY pf.nombre ASC
            """)
    List<ProductoFinal> findByFiltros(
            @Param("idCategoria") Long idCategoria,
            @Param("activo") Boolean activo);
}
