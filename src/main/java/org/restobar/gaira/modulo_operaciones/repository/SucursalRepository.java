package org.restobar.gaira.modulo_operaciones.repository;


import java.util.List;
import java.util.Optional;

import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;





// aun falta pulir, es lo basico
@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
    

    List<Sucursal> findByActivoTrue();

    Optional<Sucursal> findByCorreo(String correo);

    boolean existsByCorreo(String correo);
     // añadir a la necesidad

}
