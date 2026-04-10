package org.restobar.gaira.modulo_acceso.service.sesion;

import java.time.LocalDateTime;
import java.util.List;

import org.restobar.gaira.modulo_acceso.dto.sesion.SesionResponse;
import org.restobar.gaira.modulo_acceso.entity.Sesion;
import org.restobar.gaira.modulo_acceso.repository.SesionRepository;
import org.restobar.gaira.modulo_acceso.mapper.AutenticacionMapper;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@SuppressWarnings("null")
public class SesionService {

    private final SesionRepository sesionRepository;

    public SesionService(SesionRepository sesionRepository) {
        this.sesionRepository = sesionRepository;
    }

    @Transactional(readOnly = true)
    public List<SesionResponse> findByUsuario(Long idUsuario) {
        return sesionRepository.findByUsuario_IdUsuarioOrderByFechaInicioDesc(idUsuario).stream()
                .map(AutenticacionMapper::toSesionResponse)
                .toList();
    }

    /**
     * Cierra/revoca una sesión estableciendo fecha_cierre.
     * Una sesión cerrada (fecha_cierre != NULL) ya no puede ser usada.
     */
    @Transactional
    public void revoke(Long idSesion) {
        Sesion sesion = sesionRepository.findById(idSesion)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Sesión no encontrada"));
        sesion.setFechaCierre(LocalDateTime.now());
        sesionRepository.save(sesion);
    }
}
