package org.restobar.gaira.acceso.service.auditoria;

import org.restobar.gaira.acceso.dto.auditoria.LogAuditoriaResponse;
import org.restobar.gaira.acceso.repository.LogAuditoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.restobar.gaira.acceso.mapper.AutenticacionMapper;
@Service
public class LogAuditoriaService {

    private final LogAuditoriaRepository logAuditoriaRepository;

    public LogAuditoriaService(LogAuditoriaRepository logAuditoriaRepository) {
        this.logAuditoriaRepository = logAuditoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<LogAuditoriaResponse> latest() {
        return logAuditoriaRepository.findTop100ByOrderByFechaOperacionDesc().stream()
                .map(AutenticacionMapper::toLogAuditoriaResponse)
                .toList();
    }
}
