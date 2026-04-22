package org.restobar.gaira.modulo_acceso.service.usuario;

import java.util.List;
import java.util.Map;

import org.restobar.gaira.modulo_acceso.dto.usuario.ClienteResponse;
import org.restobar.gaira.modulo_acceso.entity.Cliente;
import org.restobar.gaira.modulo_acceso.mapper.usuario.ClienteMapper;
import org.restobar.gaira.modulo_acceso.repository.ClienteRepository;
import org.restobar.gaira.security.audit.util.AuditableService;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ClienteService implements AuditableService<Long, Object> {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Override
    public Object getEntity(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof Cliente c) {
            return clienteMapper.toAuditMap(c);
        } else if (entity instanceof ClienteResponse cr) {
            return clienteMapper.toAuditMap(cr);
        }
        return Map.of();
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> findAll() {
        return clienteRepository.findAll().stream()
                .map(clienteMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse findById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Cliente no encontrado"));
        return clienteMapper.toResponse(cliente);
    }
}
