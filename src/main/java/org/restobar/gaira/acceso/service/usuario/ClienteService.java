package org.restobar.gaira.acceso.service.usuario;

import java.util.List;

import org.restobar.gaira.acceso.dto.usuario.ClienteResponse;
import org.restobar.gaira.acceso.entity.Cliente;
import org.restobar.gaira.acceso.repository.ClienteRepository;
import org.restobar.gaira.acceso.mapper.AutenticacionMapper;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@SuppressWarnings("null")
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> findAll() {
        return clienteRepository.findAll().stream()
                .map(AutenticacionMapper::toClienteResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse findById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Cliente no encontrado"));
        return AutenticacionMapper.toClienteResponse(cliente);
    }
}
