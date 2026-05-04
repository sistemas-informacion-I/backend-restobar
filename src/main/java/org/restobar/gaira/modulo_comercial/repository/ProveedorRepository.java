package org.restobar.gaira.modulo_comercial.repository;

import java.util.List;
import java.util.Optional;

import org.restobar.gaira.modulo_comercial.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    Optional<Proveedor> findByNit(String nit);

    boolean existsByNit(String nit);

    boolean existsByCorreo(String correo);

    @Query("""
            SELECT p FROM Proveedor p
            WHERE (:empresa IS NULL OR LOWER(p.empresa) LIKE :empresa)
              AND (:nit IS NULL OR LOWER(p.nit) LIKE :nit)
              AND (:categoria IS NULL OR LOWER(p.categoriaProductos) LIKE :categoria)
            ORDER BY p.empresa ASC
            """)
    List<Proveedor> findByFiltros(
            @Param("empresa") String empresa,
            @Param("nit") String nit,
            @Param("categoria") String categoria);
}
