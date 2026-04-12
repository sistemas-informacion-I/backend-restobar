package org.restobar.gaira.security.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
    /**
     * Nombre de la tabla lógica auditada (ej: "usuario", "rol").
     */
    String tabla();

    /**
     * Operación realizada (ej: "INSERT", "UPDATE", "DELETE").
     */
    String operacion();

    /**
     * Nombre del parámetro que contiene el ID si es UPDATE o DELETE.
     * Si está vacío, se asume que no hay ID directo o es un INSERT.
     */
    String idParamName() default "";
}