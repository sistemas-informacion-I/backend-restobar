package org.restobar.gaira.security.audit.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AuditoriaUtils {

    /**
     * Compara dos mapas y devuelve un mapa con solo las diferencias.
     * Retorna un arreglo de dos mapas [antes, despues] que contienen solo las
     * claves modificadas.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object>[] calculateDiff(Map<String, Object> antes, Map<String, Object> despues) {
        Map<String, Object> diffAntes = new HashMap<>();
        Map<String, Object> diffDespues = new HashMap<>();

        // Si ambos son nulos, no hay diferencia
        if (antes == null && despues == null) {
            return new Map[] { new HashMap<>(), new HashMap<>() };
        }

        // Si antes es nulo (INSERT), el diff despues es todo el mapa despues
        if (antes == null) {
            return new Map[] { new HashMap<>(), new HashMap<>(despues) };
        }

        // Si despues es nulo (DELETE), el diff antes es todo el mapa antes
        if (despues == null) {
            return new Map[] { new HashMap<>(antes), new HashMap<>() };
        }

        // Iterar sobre todas las claves posibles en 'antes'
        antes.forEach((key, valAntes) -> {
            Object valDespues = despues.get(key);
            if (!Objects.equals(valAntes, valDespues)) {
                diffAntes.put(key, valAntes);
                diffDespues.put(key, valDespues);
            }
        });

        // Verificar si hay claves en 'despues' que no estan en 'antes'
        despues.forEach((key, valDespues) -> {
            if (!antes.containsKey(key)) {
                diffAntes.put(key, null);
                diffDespues.put(key, valDespues);
            }
        });

        return new Map[] { diffAntes, diffDespues };
    }
}