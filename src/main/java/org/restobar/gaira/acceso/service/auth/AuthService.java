package org.restobar.gaira.acceso.service.auth;

import java.time.LocalDateTime;
import java.util.UUID;

import org.restobar.gaira.acceso.dto.auth.AuthLoginRequest;
import org.restobar.gaira.acceso.dto.auth.AuthRegisterRequest;
import org.restobar.gaira.acceso.dto.auth.AuthResponse;
import org.restobar.gaira.acceso.dto.auth.RefreshTokenRequest;
import org.restobar.gaira.acceso.entity.LogAuditoria;
import org.restobar.gaira.acceso.entity.Rol;
import org.restobar.gaira.acceso.entity.RolUsuario;
import org.restobar.gaira.acceso.entity.Sesion;
import org.restobar.gaira.acceso.entity.Usuario;
import org.restobar.gaira.acceso.mapper.AutenticacionMapper;
import org.restobar.gaira.acceso.repository.LogAuditoriaRepository;
import org.restobar.gaira.acceso.repository.RolRepository;
import org.restobar.gaira.acceso.repository.RolUsuarioRepository;
import org.restobar.gaira.acceso.repository.SesionRepository;
import org.restobar.gaira.acceso.repository.UsuarioRepository;
import org.restobar.gaira.security.jwt.JwtService;
import org.restobar.gaira.security.userdetails.ApplicationUserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.LOCKED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

@Service
@Transactional
@SuppressWarnings("null")
public class AuthService {

    private static final String ESTADO_HABILITADO = "HABILITADO";
    private static final String ESTADO_BLOQUEADO = "BLOQUEADO";
    private static final String ESTADO_SUSPENDIDO = "SUSPENDIDO";

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final SesionRepository sesionRepository;
    private final LogAuditoriaRepository logAuditoriaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${auth.refresh-expiration-seconds}")
    private long refreshExpirationSeconds;

    @Value("${auth.max-failed-attempts}")
    private int maxFailedAttempts;

