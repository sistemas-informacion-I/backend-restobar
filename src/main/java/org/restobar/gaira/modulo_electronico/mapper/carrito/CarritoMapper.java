package org.restobar.gaira.modulo_electronico.mapper.carrito;

import org.restobar.gaira.modulo_electronico.dto.carrito.CarritoResponse;
import org.restobar.gaira.modulo_electronico.dto.item.ItemCarritoResponse;
import org.restobar.gaira.modulo_electronico.entity.CarritoCompras;
import org.restobar.gaira.modulo_electronico.entity.ItemCarrito;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Convierte entidades de carrito a DTOs de respuesta.
 *
 * <p>El mapa {@code preciosActuales} proviene de {@code producto_sucursal}
 * y es consultado en tiempo real durante cada mapeo para garantizar que
 * los subtotales reflejen precios vigentes.
 */
@Component
public class CarritoMapper {

    /**
     * @param carrito        Entidad con ítems ya cargados (JOIN FETCH).
     * @param preciosActuales Mapa {@code idProductoFinal → precio} de la BD.
     * @param disponibles     Mapa {@code idProductoFinal → disponible} de la BD.
     * @param nombres         Mapa {@code idProductoFinal → nombre} de la BD.
     */
    public CarritoResponse toResponse(
            CarritoCompras carrito,
            Map<Long, BigDecimal> preciosActuales,
            Map<Long, Boolean> disponibles,
            Map<Long, String> nombres) {

        List<ItemCarritoResponse> itemsDto = carrito.getItems().stream()
                .map(item -> toItemResponse(item, preciosActuales, disponibles, nombres))
                .toList();

        BigDecimal total = itemsDto.stream()
                .filter(ItemCarritoResponse::disponible)
                .map(ItemCarritoResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CarritoResponse(
                carrito.getIdCarrito(),
                carrito.getIdSucursal(),
                carrito.getEstado(),
                itemsDto,
                total,
                carrito.getFechaActualizacion());
    }

    private ItemCarritoResponse toItemResponse(
            ItemCarrito item,
            Map<Long, BigDecimal> precios,
            Map<Long, Boolean> disponibles,
            Map<Long, String> nombres) {

        Long pid = item.getIdProductoFinal();
        BigDecimal precioActual = precios.getOrDefault(pid, item.getPrecioUnitario());
        boolean disponible = disponibles.getOrDefault(pid, false);
        String nombre = nombres.getOrDefault(pid, "Producto " + pid);
        BigDecimal subtotal = precioActual.multiply(BigDecimal.valueOf(item.getCantidad()));

        return new ItemCarritoResponse(
                item.getIdItemCarrito(),
                pid,
                nombre,
                item.getCantidad(),
                precioActual,
                subtotal,
                item.getNotasEspeciales(),
                item.getFechaAgregado(),
                disponible);
    }

    /**
     * Construye una entidad CarritoCompras temporal (sin persistir) con ítems
     * a partir de los datos provenientes de Redis y los precios de BD.
     */
    public CarritoCompras buildTransientCarrito(
            Long idSucursal,
            String estado,
            Map<String, Object> items,
            Map<Long, BigDecimal> precios) {

        CarritoCompras carrito = CarritoCompras.builder()
                .idSucursal(idSucursal)
                .estado(estado)
                .build();

        List<ItemCarrito> itemList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : items.entrySet()) {
            Long pid = Long.valueOf(entry.getKey());
            @SuppressWarnings("unchecked")
            Map<String, Object> d = (Map<String, Object>) entry.getValue();
            int cant = ((Number) d.get("cantidad")).intValue();
            String notas = (String) d.get("notas");
            BigDecimal precio = precios.getOrDefault(pid, BigDecimal.ZERO);

            itemList.add(ItemCarrito.builder()
                    .carrito(carrito)
                    .idProductoFinal(pid)
                    .cantidad(cant)
                    .precioUnitario(precio)
                    .notasEspeciales(notas)
                    .build());
        }
        carrito.setItems(itemList);
        return carrito;
    }
}
