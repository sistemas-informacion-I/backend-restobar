package org.restobar.gaira.modulo_operaciones.service.sucursal;

import java.util.Map;
import java.util.stream.Collectors;

import java.util.List;

import org.restobar.gaira.modulo_operaciones.dto.sucursal.SucursalResponseDTO;
import org.restobar.gaira.modulo_operaciones.dto.sucursal.SucursalRequestDTO;
import org.restobar.gaira.modulo_acceso.entity.Rol;
import org.restobar.gaira.modulo_acceso.entity.RolUsuario;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_acceso.repository.RolRepository;
import org.restobar.gaira.modulo_acceso.repository.RolUsuarioRepository;
import org.restobar.gaira.modulo_acceso.repository.UsuarioRepository;
import org.restobar.gaira.modulo_operaciones.entity.EmpleadoSucursal;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.restobar.gaira.modulo_operaciones.repository.EmpleadoSucursalRepository;
import org.restobar.gaira.modulo_operaciones.repository.SucursalRepository;
import org.restobar.gaira.modulo_operaciones.mapper.sucursal.SucursalMapper;
import org.restobar.gaira.security.audit.annotation.Auditable;
import org.restobar.gaira.security.audit.util.AuditableService;
import org.restobar.gaira.security.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service // esta clase es un Service, regístrala y adminístrala tú
@RequiredArgsConstructor // Genera automáticamente un constructor con todos los campos que son final
public class SucursalService implements AuditableService<Long, Object> {

    private final SucursalRepository sucursalRepository;
    private final SucursalMapper sucursalMapper;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final EmpleadoSucursalRepository empleadoSucursalRepository;
    private final SecurityUtils securityUtils;

