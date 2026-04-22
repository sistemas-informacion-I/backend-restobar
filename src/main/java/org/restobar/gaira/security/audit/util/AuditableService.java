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
     * Convierte la entidad a un mapa de auditoría con los campos relevantes.
     */
    Map<String, Object> mapToAudit(E entity);
}