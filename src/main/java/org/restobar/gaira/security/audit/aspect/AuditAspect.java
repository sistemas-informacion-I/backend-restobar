package org.restobar.gaira.security.audit.aspect;

import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.restobar.gaira.security.audit.annotation.Auditable;
import org.restobar.gaira.security.audit.service.LogAuditoriaService;
import org.restobar.gaira.security.audit.util.AuditableService;
import org.restobar.gaira.security.audit.util.AuditoriaUtils;
import org.restobar.gaira.security.utils.SecurityUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class AuditAspect {
    public AuditAspect(LogAuditoriaService logAuditoriaService, SecurityUtils securityUtils,
            HttpServletRequest httpServletRequest) {
        this.logAuditoriaService = logAuditoriaService;
        this.securityUtils = securityUtils;
        this.httpServletRequest = httpServletRequest;
    }

    private final LogAuditoriaService logAuditoriaService;
    private final SecurityUtils securityUtils;
    private final HttpServletRequest httpServletRequest;

    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        Object target = joinPoint.getTarget();

        // Si el servicio no implementa AuditableService, no podemos auditar
        // automaticamente con diff
        if (!(target instanceof AuditableService)) {
            return joinPoint.proceed();
        }

        @SuppressWarnings("unchecked")
        AuditableService<Object, Object> service = (AuditableService<Object, Object>) target;

        String operacion = auditable.operacion();
        Object id = extractId(joinPoint, auditable.idParamName());

        Map<String, Object> stateAntes = null;
        if (("UPDATE".equals(operacion) || "DELETE".equals(operacion)) && id != null) {
            try {
                Object entityAntes = service.getEntity(id);
                if (entityAntes != null) {
                    stateAntes = service.mapToAudit(entityAntes);
                }
            } catch (Exception e) {
                log.warn("No se pudo capturar el estado inicial para auditoría: {}", e.getMessage());
            }
        }

        // Ejecutar la operacion real
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable t) {
            log.error("Error al ejecutar método de negocio: {}", t.getMessage());
            throw t;
        }

        try {
            // Capturar estado final
            Map<String, Object> stateDespues = null;

            // Si el ID era nulo (INSERT), intentar extraerlo del resultado
            Object idFinal = id;
            if (idFinal == null && result != null) {
                idFinal = extractIdFromResult(result);
            }
            String idRegistro = idFinal != null ? String.valueOf(idFinal) : "N/A";

            if ("INSERT".equals(operacion)) {
                stateDespues = service.mapToAudit(result);
            } else if ("UPDATE".equals(operacion)) {
                // Priorizar el resultado del método para evitar depender de conversiones de ID.
                if (result != null) {
                    stateDespues = service.mapToAudit(result);
                }
                // Fallback a consulta por ID para métodos que retornan void o DTO sin campos
                // auditables.
                if ((stateDespues == null || stateDespues.isEmpty()) && idFinal != null) {
                    Object entityDespues = service.getEntity(idFinal);
                    if (entityDespues != null) {
                        stateDespues = service.mapToAudit(entityDespues);
                    }
                }
            }

            log.debug("Audit state for {} {} id={}. before={}, after={}",
                    auditable.tabla(), operacion, idRegistro, stateAntes, stateDespues);

            // Calcular diff si es UPDATE
            Map<String, Object> datosAntes = stateAntes;
            Map<String, Object> datosNuevos = stateDespues;

            if ("UPDATE".equals(operacion)) {
                Map<String, Object>[] diff = AuditoriaUtils.calculateDiff(stateAntes, stateDespues);
                datosAntes = diff[0];
                datosNuevos = diff[1];

                log.debug("Audit diff for {} id={}: {} fields changed",
                        auditable.tabla(), idRegistro, datosNuevos.size());
                if (datosAntes.isEmpty() && datosNuevos.isEmpty()) {
                    // Si no se detectan diferencias por el diff, persistimos snapshot completo
                    // para no perder trazabilidad de actualizaciones ejecutadas.
                    log.debug("No diff detected for {} id={}, logging full snapshot",
                            auditable.tabla(), idRegistro);
                    datosAntes = stateAntes != null ? stateAntes : Map.of();
                    datosNuevos = stateDespues != null ? stateDespues : Map.of();
                }
            }

            // Registrar en la BD
            logAuditoriaService.logOperacion(
                    securityUtils.getCurrentUser(),
                    auditable.tabla(),
                    operacion,
                    idRegistro,
                    datosAntes,
                    datosNuevos,
                    httpServletRequest);
        } catch (Throwable e) {
            log.error("Error crítico en proceso de auditoría (no bloqueante): {}", e.getMessage(), e);
        }

        return result;
    }

    private Object extractIdFromResult(Object result) {
        if (result == null)
            return null;
        try {
            // 1. Intentar por getter convencion getId... (para DTOs normales)
            try {
                for (java.lang.reflect.Method m : result.getClass().getMethods()) {
                    if (m.getName().startsWith("get") && m.getName().toLowerCase().contains("id")
                            && m.getParameterCount() == 0) {
                        return m.invoke(result);
                    }
                }
            } catch (ReflectiveOperationException e) {
            }

            // 2. Intentar campos directos (Records o DTOs con campos id...)
            for (java.lang.reflect.Field field : result.getClass().getDeclaredFields()) {
                if (field.getName().toLowerCase().contains("id")) {
                    try {
                        // Para Records, intentamos el metodo con el mismo nombre que el campo
                        return result.getClass().getMethod(field.getName()).invoke(result);
                    } catch (NoSuchMethodException e) {
                        // Para clases normales, acceso directo al campo
                        field.setAccessible(true);
                        return field.get(result);
                    }
                }
            }
        } catch (ReflectiveOperationException | RuntimeException e) {
            log.warn("No se pudo extraer ID del resultado: {}", e.getMessage());
        }
        return null;
    }

    private Object extractId(ProceedingJoinPoint joinPoint, String idParamName) {
        if (idParamName == null || idParamName.isEmpty()) {
            return null;
        }

        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] parameterNames = signature.getParameterNames();
            Object[] args = joinPoint.getArgs();

            if (parameterNames != null) {
                for (int i = 0; i < parameterNames.length; i++) {
                    if (idParamName.equals(parameterNames[i])) {
                        return args[i];
                    }
                }
            }

            // Si no se encuentra por nombre o parameterNames es nulo, intentamos el primer
            // argumento si es Long o String de id
            if (args != null && args.length > 0) {
                if (args[0] instanceof Long || args[0] instanceof Integer) {
                    return args[0];
                }
                // Fallback: buscar el primer parámetro numérico en cualquier posición.
                for (Object arg : args) {
                    if (arg instanceof Long || arg instanceof Integer) {
                        return arg;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("No se pudo extraer el ID del parámetro {}: {}", idParamName, e.getMessage());
        }
        return null;
    }
}