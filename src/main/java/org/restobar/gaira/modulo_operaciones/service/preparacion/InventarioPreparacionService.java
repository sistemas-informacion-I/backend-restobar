package org.restobar.gaira.modulo_operaciones.service.preparacion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.restobar.gaira.modulo_comercial.entity.ProductoFinal;
import org.restobar.gaira.modulo_inventario.dto.stock.StockAjusteRequest;
import org.restobar.gaira.modulo_inventario.entity.IngredienteReceta;
import org.restobar.gaira.modulo_inventario.entity.Receta;
import org.restobar.gaira.modulo_inventario.entity.StockSucursal;
import org.restobar.gaira.modulo_inventario.repository.RecetaRepository;
import org.restobar.gaira.modulo_inventario.repository.StockSucursalRepository;
import org.restobar.gaira.modulo_inventario.service.stock.StockSucursalService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventarioPreparacionService {

    private final RecetaRepository recetaRepository;
    private final StockSucursalRepository stockSucursalRepository;
    private final StockSucursalService stockSucursalService;

    @Transactional
    public Map<String, Object> descontarInventarioFIFO(ProductoFinal productoFinal, Long idSucursal, Integer cantidad) {
        Map<String, Object> resultado = new HashMap<>();
        List<String> errores = new ArrayList<>();
        int descontados = 0;

        try {
            // 1. Buscar receta del producto
            List<Receta> recetas = recetaRepository.findByFiltros(null, true, productoFinal.getIdProductoFinal());
            if (recetas.isEmpty()) {
                String msg = "El producto " + productoFinal.getNombre() + " no tiene receta asociada. No se puede descontar inventario.";
                log.warn(msg);
                resultado.put("exitoso", false);
                resultado.put("mensaje", msg);
                resultado.put("ingredientesDescontados", 0);
                return resultado;
            }

            Receta receta = recetas.get(0);
            List<IngredienteReceta> ingredientes = receta.getIngredientes();

            if (ingredientes.isEmpty()) {
                log.info("Receta {} no tiene ingredientes, no hay descuento de inventario", receta.getIdReceta());
                resultado.put("exitoso", true);
                resultado.put("mensaje", "Receta sin ingredientes");
                resultado.put("ingredientesDescontados", 0);
                return resultado;
            }

            // 2. Para cada ingrediente, descontar stock usando StockSucursalService.ajustarStock()
            for (IngredienteReceta ingrediente : ingredientes) {
                try {
                    BigDecimal cantidadADescontar = ingrediente.getCantidad().multiply(BigDecimal.valueOf(cantidad));

                    StockSucursal stock = stockSucursalRepository
                            .findByInventarioIdInventarioAndSucursalIdSucursal(
                                    ingrediente.getInventario().getIdInventario(),
                                    idSucursal)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                    "No hay stock de " + ingrediente.getInventario().getNombre() + " en la sucursal"));

                    StockAjusteRequest request = new StockAjusteRequest();
                    request.setIdInventario(ingrediente.getInventario().getIdInventario());
                    request.setCantidad(cantidadADescontar.negate());

                    stockSucursalService.ajustarStock(stock.getIdStock(), request);
                    descontados++;

                } catch (ResponseStatusException e) {
                    errores.add(String.format("Error descuento %s: %s",
                            ingrediente.getInventario().getNombre(), e.getReason()));
                    log.error("Error descuentando inventario para ingrediente: {}",
                            ingrediente.getIdIngredienteReceta(), e);
                }
            }

            // 3. Compilar resultado
            if (errores.isEmpty()) {
                resultado.put("exitoso", true);
                resultado.put("mensaje", String.format("Descuento exitoso: %d ingredientes descontados", descontados));
                resultado.put("ingredientesDescontados", descontados);
            } else {
                resultado.put("exitoso", false);
                resultado.put("mensaje", String.format("Errores en descuento: %s", String.join("; ", errores)));
                resultado.put("ingredientesDescontados", descontados);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, (String) resultado.get("mensaje"));
            }

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al descontar inventario", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al descontar inventario: " + e.getMessage());
        }

        return resultado;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> validarDisponibilidad(ProductoFinal productoFinal, Long idSucursal, Integer cantidad) {
        Map<String, Object> resultado = new HashMap<>();
        List<String> advertencias = new ArrayList<>();

        try {
            List<Receta> recetas = recetaRepository.findByFiltros(null, true, productoFinal.getIdProductoFinal());
            if (recetas.isEmpty()) {
                resultado.put("disponible", false);
                resultado.put("mensaje", "Producto sin receta");
                return resultado;
            }

            Receta receta = recetas.get(0);
            List<IngredienteReceta> ingredientes = receta.getIngredientes();

            if (ingredientes.isEmpty()) {
                resultado.put("disponible", true);
                resultado.put("mensaje", "Receta sin ingredientes");
                return resultado;
            }

            for (IngredienteReceta ingrediente : ingredientes) {
                BigDecimal cantidadADescontar = ingrediente.getCantidad().multiply(BigDecimal.valueOf(cantidad));

                Optional<StockSucursal> stockOpt = stockSucursalRepository
                        .findByInventarioIdInventarioAndSucursalIdSucursal(
                                ingrediente.getInventario().getIdInventario(),
                                idSucursal);

                if (stockOpt.isEmpty() || stockOpt.get().getCantidad().compareTo(cantidadADescontar) < 0) {
                    advertencias.add(String.format("Stock insuficiente: %s", ingrediente.getInventario().getNombre()));
                }
            }

            if (advertencias.isEmpty()) {
                resultado.put("disponible", true);
                resultado.put("mensaje", "Stock disponible");
            } else {
                resultado.put("disponible", false);
                resultado.put("mensaje", String.join("; ", advertencias));
            }

        } catch (Exception e) {
            resultado.put("disponible", false);
            resultado.put("mensaje", "Error en validación: " + e.getMessage());
        }

        return resultado;
    }
}
