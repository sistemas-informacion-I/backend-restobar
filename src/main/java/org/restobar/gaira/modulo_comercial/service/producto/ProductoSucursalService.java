package org.restobar.gaira.modulo_comercial.service.producto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_comercial.dto.producto.ProductoSucursalRequest;
import org.restobar.gaira.modulo_comercial.dto.producto.ProductoSucursalResponse;
import org.restobar.gaira.modulo_comercial.entity.ProductoFinal;
import org.restobar.gaira.modulo_comercial.entity.ProductoSucursal;
import org.restobar.gaira.modulo_comercial.entity.ProductoSucursalId;
import org.restobar.gaira.modulo_comercial.mapper.producto.ProductoSucursalMapper;
import org.restobar.gaira.modulo_comercial.repository.ProductoFinalRepository;
import org.restobar.gaira.modulo_comercial.repository.ProductoSucursalRepository;
import org.restobar.gaira.modulo_operaciones.entity.Sucursal;
import org.restobar.gaira.modulo_operaciones.repository.SucursalRepository;
import org.restobar.gaira.security.audit.annotation.Auditable;
import org.restobar.gaira.security.audit.util.AuditableService;
import org.restobar.gaira.security.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ProductoSucursalService implements AuditableService<ProductoSucursalId, Object> {

    private final ProductoSucursalRepository productoSucursalRepository;
    private final ProductoSucursalMapper productoSucursalMapper;
    private final ProductoFinalRepository productoFinalRepository;
    private final SucursalRepository sucursalRepository;
    private final SecurityUtils securityUtils;

    @Override
    public Object getEntity(ProductoSucursalId id) {
        return productoSucursalRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof ProductoSucursal prodSuc) {
            return productoSucursalMapper.toAuditMap(prodSuc);
        }
        return Map.of();
    }

    @Transactional(readOnly = true)
    public List<ProductoSucursalResponse> listarPorSucursal(Long idSucursal) {
        Long idSucursalFinal = resolverSucursalSegunRol(idSucursal);
        return productoSucursalRepository.findByIdSucursal(idSucursalFinal).stream()
                .map(productoSucursalMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoSucursalResponse obtenerPorProductoYSucursal(Long idProducto, Long idSucursal) {
        Long idSucursalFinal = resolverSucursalSegunRol(idSucursal);
        ProductoSucursal productoSucursal = productoSucursalRepository.findByIdProductoFinalAndIdSucursal(idProducto, idSucursalFinal)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no asignado a esta sucursal"));
        return productoSucursalMapper.toResponse(productoSucursal);
    }

    @Transactional
    @Auditable(tabla = "producto_sucursal", operacion = "INSERT")
    public ProductoSucursalResponse asignarASucursal(Long idProducto, ProductoSucursalRequest dto) {
        Long idSucursalFinal = resolverSucursalSegunRol(dto.idSucursal());
        validarProductoActivo(idProducto);
        validarSucursalActiva(idSucursalFinal);

        ProductoSucursalId id = new ProductoSucursalId(idProducto, idSucursalFinal);
        if (productoSucursalRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto ya asignado a esta sucursal");
        }
        
        // Re-creamos el Record con la sucursal resuelta si fuera necesario, 
        // pero el mapper toEntity ya usa idSucursal del dto si es admin, 
        // o podemos pasar el idSucursalFinal explícitamente.
        ProductoSucursal productoSucursal = ProductoSucursal.builder()
                .idProductoFinal(idProducto)
                .idSucursal(idSucursalFinal)
                .precio(dto.precio())
                .disponible(dto.disponible() == null ? true : dto.disponible())
                .activo(dto.activo() == null ? true : dto.activo())
                .build();
                
        return productoSucursalMapper.toResponse(productoSucursalRepository.save(productoSucursal));
    }

    @Transactional
    @Auditable(tabla = "producto_sucursal", operacion = "UPDATE")
    public ProductoSucursalResponse actualizarEnSucursal(Long idProducto, Long idSucursal, ProductoSucursalRequest dto) {
        Long idSucursalFinal = resolverSucursalSegunRol(idSucursal);
        ProductoSucursal productoSucursal = productoSucursalRepository.findByIdProductoFinalAndIdSucursal(idProducto, idSucursalFinal)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no asignado a esta sucursal"));

        productoSucursalMapper.updateEntity(productoSucursal, dto);

        return productoSucursalMapper.toResponse(productoSucursalRepository.save(productoSucursal));
    }

    @Transactional
    public void removerDeSucursal(Long idProducto, Long idSucursal) {
        Long idSucursalFinal = resolverSucursalSegunRol(idSucursal);
        ProductoSucursalId id = new ProductoSucursalId(idProducto, idSucursalFinal);
        if (!productoSucursalRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no asignado a esta sucursal");
        }
        productoSucursalRepository.deleteById(id);
    }

    private Long resolverSucursalSegunRol(Long idSucursalRequest) {
        Usuario usuario = securityUtils.getCurrentUser();
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }

        if ("S".equals(usuario.getTipoUsuario())) {
            if (idSucursalRequest == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar la sucursal");
            }
            return idSucursalRequest;
        }

        Long idSucursalUsuario = securityUtils.getCurrentSucursalId();
        if (idSucursalUsuario == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se encontró sucursal para el usuario actual");
        }

        if (idSucursalRequest != null && !idSucursalUsuario.equals(idSucursalRequest)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No puede consultar ni modificar productos de otra sucursal");
        }

        return idSucursalUsuario;
    }

    private void validarProductoActivo(Long idProducto) {
        ProductoFinal producto = productoFinalRepository.findById(idProducto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        if (!producto.isActivo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El producto está inactivo");
        }
    }

    private void validarSucursalActiva(Long idSucursal) {
        Sucursal sucursal = sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada"));
        if (Boolean.FALSE.equals(sucursal.getActivo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La sucursal está inactiva");
        }
    }
}
