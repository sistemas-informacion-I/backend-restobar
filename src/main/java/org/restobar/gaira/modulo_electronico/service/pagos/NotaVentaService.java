package org.restobar.gaira.modulo_electronico.service.pagos;

import lombok.RequiredArgsConstructor;
import org.restobar.gaira.modulo_comercial.entity.NotaVenta;
import org.restobar.gaira.modulo_comercial.repository.NotaVentaRepository;
import org.restobar.gaira.modulo_electronico.mapper.pago.NotaVentaMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("electronicoNotaVentaService")
@RequiredArgsConstructor
public class NotaVentaService {

    private final NotaVentaRepository notaVentaRepository;
    private final NotaVentaMapper notaVentaMapper;

    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerMisPedidos(String username) {
        List<NotaVenta> notasVenta = notaVentaRepository.findByClienteUsername(username);

        return notasVenta.stream()
                .map(notaVentaMapper::toResponseMap)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerPorId(Long id) {
        NotaVenta notaVenta = notaVentaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Nota de venta no encontrada"));

        return notaVentaMapper.toResponseMap(notaVenta);
    }
}
