package org.restobar.gaira.modulo_operaciones.service.comanda;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.restobar.gaira.modulo_acceso.entity.Cliente;
import org.restobar.gaira.modulo_operaciones.entity.Comanda;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.restobar.gaira.modulo_operaciones.repository.ComandaRepository;
import org.restobar.gaira.modulo_operaciones.repository.SucursalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComandaService {

    private final ComandaRepository comandaRepository;
    private final SucursalRepository sucursalRepository;

    @Transactional
    public Comanda crearOnlineDesdeCarrito(
            Long idCarrito,
            Long idCliente,
            Long idSucursal,
            Map<Long, BigDecimal> precios,
            List<ItemData> items,
            Cliente cliente) {

        Sucursal sucursal = sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> new IllegalArgumentException("Sucursal no encontrada: " + idSucursal));

        String numeroComanda = generarNumeroComanda();

        Comanda comanda = Comanda.builder()
                .numeroComanda(numeroComanda)
                .sucursal(sucursal)
                .cliente(cliente)
                .empleado(null)
                .tipoServicio(Comanda.TipoServicio.ONLINE.name())
                .estado(Comanda.EstadoComanda.PENDIENTE_PAGO.name())
                .idCarrito(idCarrito)
                .fechaApertura(LocalDateTime.now())
                .build();

        comanda = comandaRepository.save(comanda);
        log.info("Comanda ONLINE {} creada para carrito {} (cliente {})", numeroComanda, idCarrito, idCliente);
        return comanda;
    }

    private String generarNumeroComanda() {
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long random = System.currentTimeMillis() % 10000;
        return "CMD-" + fecha + "-" + String.format("%04d", random);
    }

    public record ItemData(Long idProductoFinal, Integer cantidad, BigDecimal precioUnitario, String notas) {}
}