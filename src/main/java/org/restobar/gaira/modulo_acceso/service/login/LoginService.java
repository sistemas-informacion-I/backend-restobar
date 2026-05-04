package org.restobar.gaira.modulo_acceso.service.login;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.restobar.gaira.exception.LockoutException;
import org.restobar.gaira.modulo_acceso.dto.login.LoginRequest;
import org.restobar.gaira.modulo_acceso.dto.login.LoginResponse;
import org.restobar.gaira.modulo_acceso.dto.login.RefreshTokenRequest;
import org.restobar.gaira.modulo_acceso.dto.login.SendCodeRequest;
import org.restobar.gaira.modulo_acceso.dto.login.VerifyCodeRequest;
import org.restobar.gaira.modulo_acceso.entity.Sesion;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_acceso.mapper.usuario.UsuarioMapper;
import org.restobar.gaira.modulo_acceso.repository.UsuarioRepository;
import org.restobar.gaira.modulo_acceso.repository.login.SesionRepository;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.restobar.gaira.modulo_operaciones.entity.EmpleadoSucursal;
import org.restobar.gaira.modulo_operaciones.repository.EmpleadoSucursalRepository;
import org.restobar.gaira.security.audit.service.LogAuditoriaService;
import org.restobar.gaira.security.jwt.JwtService;
import org.restobar.gaira.security.userdetails.ApplicationUserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import static org.springframework.http.HttpStatus.FORBIDDEN;
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
public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    private static final String ESTADO_HABILITADO = "HABILITADO";
    private static final String ESTADO_SUSPENDIDO = "SUSPENDIDO";
    private static final String REDIS_LOCK_PREFIX = "lockout:";

    private final UsuarioRepository usuarioRepository;
    private final SesionRepository sesionRepository;
    private final LogAuditoriaService logAuditoriaService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UsuarioMapper usuarioMapper;
    private final EmailService emailService;
    private final EmpleadoSucursalRepository empleadoSucursalRepository;

    @Autowired
    @Lazy
    private LoginService self;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${auth.refresh-expiration-seconds}")
    private long refreshExpirationSeconds;

    @Value("${auth.max-failed-attempts:5}")
    private int maxFailedAttempts;

    @Value("${auth.lockout-duration-minutes:3}")
    private int lockoutDurationMinutes;

    public LoginService(UsuarioRepository usuarioRepository,
            SesionRepository sesionRepository,
            LogAuditoriaService logAuditoriaService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RedisTemplate<String, Object> redisTemplate,
            UsuarioMapper usuarioMapper,
            EmailService emailService,
            EmpleadoSucursalRepository empleadoSucursalRepository) {
        this.usuarioRepository = usuarioRepository;
        this.sesionRepository = sesionRepository;
        this.logAuditoriaService = logAuditoriaService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.redisTemplate = redisTemplate;
        this.usuarioMapper = usuarioMapper;
        this.emailService = emailService;
        this.empleadoSucursalRepository = empleadoSucursalRepository;
    }

    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        String username = request.username();

        // 1. Verificar bloqueo en Redis
        Long ttl = safeRedisGetExpire(REDIS_LOCK_PREFIX + username);
        if (ttl != null && ttl > 0) {
            Instant lockedUntil = Instant.now().plusSeconds(ttl);
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
                Instant expireAt = Instant.now().plus(lockoutDurationMinutes, java.time.temporal.ChronoUnit.MINUTES);
                safeRedisSetLock(REDIS_LOCK_PREFIX + username, lockoutDurationMinutes);
                throw new LockoutException("Has superado el límite de intentos. Cuenta bloqueada.", expireAt);
            }

            throw new ResponseStatusException(UNAUTHORIZED,
                    "Credenciales inválidas. Intento " + intentos + " de " + maxFailedAttempts);
        }

        self.resetearIntentos(usuario);
        safeRedisDelete(REDIS_LOCK_PREFIX + username);

        ApplicationUserPrincipal principal = ApplicationUserPrincipal.from(usuario);
        String accessToken = jwtService.generateToken(principal);
        String refreshToken = UUID.randomUUID().toString();

        createSession(usuario, accessToken, refreshToken, httpRequest);
        logAuditoriaService.logAcceso(usuario, "usuario", "EJECUTAR", httpRequest, "login_exitoso");

        // Identificar sucursal si es empleado
        Long sucursalId = null;
        if ("E".equals(usuario.getTipoUsuario())) {
            sucursalId = empleadoSucursalRepository.findByEmpleado_Usuario_IdUsuarioAndActivoTrue(usuario.getIdUsuario())
                    .map(EmpleadoSucursal::getSucursal)
                    .map(Sucursal::getIdSucursal)
                    .orElse(null);
        }

        return new LoginResponse(
                accessToken,
                refreshToken,
                usuarioMapper.toResponse(usuario),
                usuario.getUsername(),
                usuario.getTipoUsuario(),
                sucursalId,
                principal.getAuthorities().stream().map(a -> a.getAuthority()).toList());
    }

    @Transactional
    public LoginResponse refreshToken(RefreshTokenRequest request, HttpServletRequest httpRequest) {
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

        // Identificar sucursal si es empleado
        Long sucursalId = null;
        if ("E".equals(usuario.getTipoUsuario())) {
            sucursalId = empleadoSucursalRepository.findByEmpleado_Usuario_IdUsuarioAndActivoTrue(usuario.getIdUsuario())
                    .map(EmpleadoSucursal::getSucursal)
                    .map(Sucursal::getIdSucursal)
                    .orElse(null);
        }

        return new LoginResponse(
                newAccessToken,
                newRefreshToken,
                usuarioMapper.toResponse(usuario),
                usuario.getUsername(),
                usuario.getTipoUsuario(),
                sucursalId,
                principal.getAuthorities().stream().map(a -> a.getAuthority()).toList());
    }

    @Transactional
    public void logout(RefreshTokenRequest request, HttpServletRequest httpRequest) {
        sesionRepository.findByRefreshTokenAndFechaCierreIsNull(request.refreshToken())
                .ifPresent(sesion -> {
                    logAuditoriaService.logAcceso(sesion.getUsuario(), "usuario", "EJECUTAR", httpRequest, "logout_exitoso");
                    sesion.setFechaCierre(LocalDateTime.now());
                    sesionRepository.save(sesion);
                });
    }

    @Transactional
    public void sendResetCode(SendCodeRequest request, HttpServletRequest httpRequest) {
        Usuario usuario = usuarioRepository.findByCorreo(request.correo())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Correo no registrado."));

        String code = generateAlphanumericCode(6);
        String key = "reset:code:" + request.correo();

        try {
            redisTemplate.opsForValue().set(key, code, 15, TimeUnit.MINUTES);
            log.info("Código de recuperación guardado en Redis para {}", request.correo());
        } catch (Exception e) {
            log.error("Error al guardar código en Redis para {}: {}. ¿Está Redis activo?", request.correo(),
                    e.getMessage());
            throw new ResponseStatusException(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                    "El servicio de recuperación no está disponible en este momento. Inténtelo más tarde.");
        }

        boolean emailSent = emailService.sendResetCode(request.correo(), usuario.getUsername(), code);
        if (!emailSent) {
            safeRedisDelete(key);
            throw new ResponseStatusException(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo enviar el código de recuperación. Verifique la configuración SMTP e intente nuevamente.");
        }

        logAuditoriaService.logAcceso(usuario, "usuario", "EJECUTAR", httpRequest, "solicitud_codigo_recuperacion");
    }

    @Transactional
    public void verifyAndResetPassword(VerifyCodeRequest request, HttpServletRequest httpRequest) {
        String key = "reset:code:" + request.correo();
        String savedCode = null;

        try {
            savedCode = (String) redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Error al obtener código de Redis para {}: {}. ¿Está Redis activo?", request.correo(),
                    e.getMessage());
            throw new ResponseStatusException(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                    "El servicio de verificación no está disponible en este momento.");
        }

        if (savedCode == null || !savedCode.equals(request.codigo())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Código inválido o expirado.");
        }

        Usuario usuario = usuarioRepository.findByCorreo(request.correo())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Usuario no encontrado."));

        String newPassword = generateSecurePassword(12);
        usuario.setPasswordHash(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);

        boolean emailSent = emailService.sendNewPassword(request.correo(), usuario.getUsername(), newPassword);
        if (!emailSent) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo enviar la nueva contraseña. Revise SMTP e intente nuevamente.");
        }
        safeRedisDelete(key);

        logAuditoriaService.logAcceso(usuario, "usuario", "UPDATE", httpRequest, "reset_password_exitoso");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int incrementarIntentos(Usuario usuario, int maxIntentos, int durationMinutes) {
        Usuario u = usuarioRepository.findById(usuario.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para incrementar intentos"));

        int intentos = (u.getIntentosFallidos() == null ? 0 : u.getIntentosFallidos()) + 1;
        u.setIntentosFallidos(intentos);
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
        } catch (Exception ex) {
            log.warn("Error al obtener TTL de Redis para clave {}: {}", key, ex.getMessage());
            return null;
        }
    }

    private void safeRedisSetLock(String key, int minutes) {
        try {
            redisTemplate.opsForValue().set(key, "LOCKED", minutes, TimeUnit.MINUTES);
            log.info("Bloqueo establecido en Redis para: {}", key);
        } catch (Exception ex) {
            log.error("Error crítico al establecer bloqueo en Redis para {}: {}", key, ex.getMessage());
        }
    }

    private void safeRedisDelete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception ex) {
            log.warn("Error al eliminar clave {} de Redis: {}", key, ex.getMessage());
        }
    }

    private String generateAlphanumericCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        java.security.SecureRandom random = new java.security.SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String generateSecurePassword(int length) {
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "0123456789";
        String all = lower + upper + digits;

        java.security.SecureRandom random = new java.security.SecureRandom();
        StringBuilder sb = new StringBuilder();

        // Ensure at least one of each
        sb.append(lower.charAt(random.nextInt(lower.length())));
        sb.append(upper.charAt(random.nextInt(upper.length())));
        sb.append(digits.charAt(random.nextInt(digits.length())));

        for (int i = 3; i < length; i++) {
            sb.append(all.charAt(random.nextInt(all.length())));
        }

        // Shuffle
        char[] result = sb.toString().toCharArray();
        for (int i = result.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = result[i];
            result[i] = result[j];
            result[j] = temp;
        }

        return new String(result);
    }
}