    public AuthService(UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            RolUsuarioRepository rolUsuarioRepository,
            SesionRepository sesionRepository,
            LogAuditoriaRepository logAuditoriaRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.rolUsuarioRepository = rolUsuarioRepository;
        this.sesionRepository = sesionRepository;
        this.logAuditoriaRepository = logAuditoriaRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(AuthRegisterRequest request, HttpServletRequest httpRequest) {
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
        logAcceso(usuario, "usuario", "INSERT", httpRequest, "registro_exitoso");

        return new AuthResponse(
                accessToken,
                refreshToken,
                AutenticacionMapper.toUsuarioResponse(usuario),
                usuario.getUsername(),
                principal.getAuthorities().stream().map(a -> a.getAuthority()).toList());
    }

    @Transactional
    public AuthResponse login(AuthLoginRequest request, HttpServletRequest httpRequest) {
        Usuario usuario = usuarioRepository.findActiveByUsernameWithAuthorities(request.username())
                .orElseThrow(() -> {
                    logAcceso(null, "usuario", "EJECUTAR", httpRequest, "usuario_no_existe");
                    return new ResponseStatusException(UNAUTHORIZED, "Credenciales inválidas");
                });

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            logAcceso(usuario, "usuario", "EJECUTAR", httpRequest, "usuario_inactivo");
            throw new ResponseStatusException(FORBIDDEN, "Usuario inactivo");
        }

        if (ESTADO_BLOQUEADO.equals(usuario.getEstadoAcceso())) {
            logAcceso(usuario, "usuario", "EJECUTAR", httpRequest, "cuenta_bloqueada");
            throw new ResponseStatusException(LOCKED, "Cuenta bloqueada");
        }

        if (ESTADO_SUSPENDIDO.equals(usuario.getEstadoAcceso())) {
            logAcceso(usuario, "usuario", "EJECUTAR", httpRequest, "cuenta_suspendida");
            throw new ResponseStatusException(FORBIDDEN, "Cuenta suspendida");
        }

        if (!passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            int intentos = usuario.getIntentosFallidos() == null ? 1 : usuario.getIntentosFallidos() + 1;
            usuario.setIntentosFallidos(intentos);

            if (intentos >= maxFailedAttempts) {
                usuario.setEstadoAcceso(ESTADO_BLOQUEADO);
            }

            usuarioRepository.save(usuario);
            logAcceso(usuario, "usuario", "EJECUTAR", httpRequest, "credenciales_invalidas");
            throw new ResponseStatusException(UNAUTHORIZED, "Credenciales inválidas");
        }

        // Login exitoso: resetear intentos
        usuario.setIntentosFallidos(0);
        if (ESTADO_BLOQUEADO.equals(usuario.getEstadoAcceso())) {
            usuario.setEstadoAcceso(ESTADO_HABILITADO);
        }
        usuarioRepository.save(usuario);

        ApplicationUserPrincipal principal = ApplicationUserPrincipal.from(usuario);
        String accessToken = jwtService.generateToken(principal);
        String refreshToken = UUID.randomUUID().toString();

        createSession(usuario, accessToken, refreshToken, httpRequest);
        logAcceso(usuario, "usuario", "EJECUTAR", httpRequest, "login_exitoso");

        return new AuthResponse(
                accessToken,
                refreshToken,
                AutenticacionMapper.toUsuarioResponse(usuario),
                usuario.getUsername(),
                principal.getAuthorities().stream().map(a -> a.getAuthority()).toList());
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request, HttpServletRequest httpRequest) {
        Sesion sesion = sesionRepository.findByRefreshTokenAndFechaCierreIsNull(request.refreshToken())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Refresh token inválido o sesión cerrada"));

        if (sesion.getRefreshExpiracion() != null && sesion.getRefreshExpiracion().isBefore(LocalDateTime.now())) {
            sesion.setFechaCierre(LocalDateTime.now());
            sesionRepository.save(sesion);
            throw new ResponseStatusException(UNAUTHORIZED, "Refresh token expirado");
        }

        Usuario usuario = usuarioRepository
                .findActiveByUsernameWithAuthorities(sesion.getUsuario().getUsername())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Usuario no válido o inactivo"));

        ApplicationUserPrincipal principal = ApplicationUserPrincipal.from(usuario);
        String newAccessToken = jwtService.generateToken(principal);
        String newRefreshToken = UUID.randomUUID().toString();

        sesion.setTokenSesion(newAccessToken);
        sesion.setRefreshToken(newRefreshToken);
        sesion.setFechaExpiracion(LocalDateTime.now().plusSeconds(jwtExpiration / 1000));
        sesion.setRefreshExpiracion(LocalDateTime.now().plusSeconds(refreshExpirationSeconds));
        sesion.setIpOrigen(extractIp(httpRequest));
        sesion.setUserAgent(httpRequest.getHeader("User-Agent"));
        sesionRepository.save(sesion);

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                AutenticacionMapper.toUsuarioResponse(usuario),
                usuario.getUsername(),
                principal.getAuthorities().stream().map(a -> a.getAuthority()).toList());
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        sesionRepository.findByRefreshTokenAndFechaCierreIsNull(request.refreshToken())
                .ifPresent(sesion -> {
                    sesion.setFechaCierre(LocalDateTime.now());
                    sesionRepository.save(sesion);
                });
    }

    // ─── Helpers ────────────────────────────────────────────────────────────────

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

    /**
     * Registra un evento de acceso en log_auditoria.
     * Reutiliza la tabla existente en la BD para registrar intentos de login.
     */
    private void logAcceso(Usuario usuario, String tabla, String operacion,
            HttpServletRequest request, String descripcion) {
        try {
            LogAuditoria log = LogAuditoria.builder()
                    .tabla(tabla)
                    .operacion(operacion)
                    .idRegistro(usuario != null ? String.valueOf(usuario.getIdUsuario()) : null)
                    .usuario(usuario)
                    .ipOrigen(extractIp(request))
                    .userAgent(request.getHeader("User-Agent"))
                    .datosNuevos(java.util.Map.of("resultado", descripcion))
                    .build();
            logAuditoriaRepository.save(log);
        } catch (Exception e) {
            // No romper el flujo principal si el log falla
        }
    }

    private String extractIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
