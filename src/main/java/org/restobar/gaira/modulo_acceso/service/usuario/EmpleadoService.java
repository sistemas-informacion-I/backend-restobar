package org.restobar.gaira.acceso.service.usuario;

import java.util.List;

import org.restobar.gaira.acceso.dto.usuario.EmpleadoResponse;
import org.restobar.gaira.acceso.entity.Empleado;
import org.restobar.gaira.acceso.repository.EmpleadoRepository;
import org.restobar.gaira.acceso.mapper.AutenticacionMapper;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@SuppressWarnings("null")
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional(readOnly = true)
    public List<EmpleadoResponse> findAll() {
        return empleadoRepository.findAll().stream()
                .map(AutenticacionMapper::toEmpleadoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmpleadoResponse findById(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Empleado no encontrado"));
        return AutenticacionMapper.toEmpleadoResponse(empleado);
    }
}
