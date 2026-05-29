package org.restobar.gaira.modulo_electronico.dto.pago;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public record TransaccionOnlineResponse(
        Long idTransaccion,
        Long idNotaVenta,
        Long idComanda,
        Long idMetodoPago,
        String numeroTransaccion,
        BigDecimal monto,
        String moneda,
        String estado,
        LocalDateTime fechaInicio,
        LocalDateTime fechaCompletado,
        String codigoAutorizacion,
        String codigoError,
        Map<String, Object> datosAdicionales,
        String estadoNotaVenta,
        String estadoComanda,
        String metodoPagoNombre) {
}