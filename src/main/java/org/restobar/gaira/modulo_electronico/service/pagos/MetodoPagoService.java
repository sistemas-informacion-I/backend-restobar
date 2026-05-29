package org.restobar.gaira.modulo_electronico.service.pagos;

import lombok.RequiredArgsConstructor;
import org.restobar.gaira.modulo_electronico.entity.MetodoPago;
import org.restobar.gaira.modulo_electronico.repository.MetodoPagoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MetodoPagoService {

    private final MetodoPagoRepository metodoPagoRepository;

    @Transactional(readOnly = true)
    public List<MetodoPago> getAll() {
        return metodoPagoRepository.findAll();
    }

    @Transactional
    public MetodoPago update(Long id, Map<String, Object> body) {
        MetodoPago metodo = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Método de pago no encontrado"));

        if (body.containsKey("descripcion")) {
            metodo.setDescripcion((String) body.get("descripcion"));
        }
        if (body.containsKey("comisionPorcentaje")) {
            metodo.setComisionPorcentaje(new BigDecimal(body.get("comisionPorcentaje").toString()));
        }
        if (body.containsKey("comisionFija")) {
            metodo.setComisionFija(new BigDecimal(body.get("comisionFija").toString()));
        }
        if (body.containsKey("activo")) {
            metodo.setActivo((Boolean) body.get("activo"));
        }

        return metodoPagoRepository.save(metodo);
    }
}
