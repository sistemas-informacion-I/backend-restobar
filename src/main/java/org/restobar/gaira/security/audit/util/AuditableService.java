package org.restobar.gaira.security.audit.util;

import java.util.Map;

/**
 * Interfaz que deben implementar los servicios que deseen usar la auditoría
 * automática por aspectos.
 * 
 * @param <ID> Tipo del ID de la entidad.
 * @param <E>  Tipo de la entidad.
 */
public interface AuditableService<ID, E> {

    /**
     * Busca la entidad por su ID. Se usa para capturar el estado "antes".
     */
    E getEntity(ID id);

    /**
     * Busca la entidad por su ID y tabla lógica auditada. Por defecto delega en
     * getEntity para mantener compatibilidad con servicios de una sola entidad.
     */
    default E getEntity(ID id, String tabla) {
        return getEntity(id);
    }

    /**
     * Convierte la entidad a un mapa de auditoría con los campos relevantes.
     */
    Map<String, Object> mapToAudit(E entity);
}
