package org.restobar.gaira.acceso.service.usuario;

import java.util.List;

import org.restobar.gaira.acceso.dto.usuario.ProveedorResponse;
import org.restobar.gaira.acceso.entity.Proveedor;
import org.restobar.gaira.acceso.repository.ProveedorRepository;
import org.restobar.gaira.acceso.mapper.AutenticacionMapper;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@SuppressWarnings("null")
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    @Transactional(readOnly = true)
    public List<ProveedorResponse> findAll() {
        return proveedorRepository.findAll().stream()
                .map(AutenticacionMapper::toProveedorResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProveedorResponse findById(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Proveedor no encontrado"));
        return AutenticacionMapper.toProveedorResponse(proveedor);
    }
}
