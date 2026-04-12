package org.restobar.gaira.modulo_acceso.service.auth;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.restobar.gaira.exception.LockoutException;
import org.restobar.gaira.modulo_acceso.dto.auth.AuthLogin;
import org.restobar.gaira.modulo_acceso.dto.auth.AuthRegister;
import org.restobar.gaira.modulo_acceso.dto.auth.AuthResponse;
import org.restobar.gaira.modulo_acceso.dto.auth.RefreshToken;
import org.restobar.gaira.modulo_acceso.entity.Rol;
import org.restobar.gaira.modulo_acceso.entity.RolUsuario;
import org.restobar.gaira.modulo_acceso.entity.Sesion;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_acceso.mapper.usuario.UsuarioMapper;
import org.restobar.gaira.modulo_acceso.repository.RolRepository;
import org.restobar.gaira.modulo_acceso.repository.RolUsuarioRepository;
import org.restobar.gaira.modulo_acceso.repository.SesionRepository;
import org.restobar.gaira.modulo_acceso.repository.UsuarioRepository;
import org.restobar.gaira.security.audit.service.LogAuditoriaService;
import org.restobar.gaira.security.jwt.JwtService;
import org.restobar.gaira.security.userdetails.ApplicationUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

@Service
@Transactional
@SuppressWarnings("null")
public class AuthService {

    private static final String ESTADO_HABILITADO = "HABILITADO";
    private static final String ESTADO_SUSPENDIDO = "SUSPENDIDO";
    private static final String REDIS_LOCK_PREFIX = "lockout:";

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final SesionRepository sesionRepository;
    private final LogAuditoriaService logAuditoriaService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UsuarioMapper usuarioMapper;

    @Autowired
    @Lazy
    private AuthService self;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${auth.refresh-expiration-seconds}")
    private long refreshExpirationSeconds;

    @Value("${auth.max-failed-attempts:5}")
    private int maxFailedAttempts;

    @Value("${auth.lockout-duration-minutes:3}")
    private int lockoutDurationMinutes;

    public AuthService(UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            RolUsuarioRepository rolUsuarioRepository,
            SesionRepository sesionRepository,
            LogAuditoriaService logAuditoriaService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RedisTemplate<String, Object> redisTemplate,
            UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.rolUsuarioRepository = rolUsuarioRepository;
        this.sesionRepository = sesionRepository;
        this.logAuditoriaService = logAuditoriaService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.redisTemplate = redisTemplate;
        this.usuarioMapper = usuarioMapper;
    }

    @Transactional
    public AuthResponse register(AuthRegister request, HttpServletRequest httpRequest) {
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

        return new AuthResponse(
                accessToken,
                refreshToken,
                usuarioMapper.toResponse(usuario),
                usuario.getUsername(),
                principal.getAuthorities().stream().map(a -> a.getAuthority()).toList());
    }

    @Transactional
    public AuthResponse login(AuthLogin request, HttpServletRequest httpRequest) {
        String username = request.username();

        // 1. Verificar bloqueo en Redis antes de cualquier otra cosa
        Long ttl = safeRedisGetExpire(REDIS_LOCK_PREFIX + username);
        if (ttl != null && ttl > 0) {
            LocalDateTime lockedUntil = LocalDateTime.now().plusSeconds(ttl);
            logAuditoriaService.logAcceso(null, "usuario", "EJECUTAR", httpRequest, "cuenta_bloqueada_redis");
            throw new LockoutException("Tu cuenta está temporalmente bloqueada.", lockedUntil);
        }

        Usuario usuario = usuarioRepository.findActiveByUsernameWithAuthorities(username)
                .orElseThrow(() -> {
                    logAuditoriaService.logAcceso(null, "usuario", "EJECUTAR", httpRequest,
                            "usuario_no_existe: " + username);
                    return new ResponseStatusException(UNAUTHORIZED, "Credenciales inválidas");
                });

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            logAuditoriaService.logAcceso(usuario, "usuario", "EJECUTAR", httpRequest, "usuario_inactivo");
            throw new ResponseStatusException(FORBIDDEN, "Usuario inactivo");
        }

