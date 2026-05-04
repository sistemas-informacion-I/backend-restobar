package org.restobar.gaira.modulo_acceso.dto.perfil;

import org.restobar.gaira.modulo_acceso.dto.usuario.ClienteResponse;
import org.restobar.gaira.modulo_acceso.dto.usuario.EmpleadoResponse;
import java.time.LocalDateTime;

public record PerfilPersonalResponse(
    Long idUsuario,
    String ci,
    String nombre,
    String apellido,
    String username,
    String telefono,
    String sexo,
    String correo,
    String direccion,
    LocalDateTime fechaRegistro,
    ClienteResponse cliente,
    EmpleadoResponse empleado
) {}
