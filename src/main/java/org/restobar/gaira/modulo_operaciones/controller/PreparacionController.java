package org.restobar.gaira.modulo_operaciones.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.restobar.gaira.modulo_operaciones.dto.comanda.MarcarListoResponseDTO;
import org.restobar.gaira.modulo_operaciones.dto.comanda.PreparacionQueueResponseDTO;
import org.restobar.gaira.modulo_operaciones.service.preparacion.PreparacionService;
import org.restobar.gaira.security.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controlador para gestionar la preparación de comandas (CU25)
 * Kitchen Display System (KDS) - Pantalla para cocina/barra
 */
@RestController
@RequestMapping("/api/preparacion")
@RequiredArgsConstructor
@Slf4j
public class PreparacionController {

    private final PreparacionService preparacionService;
    private final SecurityUtils securityUtils;

    /**
     * Obtiene la cola de preparación filtrada por estación
     * GET /api/preparacion/cola?estacion=COCINA
     * GET /api/preparacion/cola?estacion=BARRA
     */
    @GetMapping("/cola")
    @PreAuthorize("hasAnyRole('COCINERO', 'BARTENDER', 'GERENTE', 'SUPERUSER', 'SUPERUSUARIO') or @securityUtils.isSuperUser()")
    public ResponseEntity<List<PreparacionQueueResponseDTO>> obtenerColaPorEstacion(
            @RequestParam String estacion,
            @RequestParam(required = false) Long idSucursal) {

        Long sucursalId = resolveSucursal(idSucursal);

        log.info("Obteniendo cola de preparación para estación: {} en sucursal: {}", estacion, sucursalId);
        List<PreparacionQueueResponseDTO> cola = preparacionService.obtenerColaPorEstacion(sucursalId, estacion);

        return ResponseEntity.ok(cola);
    }

    /**
     * Obtiene la cola completa de preparación (sin filtro de estación)
     * Útil para supervisores/gerentes que necesitan ver todo
     * GET /api/preparacion/cola/completa
     */
    @GetMapping("/cola/completa")
    @PreAuthorize("hasAnyRole('GERENTE', 'SUPERUSER', 'SUPERUSUARIO') or @securityUtils.isSuperUser()")
    public ResponseEntity<List<PreparacionQueueResponseDTO>> obtenerColaCompleta(
            @RequestParam(required = false) Long idSucursal) {

        Long sucursalId = resolveSucursal(idSucursal);

        log.info("Obteniendo cola completa de preparación en sucursal: {}", sucursalId);
        List<PreparacionQueueResponseDTO> cola = preparacionService.obtenerColaCompleta(sucursalId);

        return ResponseEntity.ok(cola);
    }

    /**
     * Cocinero/Bartender toma un item para empezar a prepararlo
     * PATCH /api/preparacion/detalles/{idDetalleComanda}/tomar
     * Estado: PENDIENTE → EN_PREPARACION
     */
    @PatchMapping("/detalles/{idDetalleComanda}/tomar")
    @PreAuthorize("hasAnyRole('COCINERO', 'BARTENDER', 'SUPERUSER', 'SUPERUSUARIO') or @securityUtils.isSuperUser()")
    public ResponseEntity<?> tomarItemParaPreparar(
            @PathVariable Long idDetalleComanda) {

        try {
            log.info("Usuario {} tomando item {} para preparación", securityUtils.getCurrentUserId(), idDetalleComanda);
            var item = preparacionService.tomarItem(idDetalleComanda);
            return ResponseEntity.ok(item);
        } catch (ResponseStatusException e) {
            log.warn("Error al tomar item: {}", e.getReason());
            throw e;
        } catch (Exception e) {
            log.error("Error al tomar item", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al tomar item");
        }
    }

    /**
     * Cocinero/Bartender marca un item como LISTO
     * PATCH /api/preparacion/detalles/{idDetalleComanda}/listo
     * Estado: EN_PREPARACION → LISTO
     * Efectos secundarios:
     * - Descuenta inventario automáticamente (FIFO)
     * - Si todos los items están LISTO, marca comanda como LISTA
     */
    @PatchMapping("/detalles/{idDetalleComanda}/listo")
    @PreAuthorize("hasAnyRole('COCINERO', 'BARTENDER', 'SUPERUSER', 'SUPERUSUARIO') or @securityUtils.isSuperUser()")
    public ResponseEntity<?> marcarItemComoListo(
            @PathVariable Long idDetalleComanda) {

        try {
            log.info("Usuario {} marcando item {} como LISTO", securityUtils.getCurrentUserId(), idDetalleComanda);
            MarcarListoResponseDTO response = preparacionService.marcarItemListo(idDetalleComanda);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            log.warn("Error al marcar como listo: {}", e.getReason());
            throw e;
        } catch (Exception e) {
            log.error("Error al marcar como listo", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al marcar como listo");
        }
    }

    // ==================== HELPERS ====================

    /**
     * Resuelve la sucursal del usuario actual
     * Si el usuario es superusuario, debe proporcionar idSucursal
     * Si es empleado, usa su sucursal asignada
     */
    private Long resolveSucursal(Long idSucursal) {
        String tipoUsuario = securityUtils.getCurrentUserTipoUsuario();

        if ("S".equals(tipoUsuario)) { // SUPERUSUARIO
            if (idSucursal == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Superusuario debe especificar idSucursal");
            }
            return idSucursal;
        } else { // EMPLEADO
            Long sucursalId = securityUtils.getCurrentSucursalId();
            if (sucursalId == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No se pudo determinar la sucursal del usuario");
            }
            return sucursalId;
        }
    }
}
