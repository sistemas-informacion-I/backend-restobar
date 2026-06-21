package org.restobar.gaira.modulo_electronico.service.reserva;

import lombok.RequiredArgsConstructor;
import org.restobar.gaira.modulo_acceso.entity.Cliente;
import org.restobar.gaira.modulo_acceso.entity.Empleado;
import org.restobar.gaira.modulo_acceso.repository.ClienteRepository;
import org.restobar.gaira.modulo_acceso.repository.EmpleadoRepository;
import org.restobar.gaira.modulo_electronico.dto.reserva.*;
import org.restobar.gaira.modulo_electronico.entity.Reserva;
import org.restobar.gaira.modulo_electronico.entity.ReservaMesa;
import org.restobar.gaira.modulo_electronico.mapper.reserva.ReservaMapper;
import org.restobar.gaira.modulo_electronico.repository.ReservaRepository;
import org.restobar.gaira.modulo_operaciones.entity.Mesa;
import org.restobar.gaira.modulo_operaciones.repository.MesaRepository;
import org.restobar.gaira.modulo_operaciones.repository.SucursalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private static final int DURACION_DEFAULT_HORAS = 2;
    private static final int TOLERANCIA_NO_SHOW_MINUTOS = 15;
    private static final Set<String> ESTADOS_ACTIVOS = Set.of("PENDIENTE", "CONFIRMADA", "EN_CURSO");

    private final ReservaRepository reservaRepository;
    private final MesaRepository mesaRepository;
    private final SucursalRepository sucursalRepository;
    private final ClienteRepository clienteRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ReservaMapper reservaMapper;

    @Transactional(readOnly = true)
    public List<DisponibilidadMesaResponse> consultarDisponibilidad(
            Long idSucursal,
            LocalDate fechaReserva,
            LocalTime horaInicio,
            LocalTime horaFin) {

        validarSucursal(idSucursal);
        LocalTime horaFinFinal = resolverHoraFin(horaInicio, horaFin);

        return mesaRepository.findBySucursalId(idSucursal).stream()
                .map(mesa -> toDisponibilidad(mesa, fechaReserva, horaInicio, horaFinFinal))
                .toList();
    }

    @Transactional
    public ReservaResponse crearReserva(CrearReservaRequest request, Long idClienteAutenticado) {
        validarSucursal(request.idSucursal());
        LocalTime horaFin = resolverHoraFin(request.horaInicio(), request.horaFin());

        List<Mesa> mesas = request.idsMesa().stream()
                .distinct()
                .map(this::requireMesa)
                .toList();

        validarMesasParaReserva(mesas, request.idSucursal(), request.fechaReserva(), request.horaInicio(), horaFin);

        int capacidadTotal = mesas.stream().mapToInt(Mesa::getCapacidadPersonas).sum();
        if (capacidadTotal < request.cantidadPersonas()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La capacidad de las mesas seleccionadas no alcanza para la cantidad de personas");
        }

        Cliente cliente = idClienteAutenticado != null
                ? clienteRepository.findByUsuario_IdUsuario(idClienteAutenticado).orElse(null)
                : null;

        String nombreCliente = resolverNombreCliente(request, cliente);
        if (nombreCliente == null || nombreCliente.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Se requiere el nombre del cliente para registrar la reserva");
        }

        Reserva reserva = Reserva.builder()
                .idSucursal(request.idSucursal())
                .idCliente(cliente != null ? cliente.getIdCliente() : null)
                .clienteNombre(nombreCliente)
                .clienteTelefono(request.clienteTelefono())
                .clienteCorreo(request.clienteCorreo())
                .fechaReserva(request.fechaReserva())
                .horaInicio(request.horaInicio())
                .horaFin(horaFin)
                .cantidadPersonas(request.cantidadPersonas())
                .estado("PENDIENTE")
                .observaciones(request.observaciones())
                .build();

        mesas.forEach(mesa -> reserva.getMesas().add(ReservaMesa.builder()
                .reserva(reserva)
                .mesa(mesa)
                .build()));

        return reservaMapper.toResponse(reservaRepository.save(reserva));
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> listarReservas(Long idSucursal, LocalDate fechaReserva, String estado) {
        validarSucursal(idSucursal);
        return reservaRepository.findPanelReservas(idSucursal, fechaReserva, estado).stream()
                .map(reservaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservaResponse obtenerReserva(Long idReserva) {
        return reservaMapper.toResponse(requireReserva(idReserva));
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> historialCliente(Long idClienteAutenticado) {
        if (idClienteAutenticado == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Debe iniciar sesion para ver su historial de reservas");
        }
        Cliente cliente = clienteRepository.findByUsuario_IdUsuario(idClienteAutenticado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        return reservaRepository.findByIdClienteOrderByFechaReservaDescHoraInicioDesc(cliente.getIdCliente()).stream()
                .map(reservaMapper::toResponse)
                .toList();
    }

    @Transactional
    public ReservaResponse confirmar(Long idReserva, Long idEmpleado, Long idUsuarioEmpleado) {
        Long idEmpleadoFinal = resolverIdEmpleado(idEmpleado, idUsuarioEmpleado);
        Reserva reserva = requireReserva(idReserva);
        requireEstado(reserva, "PENDIENTE");

        reserva.setEstado("CONFIRMADA");
        reserva.setIdEmpleadoConfirmacion(idEmpleadoFinal);
        reserva.setFechaConfirmacion(LocalDateTime.now());
        return reservaMapper.toResponse(reserva);
    }

    @Transactional
    public ReservaResponse checkIn(Long idReserva, Long idEmpleado, Long idUsuarioEmpleado) {
        Long idEmpleadoFinal = resolverIdEmpleado(idEmpleado, idUsuarioEmpleado);
        Reserva reserva = requireReserva(idReserva);
        if (!"CONFIRMADA".equals(reserva.getEstado()) && !"PENDIENTE".equals(reserva.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Solo se puede hacer check-in de reservas pendientes o confirmadas");
        }

        reserva.setEstado("EN_CURSO");
        reserva.setIdEmpleadoCheckIn(idEmpleadoFinal);
        reserva.setFechaCheckIn(LocalDateTime.now());
        return reservaMapper.toResponse(reserva);
    }

    @Transactional
    public ReservaResponse cancelar(Long idReserva, String motivo) {
        Reserva reserva = requireReserva(idReserva);
        if ("EN_CURSO".equals(reserva.getEstado()) || "NO_ASISTIO".equals(reserva.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La reserva ya no puede cancelarse desde este flujo");
        }

        reserva.setEstado("CANCELADA");
        reserva.setMotivoCancelacion(motivo);
        reserva.setFechaCancelacion(LocalDateTime.now());
        return reservaMapper.toResponse(reserva);
    }

    @Transactional
    public ReservaResponse marcarNoAsistio(Long idReserva, Long idEmpleado, Long idUsuarioEmpleado) {
        Long idEmpleadoFinal = resolverIdEmpleado(idEmpleado, idUsuarioEmpleado);
        Reserva reserva = requireReserva(idReserva);
        if (!"CONFIRMADA".equals(reserva.getEstado()) && !"PENDIENTE".equals(reserva.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Solo se puede marcar no-show en reservas pendientes o confirmadas");
        }

        LocalDateTime limiteNoShow = LocalDateTime
                .of(reserva.getFechaReserva(), reserva.getHoraInicio())
                .plusMinutes(TOLERANCIA_NO_SHOW_MINUTOS);
        if (LocalDateTime.now().isBefore(limiteNoShow)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Aun no se alcanzo la tolerancia para marcar no asistio");
        }

        reserva.setEstado("NO_ASISTIO");
        reserva.setIdEmpleadoCheckIn(idEmpleadoFinal);
        reserva.setFechaCancelacion(LocalDateTime.now());
        return reservaMapper.toResponse(reserva);
    }

    private DisponibilidadMesaResponse toDisponibilidad(
            Mesa mesa,
            LocalDate fechaReserva,
            LocalTime horaInicio,
            LocalTime horaFin) {

        if (!Boolean.TRUE.equals(mesa.getActivo())) {
            return disponibilidad(mesa, "NO_DISPONIBLE", false, "Mesa inactiva");
        }
        if (!"DISPONIBLE".equals(mesa.getDisponibilidad())) {
            return disponibilidad(mesa, "NO_DISPONIBLE", false, "Mesa no disponible operativamente");
        }

        boolean reservada = reservaRepository.existsReservaActivaParaMesa(
                mesa.getIdMesa(), fechaReserva, horaInicio, horaFin, ESTADOS_ACTIVOS);

        if (reservada) {
            return disponibilidad(mesa, "OCUPADO_RESERVADO", false, "Mesa ocupada o reservada en ese horario");
        }
        return disponibilidad(mesa, "DISPONIBLE", true, null);
    }

    private DisponibilidadMesaResponse disponibilidad(
            Mesa mesa,
            String estadoPlano,
            boolean disponible,
            String motivo) {

        return new DisponibilidadMesaResponse(
                mesa.getIdMesa(),
                mesa.getSector() != null ? mesa.getSector().getIdSector() : null,
                mesa.getNumeroMesa(),
                mesa.getCapacidadPersonas(),
                estadoPlano,
                disponible,
                motivo);
    }

    private void validarMesasParaReserva(
            List<Mesa> mesas,
            Long idSucursal,
            LocalDate fechaReserva,
            LocalTime horaInicio,
            LocalTime horaFin) {

        for (Mesa mesa : mesas) {
            Long mesaSucursal = mesa.getSector() != null && mesa.getSector().getSucursal() != null
                    ? mesa.getSector().getSucursal().getIdSucursal()
                    : null;

            if (!idSucursal.equals(mesaSucursal)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Todas las mesas deben pertenecer a la sucursal seleccionada");
            }

            DisponibilidadMesaResponse disponibilidad = toDisponibilidad(mesa, fechaReserva, horaInicio, horaFin);
            if (!Boolean.TRUE.equals(disponibilidad.disponible())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "La mesa " + mesa.getNumeroMesa() + " no esta disponible: " + disponibilidad.motivo());
            }
        }
    }

    private Reserva requireReserva(Long idReserva) {
        return reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));
    }

    private Mesa requireMesa(Long idMesa) {
        return mesaRepository.findById(idMesa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));
    }

    private void validarSucursal(Long idSucursal) {
        if (!sucursalRepository.existsById(idSucursal)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada");
        }
    }

    private void validarEmpleado(Long idEmpleado) {
        if (idEmpleado == null || !empleadoRepository.existsById(idEmpleado)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado");
        }
    }

    private Long resolverIdEmpleado(Long idEmpleado, Long idUsuarioEmpleado) {
        if (idEmpleado != null) {
            validarEmpleado(idEmpleado);
            return idEmpleado;
        }

        if (idUsuarioEmpleado == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "No se pudo identificar al empleado autenticado");
        }

        Empleado empleado = empleadoRepository.findByUsuario_IdUsuario(idUsuarioEmpleado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado"));
        return empleado.getIdEmpleado();
    }

    private void requireEstado(Reserva reserva, String estadoEsperado) {
        if (!estadoEsperado.equals(reserva.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La reserva debe estar en estado " + estadoEsperado);
        }
    }

    private LocalTime resolverHoraFin(LocalTime horaInicio, LocalTime horaFin) {
        LocalTime horaFinFinal = horaFin != null ? horaFin : horaInicio.plusHours(DURACION_DEFAULT_HORAS);
        if (!horaFinFinal.isAfter(horaInicio)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La hora fin debe ser posterior a la hora inicio");
        }
        return horaFinFinal;
    }

    private String resolverNombreCliente(CrearReservaRequest request, Cliente cliente) {
        if (request.clienteNombre() != null && !request.clienteNombre().isBlank()) {
            return request.clienteNombre();
        }
        if (cliente != null && cliente.getUsuario() != null) {
            String nombre = cliente.getUsuario().getNombre();
            String apellido = cliente.getUsuario().getApellido();
            return ((nombre != null ? nombre : "") + " " + (apellido != null ? apellido : "")).trim();
        }
        return null;
    }
}