    @Override
    public Object getEntity(Long id) {
        return sucursalRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof Sucursal s) {
            return sucursalMapper.toAuditMap(s);
        } else if (entity instanceof SucursalResponseDTO sr) {
            return sucursalMapper.toAuditMap(sr);
        }
        return Map.of();
    }

    @Transactional(readOnly = true)
    public List<SucursalResponseDTO> listarTodas() {
        Usuario actor = securityUtils.getCurrentUser();
        
        // Si es SUPERUSER, ve todas
        if (actor != null && "S".equals(actor.getTipoUsuario())) {
            return sucursalRepository.findByActivoTrue()
                    .stream()
                    .map(sucursalMapper::toResponseDTO)
                    .collect(Collectors.toList());
        }

        // Si es ADMIN (E), solo ve las suyas
        if (actor != null && "E".equals(actor.getTipoUsuario())) {
            return actor.getEmpleado().getEmpleadoSucursales().stream()
                    .filter(es -> Boolean.TRUE.equals(es.getActivo()))
                    .map(EmpleadoSucursal::getSucursal)
                    .filter(Sucursal::getActivo)
                    .map(sucursalMapper::toResponseDTO)
                    .collect(Collectors.toList());
        }

        return List.of();
    }

    @Transactional(readOnly = true)
    public SucursalResponseDTO obtenerPorId(Long id) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada"));

        validarPropiedadSucursal(sucursal);

        return sucursalMapper.toResponseDTO(sucursal);
    }

    @Transactional
    @Auditable(tabla = "sucursal", operacion = "INSERT")
    public SucursalResponseDTO crear(SucursalRequestDTO dto) {
        validarSuperusuario();

        if (sucursalRepository.existsByCorreo(dto.getCorreo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe una sucursal con ese correo");
        }

        // 1. Crear Sucursal
        Sucursal sucursal = sucursalMapper.toEntity(dto);
        sucursal = sucursalRepository.save(sucursal);

        // 2. Asignar Responsable si se proporciona
        if (dto.getIdUsuarioResponsable() != null) {
            asignarResponsable(sucursal, dto.getIdUsuarioResponsable());
        }

        return sucursalMapper.toResponseDTO(sucursal);
    }

    @Transactional
    @Auditable(tabla = "sucursal", operacion = "UPDATE", idParamName = "id")
    public SucursalResponseDTO actualizar(Long id, SucursalRequestDTO dto) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada"));

        validarPropiedadSucursal(sucursal);

        sucursal.setNombre(dto.getNombre());
        sucursal.setDireccion(dto.getDireccion());
        sucursal.setTelefono(dto.getTelefono());
        sucursal.setCorreo(dto.getCorreo());
        sucursal.setHorarioApertura(dto.getHorarioApertura());
        sucursal.setHorarioCierre(dto.getHorarioCierre());
        sucursal.setCiudad(dto.getCiudad());
        sucursal.setDepartamento(dto.getDepartamento());

        // Solo Superusuario puede cambiar el estado operativo o el responsable
        if (securityUtils.getCurrentUser().getTipoUsuario().equals("S")) {
            if (dto.getEstadoOperativo() != null) {
                sucursal.setEstadoOperativo(dto.getEstadoOperativo());
            }
            if (dto.getIdUsuarioResponsable() != null) {
                actualizarResponsable(sucursal, dto.getIdUsuarioResponsable());
            }
        }

        return sucursalMapper.toResponseDTO(sucursalRepository.save(sucursal));
    }

    @Transactional
    @Auditable(tabla = "sucursal", operacion = "UPDATE", idParamName = "id")
    public void eliminar(Long id) {
        validarSuperusuario();
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada"));
        sucursal.setActivo(false);
        sucursalRepository.save(sucursal);
    }

    // HELPERS DE SEGURIDAD Y NEGOCIO

    private void validarSuperusuario() {
        Usuario actor = securityUtils.getCurrentUser();
        if (actor == null || !"S".equals(actor.getTipoUsuario())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acción reservada para Superusuario");
        }
    }

    private void validarPropiedadSucursal(Sucursal sucursal) {
        Usuario actor = securityUtils.getCurrentUser();
        if (actor == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        
        if ("S".equals(actor.getTipoUsuario())) return;

        boolean esPropia = actor.getEmpleado().getEmpleadoSucursales().stream()
                .anyMatch(es -> es.getSucursal().getIdSucursal().equals(sucursal.getIdSucursal()) && Boolean.TRUE.equals(es.getActivo()));
        
        if (!esPropia) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes acceso a esta sucursal");
        }
    }

    private void asignarResponsable(Sucursal sucursal, Long idUsuarioResponsable) {
        Usuario usuario = usuarioRepository.findById(idUsuarioResponsable)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario responsable no encontrado"));

        if (!"E".equals(usuario.getTipoUsuario())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El responsable debe ser tipo Empleado");
        }

        // Crear relación EmpleadoSucursal
        EmpleadoSucursal es = EmpleadoSucursal.builder()
                .empleado(usuario.getEmpleado())
                .sucursal(sucursal)
                .activo(true)
                .build();
        empleadoSucursalRepository.save(es);

        // ASIGNACIÓN AUTOMÁTICA DE ROL ADMIN
        Rol rolAdmin = rolRepository.findByNombre("ADMIN")
                .orElseThrow(() -> new RuntimeException("Error: Rol ADMIN no encontrado en el sistema"));

        boolean yaTieneAdmin = usuario.getRolesUsuario().stream()
                .anyMatch(ru -> ru.getRol().getNombre().equals("ADMIN") && Boolean.TRUE.equals(ru.getActivo()));

        if (!yaTieneAdmin) {
            RolUsuario ru = RolUsuario.builder()
                    .usuario(usuario)
                    .rol(rolAdmin)
                    .activo(true)
                    .build();
            rolUsuarioRepository.save(ru);
        }
    }

    private void actualizarResponsable(Sucursal sucursal, Long idUsuarioResponsable) {
        // Desactivar responsable actual si existe
        sucursal.getEmpleadoSucursales().stream()
                .filter(es -> Boolean.TRUE.equals(es.getActivo()))
                .forEach(es -> {
                    es.setActivo(false);
                    empleadoSucursalRepository.save(es);
                });

        asignarResponsable(sucursal, idUsuarioResponsable);
    }
}

