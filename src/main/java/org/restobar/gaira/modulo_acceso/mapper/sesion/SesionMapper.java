package org.restobar.gaira.modulo_acceso.mapper.sesion;

import java.util.HashMap;
import java.util.Map;
import org.restobar.gaira.modulo_acceso.dto.sesion.SesionResponse;
import org.restobar.gaira.modulo_acceso.entity.Sesion;
import org.springframework.stereotype.Component;

@Component
public class SesionMapper {

    public SesionResponse toResponse(Sesion s) {
        if (s == null) return null;

        return SesionResponse.builder()
                .idSesion(s.getIdSesion())
                .idUsuario(s.getUsuario() != null ? s.getUsuario().getIdUsuario() : null)
                .fechaInicio(s.getFechaInicio())
                .fechaExpiracion(s.getFechaExpiracion())
                .ipOrigen(s.getIpOrigen())
                .userAgent(s.getUserAgent())
                .fechaCierre(s.getFechaCierre())
                .activa(s.getFechaCierre() == null)
                .build();
    }

    public Map<String, Object> toAuditMap(Sesion s) {
        if (s == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idSesion", s.getIdSesion());
        map.put("idUsuario", s.getUsuario() != null ? s.getUsuario().getIdUsuario() : null);
        map.put("ipOrigen", s.getIpOrigen());
        map.put("fechaInicio", s.getFechaInicio());
        map.put("fechaCierre", s.getFechaCierre());
        return map;
    }

    public Map<String, Object> toAuditMap(SesionResponse sr) {
        if (sr == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idSesion", sr.idSesion());
        map.put("idUsuario", sr.idUsuario());
        map.put("ipOrigen", sr.ipOrigen());
        map.put("fechaInicio", sr.fechaInicio());
        map.put("fechaCierre", sr.fechaCierre());
        return map;
    }
}
