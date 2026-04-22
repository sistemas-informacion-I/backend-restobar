package org.restobar.gaira.modulo_acceso.service.register;

import java.time.LocalDateTime;
import java.util.UUID;

import org.restobar.gaira.modulo_acceso.dto.login.LoginResponse;
import org.restobar.gaira.modulo_acceso.dto.register.RegisterRequest;
import org.restobar.gaira.modulo_acceso.entity.Rol;
import org.restobar.gaira.modulo_acceso.entity.RolUsuario;
import org.restobar.gaira.modulo_acceso.entity.Sesion;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_acceso.mapper.usuario.UsuarioMapper;
import org.restobar.gaira.modulo_acceso.repository.RolRepository;
import org.restobar.gaira.modulo_acceso.repository.RolUsuarioRepository;
import org.restobar.gaira.modulo_acceso.repository.UsuarioRepository;
import org.restobar.gaira.modulo_acceso.repository.login.SesionRepository;
import org.restobar.gaira.security.audit.service.LogAuditoriaService;
import org.restobar.gaira.security.jwt.JwtService;
import org.restobar.gaira.security.userdetails.ApplicationUserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

@Service
@Transactional
@SuppressWarnings("null")
public class RegisterService {

    private static final String ESTADO_HABILITADO = "HABILITADO";

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final SesionRepository sesionRepository;
    private final LogAuditoriaService logAuditoriaService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UsuarioMapper usuarioMapper;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${auth.refresh-expiration-seconds}")
    private long refreshExpirationSeconds;

    public RegisterService(UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            RolUsuarioRepository rolUsuarioRepository,
            SesionRepository sesionRepository,
            LogAuditoriaService logAuditoriaService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.rolUsuarioRepository = rolUsuarioRepository;
        this.sesionRepository = sesionRepository;
        this.logAuditoriaService = logAuditoriaService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.usuarioMapper = usuarioMapper;
    }

    @Transactional
    public LoginResponse register(RegisterRequest request, HttpServletRequest httpRequest) {
        if (usuarioRepository.existsByCi(request.ci())) {
            throw new ResponseStatusException(CONFLICT, "CI ya registrado");
        }
        if (usuarioRepository.existsByCorreo(request.correo())) {
            throw new ResponseStatusException(CONFLICT, "Correo ya registrado");
        }
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(CONFLICT, "Username ya registrado");
        }

        Usuario usuario = Usuario.builder()
                .ci(request.ci())
                .nombre(request.nombre())
                .apellido(request.apellido())
                .username(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                .telefono(request.telefono())
                .sexo(request.sexo())
                .correo(request.correo())
                .direccion(request.direccion())
                .intentosFallidos(0)
                .estadoAcceso(ESTADO_HABILITADO)
                .activo(true)
                .build();
        usuarioRepository.save(usuario);

        String roleName = (request.rol() == null || request.rol().isBlank()) ? "USER" : request.rol().trim();
        Rol rol = rolRepository.findByNombre(roleName)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Rol no encontrado: " + roleName));

        RolUsuario rolUsuario = RolUsuario.builder()
                .usuario(usuario)
                .rol(rol)
                .activo(true)
                .build();
        rolUsuarioRepository.save(rolUsuario);

        Usuario usuarioConAuthorities = usuarioRepository
                .findActiveByUsernameWithAuthorities(request.username())
                .orElseThrow(() -> new ResponseStatusException(INTERNAL_SERVER_ERROR,
                        "No se pudo cargar usuario registrado"));

        ApplicationUserPrincipal principal = ApplicationUserPrincipal.from(usuarioConAuthorities);
        String accessToken = jwtService.generateToken(principal);
        String refreshToken = UUID.randomUUID().toString();

        createSession(usuario, accessToken, refreshToken, httpRequest);
        logAuditoriaService.logAcceso(usuario, "usuario", "INSERT", httpRequest, "registro_exitoso");

        return new LoginResponse(
                accessToken,
                refreshToken,
                usuarioMapper.toResponse(usuario),
                usuario.getUsername(),
                principal.getAuthorities().stream().map(a -> a.getAuthority()).toList());
    }

    private void createSession(Usuario usuario, String accessToken, String refreshToken, HttpServletRequest request) {
        Sesion sesion = Sesion.builder()
                .usuario(usuario)
                .tokenSesion(accessToken)
                .refreshToken(refreshToken)
                .fechaExpiracion(LocalDateTime.now().plusSeconds(jwtExpiration / 1000))
                .refreshExpiracion(LocalDateTime.now().plusSeconds(refreshExpirationSeconds))
                .ipOrigen(extractIp(request))
                .userAgent(request.getHeader("User-Agent"))
                .build();
        sesionRepository.save(sesion);
    }

    private String extractIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
