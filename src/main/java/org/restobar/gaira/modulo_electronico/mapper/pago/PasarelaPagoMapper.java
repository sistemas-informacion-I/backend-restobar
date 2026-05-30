package org.restobar.gaira.modulo_electronico.mapper.pago;

import org.restobar.gaira.modulo_electronico.dto.pago.MetodoPagoResponse;
import org.restobar.gaira.modulo_electronico.dto.pago.TransaccionOnlineResponse;
import org.restobar.gaira.modulo_electronico.entity.MetodoPago;
import org.restobar.gaira.modulo_electronico.entity.TransaccionOnline;
import org.restobar.gaira.modulo_comercial.entity.NotaVenta;
import org.restobar.gaira.modulo_operaciones.entity.Comanda;
import org.springframework.stereotype.Component;

@Component
public class PasarelaPagoMapper {

    public MetodoPagoResponse toMetodoPagoResponse(MetodoPago metodoPago) {
        if (metodoPago == null) return null;

        return new MetodoPagoResponse(
                metodoPago.getIdMetodoPago(),
                metodoPago.getNombre(),
                metodoPago.getDescripcion(),
                metodoPago.getComisionPorcentaje(),
                metodoPago.getComisionFija(),
                metodoPago.getActivo());
    }

    public TransaccionOnlineResponse toTransaccionResponse(TransaccionOnline transaccion) {
        if (transaccion == null) return null;

        NotaVenta notaVenta = transaccion.getNotaVenta();
        Comanda comanda = notaVenta != null ? notaVenta.getComanda() : null;
        MetodoPago metodoPago = notaVenta != null ? notaVenta.getMetodoPago() : null;

        return new TransaccionOnlineResponse(
                transaccion.getIdTransaccion(),
                notaVenta != null ? notaVenta.getIdNotaVenta() : null,
                comanda != null ? comanda.getIdComanda() : null,
                metodoPago != null ? metodoPago.getIdMetodoPago() : null,
                transaccion.getNumeroTransaccion(),
                transaccion.getMonto(),
                transaccion.getMoneda(),
                transaccion.getEstado(),
                transaccion.getFechaInicio(),
                transaccion.getFechaCompletado(),
                transaccion.getCodigoAutorizacion(),
                transaccion.getCodigoError(),
                transaccion.getDatosAdicionales(),
                notaVenta != null && notaVenta.getEstado() != null ? notaVenta.getEstado().name() : null,
                comanda != null ? comanda.getEstado() : null,
                metodoPago != null ? metodoPago.getNombre() : null);
    }
}
