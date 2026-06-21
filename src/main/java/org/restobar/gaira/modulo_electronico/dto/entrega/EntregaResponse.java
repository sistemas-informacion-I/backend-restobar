package org.restobar.gaira.modulo_electronico.dto.entrega;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EntregaResponse(
        Long idEntrega,
        Long idComanda,
        String numeroComanda,
        Long idEmpleado,
        String nombreEmpleado,
        String direccionEntrega,
        BigDecimal latitud,
        BigDecimal longitud,
        BigDecimal latitudActual,
        BigDecimal longitudActual,
        BigDecimal distanciaKm,
        Integer tiempoEstimadoMin,
        BigDecimal costoEnvio,
        String estado,
        LocalDateTime fechaAsignacion,
        LocalDateTime fechaEntrega,
        String observaciones,
        Long idSucursal,
        String nombreSucursal,
        String direccionSucursal,
        BigDecimal sucursalLatitud,
        BigDecimal sucursalLongitud,
        String nombreCliente,
        String telefonoCliente
) {}
