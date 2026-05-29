package org.restobar.gaira.modulo_comercial.service;

import java.math.BigDecimal;
import java.util.List;

import org.restobar.gaira.modulo_comercial.entity.DetalleNotaVenta;
import org.restobar.gaira.modulo_comercial.entity.NotaVenta;
import org.restobar.gaira.modulo_comercial.entity.ProductoFinal;
import org.restobar.gaira.modulo_comercial.repository.DetalleNotaVentaRepository;
import org.restobar.gaira.modulo_comercial.repository.NotaVentaRepository;
import org.restobar.gaira.modulo_comercial.repository.ProductoFinalRepository;
import org.restobar.gaira.modulo_electronico.entity.MetodoPago;
import org.restobar.gaira.modulo_electronico.repository.MetodoPagoRepository;
import org.restobar.gaira.modulo_operaciones.entity.Comanda;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.restobar.gaira.modulo_operaciones.repository.SucursalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotaVentaService {

    private final NotaVentaRepository notaVentaRepository;
    private final DetalleNotaVentaRepository detalleNotaVentaRepository;
    private final ProductoFinalRepository productoFinalRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final SucursalRepository sucursalRepository;

    @Transactional
    public NotaVenta crearDesdeComanda(
            Comanda comanda,
            Long idSucursal,
            Long idMetodoPago,
            BigDecimal subtotal,
            BigDecimal descuento,
            BigDecimal impuesto,
            BigDecimal total,
            List<ItemData> items) {

        MetodoPago metodoPago = metodoPagoRepository.findById(idMetodoPago)
                .orElseThrow(() -> new IllegalArgumentException("Método de pago no encontrado: " + idMetodoPago));

        Sucursal sucursal = sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> new IllegalArgumentException("Sucursal no encontrada: " + idSucursal));

        NotaVenta notaVenta = NotaVenta.builder()
                .comanda(comanda)
                .sucursal(sucursal)
                .cliente(comanda.getCliente())
                .metodoPago(metodoPago)
                .subtotal(subtotal)
                .descuento(descuento)
                .impuesto(impuesto)
                .propina(BigDecimal.ZERO)
                .total(total)
                .estado(NotaVenta.EstadoNotaVenta.EMITIDA.name())
                .build();

        notaVenta = notaVentaRepository.save(notaVenta);

        for (ItemData item : items) {
            ProductoFinal producto = productoFinalRepository.findById(item.idProductoFinal())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + item.idProductoFinal()));

            BigDecimal costoUnitario = obtenerCostoUnitario(item.idProductoFinal(), idSucursal);
            BigDecimal itemSubtotal = item.precioUnitario().multiply(BigDecimal.valueOf(item.cantidad()));

            DetalleNotaVenta detalle = DetalleNotaVenta.builder()
                    .notaVenta(notaVenta)
                    .productoFinal(producto)
                    .cantidad(item.cantidad())
                    .precioUnitario(item.precioUnitario())
                    .costoUnitario(costoUnitario)
                    .descuento(BigDecimal.ZERO)
                    .subtotal(itemSubtotal)
                    .descripcion(item.notas())
                    .build();

            detalleNotaVentaRepository.save(detalle);
        }

        log.info("NotaVenta {} creada desde comanda {} con {} items",
                notaVenta.getIdNotaVenta(), comanda.getNumeroComanda(), items.size());
        return notaVenta;
    }

    private BigDecimal obtenerCostoUnitario(Long idProductoFinal, Long idSucursal) {
        return BigDecimal.ZERO;
    }

    public record ItemData(Long idProductoFinal, Integer cantidad, BigDecimal precioUnitario, String notas) {}
}