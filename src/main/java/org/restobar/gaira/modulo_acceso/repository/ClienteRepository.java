package org.restobar.gaira.modulo_acceso.repository;

import org.restobar.gaira.modulo_acceso.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByUsuario_IdUsuario(Long idUsuario);

    Optional<Cliente> findByNit(String nit);
}