package org.restobar.gaira.modulo_electronico.controller.reserva;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.restobar.gaira.modulo_electronico.dto.reserva.*;
import org.restobar.gaira.modulo_electronico.service.reserva.ReservaService;
import org.restobar.gaira.security.userdetails.ApplicationUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @GetMapping("/disponibilidad")
    public ResponseEntity<List<DisponibilidadMesaResponse>> disponibilidad(
            @RequestParam Long idSucursal,
            @RequestParam LocalDate fechaReserva,
            @RequestParam LocalTime horaInicio,
            @RequestParam(required = false) LocalTime horaFin) {

        return ResponseEntity.ok(reservaService.consultarDisponibilidad(
                idSucursal, fechaReserva, horaInicio, horaFin));
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> crear(
            @Valid @RequestBody CrearReservaRequest request,
            Authentication authentication) {

        ReservaResponse response = reservaService.crearReserva(request, resolveIdCliente(authentication));
        return ResponseEntity
                .created(URI.create("/reservas/" + response.idReserva()))
                .body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERUSER','SUPERUSUARIO','ADMIN','ADMINISTRADOR','MESERO')")
    public ResponseEntity<List<ReservaResponse>> listar(
            @RequestParam Long idSucursal,
            @RequestParam LocalDate fechaReserva,
            @RequestParam(required = false) String estado) {

        return ResponseEntity.ok(reservaService.listarReservas(idSucursal, fechaReserva, estado));
    }

    @GetMapping("/{idReserva}")
    public ResponseEntity<ReservaResponse> obtener(@PathVariable Long idReserva) {
        return ResponseEntity.ok(reservaService.obtenerReserva(idReserva));
    }

    @GetMapping("/cliente/historial")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReservaResponse>> historialCliente(Authentication authentication) {
        return ResponseEntity.ok(reservaService.historialCliente(resolveIdClienteRequerido(authentication)));
    }

    @PatchMapping("/{idReserva}/confirmar")
    @PreAuthorize("hasAnyRole('SUPERUSER','SUPERUSUARIO','ADMIN','ADMINISTRADOR','MESERO')")
    public ResponseEntity<ReservaResponse> confirmar(
            @PathVariable Long idReserva,
            @RequestBody(required = false) ActualizarEstadoReservaRequest request,
            Authentication authentication) {

        Long idEmpleado = request != null ? request.idEmpleado() : null;
        return ResponseEntity.ok(reservaService.confirmar(
                idReserva, idEmpleado, resolveIdUsuario(authentication)));
    }

    @PatchMapping("/{idReserva}/check-in")
    @PreAuthorize("hasAnyRole('SUPERUSER','SUPERUSUARIO','ADMIN','ADMINISTRADOR','MESERO')")
    public ResponseEntity<ReservaResponse> checkIn(
            @PathVariable Long idReserva,
            @RequestBody(required = false) ActualizarEstadoReservaRequest request,
            Authentication authentication) {

        Long idEmpleado = request != null ? request.idEmpleado() : null;
        return ResponseEntity.ok(reservaService.checkIn(
                idReserva, idEmpleado, resolveIdUsuario(authentication)));
    }

    @PatchMapping("/{idReserva}/cancelar")
    public ResponseEntity<ReservaResponse> cancelar(
            @PathVariable Long idReserva,
            @RequestBody(required = false) ActualizarEstadoReservaRequest request) {

        String motivo = request != null ? request.motivo() : null;
        return ResponseEntity.ok(reservaService.cancelar(idReserva, motivo));
    }

    @PatchMapping("/{idReserva}/no-asistio")
    @PreAuthorize("hasAnyRole('SUPERUSER','SUPERUSUARIO','ADMIN','ADMINISTRADOR','MESERO')")
    public ResponseEntity<ReservaResponse> noAsistio(
            @PathVariable Long idReserva,
            @RequestBody(required = false) ActualizarEstadoReservaRequest request,
            Authentication authentication) {

        Long idEmpleado = request != null ? request.idEmpleado() : null;
        return ResponseEntity.ok(reservaService.marcarNoAsistio(
                idReserva, idEmpleado, resolveIdUsuario(authentication)));
    }

    private Long resolveIdCliente(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof ApplicationUserPrincipal principal
                && "C".equals(principal.getTipoUsuario())) {
            return principal.getIdUsuario();
        }
        return null;
    }

    private Long resolveIdClienteRequerido(Authentication authentication) {
        Long id = resolveIdCliente(authentication);
        if (id == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED,
                    "Debe iniciar sesion como cliente");
        }
        return id;
    }

    private Long resolveIdUsuario(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof ApplicationUserPrincipal principal) {
            return principal.getIdUsuario();
        }
        return null;
    }
}
