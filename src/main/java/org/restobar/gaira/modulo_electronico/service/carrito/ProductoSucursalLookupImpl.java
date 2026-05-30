package org.restobar.gaira.modulo_electronico.service.carrito;

import lombok.RequiredArgsConstructor;
import org.restobar.gaira.modulo_comercial.entity.ProductoSucursal;
import org.restobar.gaira.modulo_comercial.repository.ProductoSucursalRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación de {@link CarritoService.ProductoSucursalLookup} que delega
 * en el repositorio JPA del módulo comercial.
 *
 * Al estar en el mismo paquete que {@link CarritoService}, Spring la detecta
 * automáticamente e inyecta el bean donde se necesite.
 */
@Component
@RequiredArgsConstructor
public class ProductoSucursalLookupImpl implements CarritoService.ProductoSucursalLookup {

    private final ProductoSucursalRepository productoSucursalRepository;

    @Override
    public Optional<CarritoService.ProductoSucursalProjection> findByIdProductoFinalAndIdSucursal(
            Long idProductoFinal, Long idSucursal) {

        return productoSucursalRepository
                .findByIdProductoFinalAndIdSucursal(idProductoFinal, idSucursal)
                .map(ProductoSucursalLookupImpl::toProjection);
    }

    @Override
    public Map<Long, CarritoService.ProductoSucursalProjection> findByIdSucursalAndIdProductoFinalIn(
            Long idSucursal, List<Long> ids) {

        return productoSucursalRepository.findByIdSucursalAndIdProductoFinalIn(idSucursal, ids).stream()
                .collect(Collectors.toMap(
                        ProductoSucursal::getIdProductoFinal,
                        ProductoSucursalLookupImpl::toProjection));
    }

    private static CarritoService.ProductoSucursalProjection toProjection(ProductoSucursal ps) {
        return new CarritoService.ProductoSucursalProjection() {
            @Override public boolean isDisponible()       { return ps.isDisponible(); }
            @Override public BigDecimal getPrecio()        { return ps.getPrecio(); }
            @Override public String getNombreProducto()    {
                return ps.getProductoFinal() != null ? ps.getProductoFinal().getNombre() : "Producto " + ps.getIdProductoFinal();
            }
        };
    }
}
