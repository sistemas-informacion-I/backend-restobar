package org.restobar.gaira.modulo_inventario.service.receta;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.restobar.gaira.modulo_comercial.entity.ProductoFinal;
import org.restobar.gaira.modulo_comercial.repository.ProductoFinalRepository;
import org.restobar.gaira.modulo_inventario.dto.receta.IngredienteRecetaRequest;
import org.restobar.gaira.modulo_inventario.dto.receta.RecetaCostoResponse;
import org.restobar.gaira.modulo_inventario.dto.receta.RecetaCreate;
import org.restobar.gaira.modulo_inventario.dto.receta.RecetaDuplicarRequest;
import org.restobar.gaira.modulo_inventario.dto.receta.RecetaResponse;
import org.restobar.gaira.modulo_inventario.dto.receta.RecetaUpdate;
import org.restobar.gaira.modulo_inventario.entity.IngredienteReceta;
import org.restobar.gaira.modulo_inventario.entity.Inventario;
import org.restobar.gaira.modulo_inventario.entity.Inventario.UnidadMedida;
import org.restobar.gaira.modulo_inventario.entity.Receta;
import org.restobar.gaira.modulo_inventario.entity.StockSucursal;
import org.restobar.gaira.modulo_inventario.mapper.receta.RecetaMapper;
import org.restobar.gaira.modulo_inventario.repository.InventarioRepository;
import org.restobar.gaira.modulo_inventario.repository.RecetaRepository;
import org.restobar.gaira.modulo_inventario.repository.StockSucursalRepository;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.restobar.gaira.modulo_operaciones.repository.SucursalRepository;
import org.restobar.gaira.security.audit.annotation.Auditable;
import org.restobar.gaira.security.audit.util.AuditableService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class RecetaService implements AuditableService<Long, Object> {

    private static final BigDecimal MIL = BigDecimal.valueOf(1000);

    private final RecetaRepository recetaRepository;
    private final RecetaMapper recetaMapper;
    private final ProductoFinalRepository productoFinalRepository;
    private final InventarioRepository inventarioRepository;
    private final SucursalRepository sucursalRepository;
    private final StockSucursalRepository stockSucursalRepository;

    @Override
    public Object getEntity(Long id) {
        return recetaRepository.findDetalleById(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof Receta receta) {
            return recetaMapper.toAuditMap(receta);
        }
        if (entity instanceof RecetaResponse response) {
            return recetaMapper.toAuditMap(response);
        }
        return Map.of();
    }

    @Transactional(readOnly = true)
    public List<RecetaResponse> findAll(String nombre, Boolean activo, Long idProductoFinal) {
        String nombrePattern = (nombre != null && !nombre.isBlank()) ? "%" + nombre.toLowerCase() + "%" : null;

        return recetaRepository.findByFiltros(nombrePattern, activo, idProductoFinal).stream()
                .map(recetaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RecetaResponse findById(Long id) {
        Receta receta = getRecetaDetalleOrThrow(id);
        return recetaMapper.toResponse(receta);
    }

    @Transactional
    @Auditable(tabla = "receta", operacion = "INSERT")
    public RecetaResponse create(RecetaCreate request) {
        validarFechasVigencia(request.fechaVigenciaInicio(), request.fechaVigenciaFin());
        validarNombreUnico(request.idProductoFinal(), request.nombre(), null);

        ProductoFinal productoFinal = getProductoOrThrow(request.idProductoFinal());
        Sucursal sucursal = getSucursalOrThrow(request.idSucursalReferencia());

        Receta receta = recetaMapper.toEntity(request, productoFinal, sucursal);
        List<IngredienteReceta> ingredientes = construirIngredientes(request.ingredientes());
        receta.replaceIngredientes(ingredientes);

        BigDecimal costoCalculado = calcularCostoTotal(ingredientes, sucursal.getIdSucursal());
        receta.setCostoTotal(costoCalculado);

        return recetaMapper.toResponse(recetaRepository.save(receta));
    }

    @Transactional
    @Auditable(tabla = "receta", operacion = "UPDATE", idParamName = "id")
    public RecetaResponse update(Long id, RecetaUpdate request) {
        validarFechasVigencia(request.fechaVigenciaInicio(), request.fechaVigenciaFin());

        Receta receta = getRecetaDetalleOrThrow(id);
        validarNombreUnico(request.idProductoFinal(), request.nombre(), id);

        ProductoFinal productoFinal = getProductoOrThrow(request.idProductoFinal());
        Sucursal sucursal = getSucursalOrThrow(request.idSucursalReferencia());

        recetaMapper.updateEntityFromDto(receta, request, productoFinal, sucursal);

        List<IngredienteReceta> ingredientes = construirIngredientes(request.ingredientes());
        receta.replaceIngredientes(ingredientes);

        BigDecimal costoCalculado = calcularCostoTotal(ingredientes, sucursal.getIdSucursal());
        receta.setCostoTotal(costoCalculado);

        return recetaMapper.toResponse(recetaRepository.save(receta));
    }

    @Transactional
    @Auditable(tabla = "receta", operacion = "UPDATE", idParamName = "id")
    public RecetaResponse desactivar(Long id) {
        Receta receta = getRecetaDetalleOrThrow(id);
        receta.setActivo(false);
        return recetaMapper.toResponse(recetaRepository.save(receta));
    }

    @Transactional
    @Auditable(tabla = "receta", operacion = "INSERT")
    public RecetaResponse duplicar(Long id, RecetaDuplicarRequest request) {
        validarFechasVigencia(request.fechaVigenciaInicio(), request.fechaVigenciaFin());

        Receta origen = getRecetaDetalleOrThrow(id);
        validarNombreUnico(origen.getProductoFinal().getIdProductoFinal(), request.nombre(), null);

        Sucursal sucursal = getSucursalOrThrow(request.idSucursalReferencia());

        Receta copia = Receta.builder()
                .productoFinal(origen.getProductoFinal())
                .sucursalReferencia(sucursal)
                .nombre(request.nombre().trim())
                .descripcion(origen.getDescripcion())
                .tiempoPreparacion(origen.getTiempoPreparacion())
                .instrucciones(origen.getInstrucciones())
                .versionEtiqueta(request.versionEtiqueta())
                .fechaVigenciaInicio(request.fechaVigenciaInicio())
                .fechaVigenciaFin(request.fechaVigenciaFin())
                .activo(true)
                .build();

        List<IngredienteReceta> ingredientesCopia = origen.getIngredientes().stream()
                .map(ing -> IngredienteReceta.builder()
                        .inventario(ing.getInventario())
                        .cantidad(ing.getCantidad())
                        .unidadMedida(ing.getUnidadMedida())
                        .notas(ing.getNotas())
                        .build())
                .toList();

        copia.replaceIngredientes(ingredientesCopia);
        copia.setCostoTotal(calcularCostoTotal(ingredientesCopia, sucursal.getIdSucursal()));

        return recetaMapper.toResponse(recetaRepository.save(copia));
    }

    @Transactional
    @Auditable(tabla = "receta", operacion = "UPDATE", idParamName = "id")
    public RecetaCostoResponse recalcularCosto(Long id, Long idSucursal) {
        Receta receta = getRecetaDetalleOrThrow(id);
        Sucursal sucursal = getSucursalOrThrow(idSucursal);

        BigDecimal costoTotal = calcularCostoTotal(receta.getIngredientes(), idSucursal);
        receta.setSucursalReferencia(sucursal);
        receta.setCostoTotal(costoTotal);
        recetaRepository.save(receta);

        return RecetaCostoResponse.builder()
                .idReceta(receta.getIdReceta())
                .idSucursal(sucursal.getIdSucursal())
                .nombreSucursal(sucursal.getNombre())
                .costoTotal(costoTotal)
                .build();
    }

    @Transactional
    @Auditable(tabla = "receta", operacion = "DELETE", idParamName = "id")
    public void delete(Long id) {
        Receta receta = getRecetaDetalleOrThrow(id);
        recetaRepository.delete(receta);
    }

    private Receta getRecetaDetalleOrThrow(Long id) {
        return recetaRepository.findDetalleById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Receta no encontrada"));
    }

    private ProductoFinal getProductoOrThrow(Long idProductoFinal) {
        ProductoFinal productoFinal = productoFinalRepository.findById(idProductoFinal)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producto final no encontrado"));

        if (!productoFinal.isActivo()) {
            throw new ResponseStatusException(BAD_REQUEST, "El producto final no está activo");
        }
        return productoFinal;
    }

    private Sucursal getSucursalOrThrow(Long idSucursal) {
        Sucursal sucursal = sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Sucursal no encontrada"));

        if (Boolean.FALSE.equals(sucursal.getActivo())) {
            throw new ResponseStatusException(BAD_REQUEST, "La sucursal de referencia no está activa");
        }
        return sucursal;
    }

    private void validarNombreUnico(Long idProductoFinal, String nombre, Long idExcluir) {
        boolean existe = recetaRepository.existsNombrePorProductoExcluyendoId(idProductoFinal, nombre.trim(), idExcluir);
        if (existe) {
            throw new ResponseStatusException(CONFLICT, "Ya existe una receta con ese nombre para el producto final");
        }
    }

    private void validarFechasVigencia(java.time.LocalDate inicio, java.time.LocalDate fin) {
        if (inicio != null && fin != null && fin.isBefore(inicio)) {
            throw new ResponseStatusException(BAD_REQUEST, "La fecha de fin de vigencia no puede ser menor a la de inicio");
        }
    }

    private List<IngredienteReceta> construirIngredientes(List<IngredienteRecetaRequest> ingredientesRequest) {
        if (ingredientesRequest == null || ingredientesRequest.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "La receta debe tener al menos un ingrediente");
        }

        List<Long> ids = ingredientesRequest.stream().map(IngredienteRecetaRequest::idInventario).toList();
        Map<Long, Inventario> inventariosPorId = inventarioRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Inventario::getIdInventario, Function.identity()));

        List<IngredienteReceta> ingredientes = ingredientesRequest.stream().map(req -> {
            Inventario inventario = inventariosPorId.get(req.idInventario());
            if (inventario == null) {
                throw new ResponseStatusException(NOT_FOUND, "Insumo no encontrado: " + req.idInventario());
            }
            if (!inventario.isActivo()) {
                throw new ResponseStatusException(BAD_REQUEST, "El insumo no está activo: " + inventario.getNombre());
            }

            validarCompatibilidadUnidad(req.unidadMedida(), inventario.getUnidadMedida(), inventario.getNombre());
            return recetaMapper.toIngredienteEntity(req, inventario);
        }).toList();

        return ingredientes;
    }

    private BigDecimal calcularCostoTotal(List<IngredienteReceta> ingredientes, Long idSucursal) {
        if (ingredientes == null || ingredientes.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        List<Long> idsInventario = ingredientes.stream()
                .map(ing -> ing.getInventario().getIdInventario())
                .distinct()
                .toList();

        Map<Long, StockSucursal> stockPorInventario = stockSucursalRepository.findActivosParaCosto(idSucursal, idsInventario)
                .stream()
                .collect(Collectors.toMap(ss -> ss.getInventario().getIdInventario(), Function.identity()));

        List<String> faltantes = idsInventario.stream()
                .filter(idInv -> !stockPorInventario.containsKey(idInv))
                .map(String::valueOf)
                .toList();

        if (!faltantes.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "No existe stock en sucursal para los insumos: " + String.join(", ", faltantes));
        }

        BigDecimal total = BigDecimal.ZERO;
        for (IngredienteReceta ingrediente : ingredientes) {
            StockSucursal stock = stockPorInventario.get(ingrediente.getInventario().getIdInventario());
            BigDecimal precioPromedio = stock.getPrecioPromedio() == null ? BigDecimal.ZERO : stock.getPrecioPromedio();

            BigDecimal cantidadNormalizada = convertirCantidad(ingrediente.getCantidad(), ingrediente.getUnidadMedida(),
                    ingrediente.getInventario().getUnidadMedida());

            total = total.add(precioPromedio.multiply(cantidadNormalizada));
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private void validarCompatibilidadUnidad(UnidadMedida unidadReceta, UnidadMedida unidadInventario, String nombreInsumo) {
        if (!sonCompatibles(unidadReceta, unidadInventario)) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Unidad incompatible para el insumo " + nombreInsumo + ": " + unidadReceta + " no es compatible con "
                            + unidadInventario);
        }
    }

    private boolean sonCompatibles(UnidadMedida origen, UnidadMedida destino) {
        if (origen == destino) {
            return true;
        }
        return esMasa(origen) && esMasa(destino)
                || esVolumen(origen) && esVolumen(destino);
    }

    private BigDecimal convertirCantidad(BigDecimal cantidad, UnidadMedida origen, UnidadMedida destino) {
        if (origen == destino) {
            return cantidad;
        }

        if (esMasa(origen) && esMasa(destino)) {
            if (origen == UnidadMedida.KG && destino == UnidadMedida.GRAMO) {
                return cantidad.multiply(MIL);
            }
            if (origen == UnidadMedida.GRAMO && destino == UnidadMedida.KG) {
                return cantidad.divide(MIL, 6, RoundingMode.HALF_UP);
            }
        }

        if (esVolumen(origen) && esVolumen(destino)) {
            if (origen == UnidadMedida.LITRO && destino == UnidadMedida.ML) {
                return cantidad.multiply(MIL);
            }
            if (origen == UnidadMedida.ML && destino == UnidadMedida.LITRO) {
                return cantidad.divide(MIL, 6, RoundingMode.HALF_UP);
            }
        }

        throw new ResponseStatusException(BAD_REQUEST,
                "No se puede convertir la unidad " + origen + " a " + destino);
    }

    private boolean esMasa(UnidadMedida unidad) {
        return unidad == UnidadMedida.KG || unidad == UnidadMedida.GRAMO;
    }

    private boolean esVolumen(UnidadMedida unidad) {
        return unidad == UnidadMedida.LITRO || unidad == UnidadMedida.ML;
    }
}
