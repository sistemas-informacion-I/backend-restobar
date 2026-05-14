package org.restobar.gaira.modulo_comercial.service.producto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_comercial.dto.producto.ProductoRequest;
import org.restobar.gaira.modulo_comercial.dto.producto.ProductoResponse;
import org.restobar.gaira.modulo_comercial.entity.ProductoFinal;
import org.restobar.gaira.modulo_comercial.mapper.producto.ProductoMapper;
import org.restobar.gaira.modulo_comercial.repository.ProductoFinalRepository;
import org.restobar.gaira.security.audit.annotation.Auditable;
import org.restobar.gaira.security.audit.util.AuditableService;
import org.restobar.gaira.security.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.restobar.gaira.shared.storage.service.StorageService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ProductoService implements AuditableService<Long, Object> {

    private final ProductoFinalRepository productoFinalRepository;
    private final ProductoMapper productoMapper;
    private final SecurityUtils securityUtils;
    private final StorageService storageService;

    @Override
    public Object getEntity(Long id) {
        return productoFinalRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> mapToAudit(Object entity) {
        if (entity instanceof ProductoFinal prod) {
            return productoMapper.toAuditMap(prod);
        }
        if (entity instanceof ProductoResponse response) {
            return productoMapper.toAuditMap(response);
        }
        return Map.of();
    }

    @Transactional
    @Auditable(tabla = "producto_final", operacion = "UPDATE", idParamName = "id")
    public ProductoResponse subirImagen(Long id, MultipartFile file) {
        validarSoloSuperusuario();
        
        ProductoFinal producto = productoFinalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        // Si ya tiene imagen, la borramos del storage
        if (producto.getImagenUrl() != null) {
            storageService.delete(producto.getImagenUrl());
        }

        // Subimos la nueva imagen
        String filename = storageService.upload(file, "products");
        producto.setImagenUrl(filename);

        return productoMapper.toResponse(productoFinalRepository.save(producto));
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarTodos(Long idCategoria, Boolean activo) {
        return productoFinalRepository.findByFiltros(idCategoria, activo).stream()
                .map(productoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtenerPorId(Long id) {
        ProductoFinal producto = productoFinalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        return productoMapper.toResponse(producto);
    }

    @Transactional
    @Auditable(tabla = "producto_final", operacion = "INSERT")
    public ProductoResponse crear(ProductoRequest dto) {
        validarSoloSuperusuario();

        if (productoFinalRepository.existsByCodigo(dto.codigo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un producto con ese código");
        }
        ProductoFinal producto = productoMapper.toEntity(dto);
        return productoMapper.toResponse(productoFinalRepository.save(producto));
    }

    @Transactional
    @Auditable(tabla = "producto_final", operacion = "UPDATE", idParamName = "id")
    public ProductoResponse actualizar(Long id, ProductoRequest dto) {
        validarSoloSuperusuario();

        ProductoFinal producto = productoFinalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        productoMapper.updateEntity(producto, dto);

        return productoMapper.toResponse(productoFinalRepository.save(producto));
    }

    @Transactional
    @Auditable(tabla = "producto_final", operacion = "DELETE", idParamName = "id")
    public void eliminar(Long id) {
        validarSoloSuperusuario();

        ProductoFinal producto = productoFinalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        producto.setActivo(false);
        productoFinalRepository.save(producto);
    }

    private void validarSoloSuperusuario() {
        Usuario usuario = securityUtils.getCurrentUser();
        if (usuario == null || !"S".equals(usuario.getTipoUsuario())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo el superusuario puede administrar productos maestros");
        }
    }
}
