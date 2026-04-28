package org.restobar.gaira.modulo_acceso.mapper.auditoria;

import java.util.LinkedHashMap;
import java.util.Map;

public final class AuditSnapshotBuilder {

    private final Map<String, Object> data = new LinkedHashMap<>();

    private AuditSnapshotBuilder() {
    }

    public static AuditSnapshotBuilder create() {
        return new AuditSnapshotBuilder();
    }

    public AuditSnapshotBuilder put(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public Map<String, Object> build() {
        return Map.copyOf(data);
    }
}