        if (ESTADO_SUSPENDIDO.equals(usuario.getEstadoAcceso())) {
            logAuditoriaService.logAcceso(usuario, "usuario", "EJECUTAR", httpRequest, "cuenta_suspendida");
            throw new ResponseStatusException(FORBIDDEN, "Cuenta suspendida");
        }

        if (!passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            int intentos = self.incrementarIntentos(usuario, maxFailedAttempts, lockoutDurationMinutes);
            logAuditoriaService.logAcceso(usuario, "usuario", "EJECUTAR", httpRequest,
                    "credenciales_invalidas_intento_" + intentos);

            if (intentos >= maxFailedAttempts) {
                // Bloquear en Redis si se supera el límite
                LocalDateTime expireAt = LocalDateTime.now().plusMinutes(lockoutDurationMinutes);
                safeRedisSetLock(REDIS_LOCK_PREFIX + username, lockoutDurationMinutes);
                throw new LockoutException("Has superado el límite de intentos. Cuenta bloqueada.", expireAt);
            }

            throw new ResponseStatusException(UNAUTHORIZED,
                    "Credenciales inválidas. Intento " + intentos + " de " + maxFailedAttempts);
        }

        // Login exitoso: resetear intentos (DB) y asegurar que no hay bloqueo en Redis
        self.resetearIntentos(usuario);
        safeRedisDelete(REDIS_LOCK_PREFIX + username);

        ApplicationUserPrincipal principal = ApplicationUserPrincipal.from(usuario);
        String accessToken = jwtService.generateToken(principal);
        String refreshToken = UUID.randomUUID().toString();

        createSession(usuario, accessToken, refreshToken, httpRequest);
        logAuditoriaService.logAcceso(usuario, "usuario", "EJECUTAR", httpRequest, "login_exitoso");

        return new AuthResponse(
                accessToken,
                refreshToken,
                usuarioMapper.toResponse(usuario),
                usuario.getUsername(),
                principal.getAuthorities().stream().map(a -> a.getAuthority()).toList());
    }

    @Transactional
    public AuthResponse refreshToken(RefreshToken request, HttpServletRequest httpRequest) {
        Sesion sesion = sesionRepository.findByRefreshTokenAndFechaCierreIsNull(request.refreshToken())
                .orElseThrow(
                        () -> new ResponseStatusException(UNAUTHORIZED, "Refresh token inválido o sesión cerrada"));

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
                usuarioMapper.toResponse(usuario),
                usuario.getUsername(),
                principal.getAuthorities().stream().map(a -> a.getAuthority()).toList());
    }

    @Transactional
    public void logout(RefreshToken request) {
        sesionRepository.findByRefreshTokenAndFechaCierreIsNull(request.refreshToken())
                .ifPresent(sesion -> {
                    sesion.setFechaCierre(LocalDateTime.now());
                    sesionRepository.save(sesion);
                });
    }

    // ─── Login Attempt Logic (Transaccional Independiente) ──────────────────────

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int incrementarIntentos(Usuario usuario, int maxIntentos, int durationMinutes) {
        Usuario u = usuarioRepository.findById(usuario.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para incrementar intentos"));

        int intentos = (u.getIntentosFallidos() == null ? 0 : u.getIntentosFallidos()) + 1;
        u.setIntentosFallidos(intentos);

        // Ya no cambiamos el estadoAcceso ni fechaBloqueoFinal en la DB, lo maneja
        // Redis.
        // Pero mantenemos intentosFallidos para el historial.
        usuarioRepository.saveAndFlush(u);
        return intentos;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void resetearIntentos(Usuario usuario) {
        Usuario u = usuarioRepository.findById(usuario.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para resetear intentos"));

        u.setIntentosFallidos(0);
        u.setEstadoAcceso(ESTADO_HABILITADO);
        usuarioRepository.saveAndFlush(u);
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

    private String extractIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private Long safeRedisGetExpire(String key) {
        try {
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private void safeRedisSetLock(String key, int minutes) {
        try {
            redisTemplate.opsForValue().set(key, "LOCKED", minutes, TimeUnit.MINUTES);
        } catch (RuntimeException ex) {
            // Si Redis no está disponible, mantenemos control de intentos por DB para no romper login.
        }
    }

    private void safeRedisDelete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (RuntimeException ex) {
            // No bloquear autenticación por fallos de Redis.
        }
    }
}
