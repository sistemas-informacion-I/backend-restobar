package org.restobar.gaira.modulo_inventario.repository;

import java.util.Optional;

import org.restobar.gaira.modulo_inventario.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    Optional<Inventario> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}
