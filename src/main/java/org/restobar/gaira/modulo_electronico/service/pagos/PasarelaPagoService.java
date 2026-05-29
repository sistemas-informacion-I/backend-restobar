package org.restobar.gaira.modulo_electronico.service.pagos;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.restobar.gaira.modulo_electronico.dto.pago.IniciarTransaccionOnlineRequest;
import org.restobar.gaira.modulo_electronico.dto.pago.MetodoPagoResponse;
import org.restobar.gaira.modulo_electronico.dto.pago.MetodoPagoUpdateRequest;
import org.restobar.gaira.modulo_electronico.dto.pago.TransaccionOnlineResponse;
import org.restobar.gaira.modulo_electronico.dto.pago.WebhookTransaccionOnlineRequest;
import org.restobar.gaira.modulo_electronico.mapper.pago.PasarelaPagoMapper;
import org.restobar.gaira.modulo_comercial.entity.NotaVenta;
import org.restobar.gaira.modulo_electronico.repository.MetodoPagoRepository;
import org.restobar.gaira.modulo_comercial.repository.NotaVentaRepository;
import org.restobar.gaira.modulo_electronico.entity.TransaccionOnline;
import org.restobar.gaira.modulo_electronico.repository.TransaccionOnlineRepository;
import org.restobar.gaira.modulo_electronico.entity.MetodoPago;
import org.restobar.gaira.modulo_operaciones.entity.Comanda;
import org.restobar.gaira.modulo_operaciones.repository.ComandaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasarelaPagoService {

    private final MetodoPagoRepository metodoPagoRepository;
    private final NotaVentaRepository notaVentaRepository;
    private final ComandaRepository comandaRepository;
    private final TransaccionOnlineRepository transaccionOnlineRepository;
    private final PasarelaPagoMapper pasarelaPagoMapper;

    @Value("${payment.webhook-secret:}")
    private String webhookSecret;

    @Transactional(readOnly = true)
    public List<MetodoPagoResponse> listarMetodos(Boolean soloActivos, Boolean soloOnline) {
        Stream<MetodoPago> metodos = Boolean.TRUE.equals(soloActivos)
                ? metodoPagoRepository.findByActivoTrue().stream()
                : metodoPagoRepository.findAll().stream();

        if (Boolean.TRUE.equals(soloOnline)) {
            metodos = metodos.filter(this::esMetodoOnline);
        }

        return metodos
                .map(pasarelaPagoMapper::toMetodoPagoResponse)
                .toList();
    }

    @Transactional
    public MetodoPagoResponse actualizarMetodoPago(Long id, MetodoPagoUpdateRequest request) {
        MetodoPago metodoPago = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Método de pago no encontrado"));

        if (request.descripcion() != null) {
            metodoPago.setDescripcion(request.descripcion());
        }
        if (request.comisionPorcentaje() != null) {
            metodoPago.setComisionPorcentaje(request.comisionPorcentaje());
        }
        if (request.comisionFija() != null) {
            metodoPago.setComisionFija(request.comisionFija());
        }
        if (request.activo() != null) {
            metodoPago.setActivo(request.activo());
        }

        metodoPago = metodoPagoRepository.save(metodoPago);
        return pasarelaPagoMapper.toMetodoPagoResponse(metodoPago);
    }

    @Transactional(readOnly = true)
    public TransaccionOnlineResponse buscarTransaccion(Long idTransaccion) {
        return transaccionOnlineRepository.findById(idTransaccion)
                .map(pasarelaPagoMapper::toTransaccionResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transacción no encontrada"));
    }

    @Transactional
    public TransaccionOnlineResponse iniciarTransaccion(IniciarTransaccionOnlineRequest request) {
        NotaVenta notaVenta = notaVentaRepository.findById(request.idNotaVenta())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nota de venta no encontrada"));

        if (!NotaVenta.EstadoNotaVenta.EMITIDA.name().equalsIgnoreCase(notaVenta.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Solo se puede iniciar una transacción para una nota de venta emitida y pendiente de pago");
        }

        MetodoPago metodoPago = metodoPagoRepository.findById(request.idMetodoPago())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Método de pago no encontrado"));

        if (!Boolean.TRUE.equals(metodoPago.getActivo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El método de pago está inactivo");
        }

        if (transaccionOnlineRepository.findByNumeroTransaccion(request.numeroTransaccion()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una transacción con ese número");
        }

        TransaccionOnline transaccion = TransaccionOnline.builder()
                .notaVenta(notaVenta)
                .numeroTransaccion(request.numeroTransaccion())
                .monto(request.monto())
                .moneda(StringUtils.hasText(request.moneda()) ? request.moneda().trim() : "BOB")
                .estado(TransaccionOnline.EstadoTransaccion.PENDIENTE.name())
                .datosAdicionales(request.datosAdicionales())
                .build();

        transaccion = transaccionOnlineRepository.save(transaccion);
        return pasarelaPagoMapper.toTransaccionResponse(transaccion);
    }

    @Transactional
    public TransaccionOnlineResponse procesarWebhook(Long idTransaccion,
            WebhookTransaccionOnlineRequest request,
            String firma) {
        TransaccionOnline transaccion = transaccionOnlineRepository.findById(idTransaccion)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transacción no encontrada"));

        validarFirmaWebhook(transaccion, request, firma);

        TransaccionOnline.EstadoTransaccion nuevoEstado;
        try {
            nuevoEstado = TransaccionOnline.EstadoTransaccion.valueOf(request.estado().trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado de transacción inválido");
        }

        if (esEstadoTerminal(transaccion.getEstado())) {
            if (transaccion.getEstado().equalsIgnoreCase(nuevoEstado.name())) {
                return pasarelaPagoMapper.toTransaccionResponse(transaccion);
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La transacción ya está cerrada y no admite cambios");
        }

        transaccion.setEstado(nuevoEstado.name());
        transaccion.setCodigoAutorizacion(request.codigoAutorizacion());
        transaccion.setCodigoError(request.codigoError());
        if (request.datosAdicionales() != null) {
            transaccion.setDatosAdicionales(request.datosAdicionales());
        }

        if (nuevoEstado == TransaccionOnline.EstadoTransaccion.APROBADA) {
            LocalDateTime momentoCierre = LocalDateTime.now();
            transaccion.setFechaCompletado(momentoCierre);

            NotaVenta notaVenta = transaccion.getNotaVenta();
            notaVenta.setEstado(NotaVenta.EstadoNotaVenta.PAGADA.name());
            if (notaVenta.getFechaPago() == null) {
                notaVenta.setFechaPago(momentoCierre);
            }

            Comanda comanda = notaVenta.getComanda();
            comanda.setEstado(Comanda.EstadoComanda.ABIERTA.name());

            notaVentaRepository.save(notaVenta);
            comandaRepository.save(comanda);
        } else if (esEstadoTerminal(nuevoEstado.name())) {
            transaccion.setFechaCompletado(LocalDateTime.now());
        }

        transaccion = transaccionOnlineRepository.save(transaccion);
        return pasarelaPagoMapper.toTransaccionResponse(transaccion);
    }

    private void validarFirmaWebhook(TransaccionOnline transaccion,
            WebhookTransaccionOnlineRequest request,
            String firma) {
        if (!StringUtils.hasText(webhookSecret)) {
            return;
        }

        String firmaEsperada = calcularFirma(transaccion.getIdTransaccion(), request.estado(),
                transaccion.getNumeroTransaccion());
        if (!StringUtils.hasText(firma) || !MessageDigest.isEqual(
                firmaEsperada.getBytes(StandardCharsets.UTF_8), firma.trim().getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Firma de webhook inválida");
        }
    }

    private String calcularFirma(Long idTransaccion, String estado, String numeroTransaccion) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] raw = mac.doFinal((idTransaccion + ":" + estado + ":" + numeroTransaccion)
                    .getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(raw.length * 2);
            for (byte b : raw) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo validar la firma del webhook", ex);
        }
    }

    private boolean esEstadoTerminal(String estado) {
        return TransaccionOnline.EstadoTransaccion.APROBADA.name().equalsIgnoreCase(estado)
                || TransaccionOnline.EstadoTransaccion.RECHAZADA.name().equalsIgnoreCase(estado)
                || TransaccionOnline.EstadoTransaccion.REEMBOLSADA.name().equalsIgnoreCase(estado)
                || TransaccionOnline.EstadoTransaccion.CANCELADA.name().equalsIgnoreCase(estado);
    }

    private boolean esMetodoOnline(MetodoPago metodoPago) {
        String nombre = metodoPago.getNombre();
        if (!StringUtils.hasText(nombre)) {
            return true;
        }
        return !nombre.trim().equalsIgnoreCase("efectivo");
    }
}