package org.restobar.gaira.modulo_inventario.mapper.receta;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.restobar.gaira.modulo_comercial.entity.ProductoFinal;
import org.restobar.gaira.modulo_inventario.dto.receta.IngredienteRecetaRequest;
import org.restobar.gaira.modulo_inventario.dto.receta.IngredienteRecetaResponse;
import org.restobar.gaira.modulo_inventario.dto.receta.RecetaCreate;
import org.restobar.gaira.modulo_inventario.dto.receta.RecetaResponse;
import org.restobar.gaira.modulo_inventario.dto.receta.RecetaUpdate;
import org.restobar.gaira.modulo_inventario.entity.IngredienteReceta;
import org.restobar.gaira.modulo_inventario.entity.Inventario;
import org.restobar.gaira.modulo_inventario.entity.Receta;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.springframework.stereotype.Component;

@Component
public class RecetaMapper {

    public Receta toEntity(RecetaCreate request, ProductoFinal producto, Sucursal sucursalReferencia) {
        if (request == null) return null;
        return Receta.builder()
                .productoFinal(producto)
                .sucursalReferencia(sucursalReferencia)
                .nombre(request.nombre().trim())
                .descripcion(request.descripcion())
                .tiempoPreparacion(request.tiempoPreparacion())
                .instrucciones(request.instrucciones())
                .versionEtiqueta(request.versionEtiqueta())
                .fechaVigenciaInicio(request.fechaVigenciaInicio())
                .fechaVigenciaFin(request.fechaVigenciaFin())
                .costoTotal(BigDecimal.ZERO)
                .activo(request.activo() == null || request.activo())
                .build();
    }

    public void updateEntityFromDto(Receta receta, RecetaUpdate request, ProductoFinal producto, Sucursal sucursalReferencia) {
        if (receta == null || request == null) return;
        receta.setProductoFinal(producto);
        receta.setSucursalReferencia(sucursalReferencia);
        receta.setNombre(request.nombre().trim());
        receta.setDescripcion(request.descripcion());
        receta.setTiempoPreparacion(request.tiempoPreparacion());
        receta.setInstrucciones(request.instrucciones());
        receta.setVersionEtiqueta(request.versionEtiqueta());
        receta.setFechaVigenciaInicio(request.fechaVigenciaInicio());
        receta.setFechaVigenciaFin(request.fechaVigenciaFin());
        if (request.activo() != null) receta.setActivo(request.activo());
    }

    public IngredienteReceta toIngredienteEntity(IngredienteRecetaRequest request, Inventario inventario) {
        if (request == null) return null;
        return IngredienteReceta.builder()
                .inventario(inventario)
                .cantidad(request.cantidad())
                .unidadMedida(request.unidadMedida())
                .notas(request.notas())
                .build();
    }

    public RecetaResponse toResponse(Receta receta) {
        if (receta == null) return null;

        List<IngredienteRecetaResponse> ingredientes = receta.getIngredientes() == null
                ? List.of()
                : receta.getIngredientes().stream().map(this::toIngredienteResponse).toList();

        return RecetaResponse.builder()
                .idReceta(receta.getIdReceta())
                .idProductoFinal(receta.getProductoFinal() != null ? receta.getProductoFinal().getIdProductoFinal() : null)
                .nombreProductoFinal(receta.getProductoFinal() != null ? receta.getProductoFinal().getNombre() : null)
                .idSucursalReferencia(receta.getSucursalReferencia() != null ? receta.getSucursalReferencia().getIdSucursal() : null)
                .nombreSucursalReferencia(receta.getSucursalReferencia() != null ? receta.getSucursalReferencia().getNombre() : null)
                .nombre(receta.getNombre())
                .descripcion(receta.getDescripcion())
                .tiempoPreparacion(receta.getTiempoPreparacion())
                .instrucciones(receta.getInstrucciones())
                .versionEtiqueta(receta.getVersionEtiqueta())
                .fechaVigenciaInicio(receta.getFechaVigenciaInicio())
                .fechaVigenciaFin(receta.getFechaVigenciaFin())
                .costoTotal(receta.getCostoTotal())
                .activo(receta.getActivo())
                .ingredientes(ingredientes)
                .build();
    }

    public IngredienteRecetaResponse toIngredienteResponse(IngredienteReceta ingrediente) {
        if (ingrediente == null) return null;
        return IngredienteRecetaResponse.builder()
                .idIngredienteReceta(ingrediente.getIdIngredienteReceta())
                .idInventario(ingrediente.getInventario() != null ? ingrediente.getInventario().getIdInventario() : null)
                .nombreInventario(ingrediente.getInventario() != null ? ingrediente.getInventario().getNombre() : null)
                .cantidad(ingrediente.getCantidad())
                .unidadMedida(ingrediente.getUnidadMedida())
                .notas(ingrediente.getNotas())
                .build();
    }

    public Map<String, Object> toAuditMap(Receta receta) {
        if (receta == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idReceta", receta.getIdReceta());
        map.put("idProductoFinal", receta.getProductoFinal() != null ? receta.getProductoFinal().getIdProductoFinal() : null);
        map.put("idSucursalReferencia", receta.getSucursalReferencia() != null ? receta.getSucursalReferencia().getIdSucursal() : null);
        map.put("nombre", receta.getNombre());
        map.put("versionEtiqueta", receta.getVersionEtiqueta());
        map.put("fechaVigenciaInicio", receta.getFechaVigenciaInicio());
        map.put("fechaVigenciaFin", receta.getFechaVigenciaFin());
        map.put("costoTotal", receta.getCostoTotal());
        map.put("activo", receta.getActivo());
        map.put("cantidadIngredientes", receta.getIngredientes() != null ? receta.getIngredientes().size() : 0);
        return map;
    }

    public Map<String, Object> toAuditMap(RecetaResponse response) {
        if (response == null) return Map.of();
        Map<String, Object> map = new HashMap<>();
        map.put("idReceta", response.idReceta());
        map.put("idProductoFinal", response.idProductoFinal());
        map.put("idSucursalReferencia", response.idSucursalReferencia());
        map.put("nombre", response.nombre());
        map.put("versionEtiqueta", response.versionEtiqueta());
        map.put("fechaVigenciaInicio", response.fechaVigenciaInicio());
        map.put("fechaVigenciaFin", response.fechaVigenciaFin());
        map.put("costoTotal", response.costoTotal());
        map.put("activo", response.activo());
        map.put("cantidadIngredientes", response.ingredientes() != null ? response.ingredientes().size() : 0);
        return map;
    }
}
