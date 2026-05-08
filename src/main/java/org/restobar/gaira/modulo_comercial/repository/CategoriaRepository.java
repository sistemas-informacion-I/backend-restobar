package org.restobar.gaira.modulo_comercial.repository;

import java.util.List;
import java.util.Optional;

import org.restobar.gaira.modulo_comercial.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    boolean existsByNombre(String nombre);

    boolean existsByNombreAndIdNot(String nombre, Long id);

    // Hijos directos de una categoría padre
    List<Categoria> findByCategoriaPadreId(Long idPadre);

    // Verifica si una categoría tiene al menos un hijo activo o inactivo
    boolean existsByCategoriaPadreId(Long idPadre);

    // Lista raíces (sin padre) ordenadas por nombre
    List<Categoria> findByCategoriaPadreIsNullOrderByNombreAsc();

    // Búsqueda por nombre parcial ignorando mayúsculas
    @Query("""
            SELECT c FROM Categoria c
            WHERE (:nombre IS NULL OR LOWER(c.nombre) LIKE :nombre)
            ORDER BY c.nivel ASC, c.nombre ASC
            """)
    List<Categoria> findByFiltros(@Param("nombre") String nombre);

    // Consulta para detectar ciclos: devuelve todos los descendientes de un nodo
    @Query(value = """
            WITH RECURSIVE descendientes AS (
                SELECT id_categoria FROM categoria WHERE id_categoria = :idRaiz
                UNION ALL
                SELECT c.id_categoria FROM categoria c
                INNER JOIN descendientes d ON c.id_categoria_padre = d.id_categoria
            )
            SELECT id_categoria FROM descendientes
            """, nativeQuery = true)
    List<Long> findAllDescendantIds(@Param("idRaiz") Long idRaiz);

    Optional<Categoria> findByNombre(String nombre);
}
