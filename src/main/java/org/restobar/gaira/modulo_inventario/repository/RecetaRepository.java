package org.restobar.gaira.modulo_inventario.repository;

import java.util.List;
import java.util.Optional;
import org.restobar.gaira.modulo_inventario.entity.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {

    boolean existsByProductoFinalIdProductoFinalAndNombreIgnoreCase(Long idProductoFinal, String nombre);

    @Query("""
            SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
            FROM Receta r
            WHERE r.productoFinal.idProductoFinal = :idProductoFinal
              AND LOWER(r.nombre) = LOWER(:nombre)
              AND (:idExcluir IS NULL OR r.idReceta <> :idExcluir)
            """)
    boolean existsNombrePorProductoExcluyendoId(
            @Param("idProductoFinal") Long idProductoFinal,
            @Param("nombre") String nombre,
            @Param("idExcluir") Long idExcluir);

    List<Receta> findByActivoTrue();

    @Query("""
            SELECT DISTINCT r FROM Receta r
            JOIN FETCH r.productoFinal pf
            LEFT JOIN FETCH r.sucursalReferencia sr
            LEFT JOIN FETCH r.ingredientes ir
            LEFT JOIN FETCH ir.inventario inv
            WHERE (:nombre IS NULL OR LOWER(r.nombre) LIKE :nombre)
              AND (:activo IS NULL OR r.activo = :activo)
              AND (:idProductoFinal IS NULL OR pf.idProductoFinal = :idProductoFinal)
            ORDER BY r.nombre ASC
            """)
    List<Receta> findByFiltros(
            @Param("nombre") String nombre,
            @Param("activo") Boolean activo,
            @Param("idProductoFinal") Long idProductoFinal);

    @Query("""
            SELECT DISTINCT r FROM Receta r
            JOIN FETCH r.productoFinal pf
            LEFT JOIN FETCH r.sucursalReferencia sr
            LEFT JOIN FETCH r.ingredientes ir
            LEFT JOIN FETCH ir.inventario inv
            WHERE r.idReceta = :id
            """)
    Optional<Receta> findDetalleById(@Param("id") Long id);
}
