package org.restobar.gaira.modulo_inventario.repository;

import org.restobar.gaira.modulo_inventario.entity.NotaSalida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotaSalidaRepository extends JpaRepository<NotaSalida, Long> {

    Page<NotaSalida> findBySucursalIdSucursal(Long idSucursal, Pageable pageable);
    Page<NotaSalida> findBySucursalIdSucursalAndTipoGasto(Long idSucursal, NotaSalida.TipoGasto tipoGasto, Pageable pageable);
    Page<NotaSalida> findBySucursalIdSucursalAndEstado(Long idSucursal, NotaSalida.EstadoNota estado, Pageable pageable);
    Page<NotaSalida> findBySucursalIdSucursalAndTipoGastoAndEstado(Long idSucursal, NotaSalida.TipoGasto tipoGasto, NotaSalida.EstadoNota estado, Pageable pageable);
}
