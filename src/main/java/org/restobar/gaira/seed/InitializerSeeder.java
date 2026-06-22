package org.restobar.gaira.seed;

import java.math.BigDecimal;
import java.util.List;

import org.restobar.gaira.modulo_acceso.entity.Permiso;
import org.restobar.gaira.modulo_acceso.entity.Rol;
import org.restobar.gaira.modulo_acceso.entity.RolPermiso;
import org.restobar.gaira.modulo_acceso.entity.RolUsuario;
import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_acceso.repository.PermisoRepository;
import org.restobar.gaira.modulo_acceso.repository.RolPermisoRepository;
import org.restobar.gaira.modulo_acceso.repository.RolRepository;
import org.restobar.gaira.modulo_acceso.repository.RolUsuarioRepository;
import org.restobar.gaira.modulo_acceso.repository.UsuarioRepository;
import org.restobar.gaira.modulo_electronico.entity.MetodoPago;
import org.restobar.gaira.modulo_electronico.repository.MetodoPagoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * Seeder centralizado para la configuración inicial de seguridad y accesos.
 * Organiza los permisos por módulos y define una jerarquía clara entre SUPERUSER y ADMIN.
 */
@Component
@RequiredArgsConstructor
@SuppressWarnings("null")
public class InitializerSeeder implements CommandLineRunner {

    private final PermisoRepository permisoRepository;
    private final RolRepository rolRepository;
    private final RolPermisoRepository rolPermisoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${seed.admin.username}") private String adminUsername;
    @Value("${seed.admin.password}") private String adminPassword;
    @Value("${seed.admin.email}") private String adminEmail;

    @Override
    @Transactional
    public void run(String... args) {
        // 1. Inicializar Roles Principales
        Rol superuser = seedRol("SUPERUSER", "Acceso Total al Sistema (Administrador Global)", 100);
        Rol admin = seedRol("ADMIN", "Gestión Operativa de Sucursal y Personal", 90);
        Rol cliente = seedRol("CLIENTE", "Acceso para Clientes Finales del Restobar", 2);
        
        // 2. Sembrar Permisos por Módulos
        seedModulo("USUARIOS", "users", List.of("create", "read", "update", "delete"));
        seedModulo("ROLES", "roles", List.of("create", "read", "update", "delete"));
        seedModulo("PERMISOS", "permissions", List.of("create", "read", "update", "delete"));
        seedModulo("AUDITORIA", "audit", List.of("read"));
        seedModulo("SESIONES", "sessions", List.of("read", "revoke"));

        seedModulo("CLIENTES", "clients", List.of("create", "read", "update", "delete"));
        seedModulo("PROVEEDORES", "providers", List.of("create", "read", "update", "delete"));
        seedModulo("EMPLEADOS", "employees", List.of("create", "read", "update", "delete"));

        seedModulo("SUCURSALES", "sucursales", List.of("create", "read", "update", "delete"));
        seedModulo("SECTORES", "sectores", List.of("create", "read", "update", "delete"));
        seedModulo("MESAS", "mesas", List.of("create", "read", "update", "delete"));
        seedModulo("COMANDAS", "comandas", List.of("create", "read", "update", "delete"));

        seedModulo("INVENTARIO", "inventario", List.of("create", "read", "update", "delete"));
        seedModulo("CATEGORIAS", "categories", List.of("create", "read", "update"));
        seedModulo("PRODUCTOS", "producto", List.of("create", "read", "update", "delete"));
        seedModulo("RECETAS", "receta", List.of("create", "read", "update", "delete"));
        seedModulo("COMPRAS", "compras", List.of("create", "read", "update", "delete"));
        seedModulo("CATALOGO", "catalogo", List.of("read", "update"));
        seedModulo("VENTAS", "ventas", List.of("create", "read", "update", "delete"));
        seedModulo("ENTREGAS", "entregas", List.of("create", "read", "update"));
        // CU22 – Gestionar Caja: create=abrir, read=consultar/arqueo, update=movimientos/cierre
        seedModulo("CAJA", "caja", List.of("create", "read", "update"));
        seedModulo("DASHBOARD", "dashboard", List.of("read"));

        // 3. Sincronizar Permisos a Roles
        syncSuperUserPermissions(superuser);
        syncAdminPermissions(admin);
        syncClientePermissions(cliente);

        // 4. Asegurar Existencia de Usuario Maestro
        ensureAdminUser(superuser);
        
        // 5. Roles Operativos Adicionales (Sin permisos base por ahora)
        seedRol("CAJERO", "Personal encargado de cobros", 10);
        seedRol("MESERO", "Personal de atención a clientes", 5);
        seedRol("COCINERO", "Personal de producción", 5);
        seedRol("BARTENDER", "Personal de bar", 5);

        // 6. Métodos de Pago
        seedMetodosPago();
    }

    private void seedMetodosPago() {
        seedMetodoPago("Efectivo", "Pago en efectivo en punto de venta", BigDecimal.ZERO, BigDecimal.ZERO, true);
        seedMetodoPago("QR Pago Móvil", "Pago mediante código QR (BCP, etc.) (Próximamente)", BigDecimal.valueOf(2.0), BigDecimal.ZERO, false);
        seedMetodoPago("PayPal", "Pago a través de PayPal (online)", BigDecimal.valueOf(5.9), BigDecimal.valueOf(0.30), true);
        seedMetodoPago("Tarjeta Débito", "Pago con tarjeta de débito Visa/Mastercard (Próximamente)", BigDecimal.valueOf(3.5), BigDecimal.valueOf(2.00), false);
    }

    private void seedMetodoPago(String nombre, String descripcion, BigDecimal comisionPorcentaje, BigDecimal comisionFija, Boolean activo) {
        if (metodoPagoRepository.findAll().stream().noneMatch(m -> m.getNombre().equals(nombre))) {
            metodoPagoRepository.save(MetodoPago.builder()
                    .nombre(nombre)
                    .descripcion(descripcion)
                    .comisionPorcentaje(comisionPorcentaje)
                    .comisionFija(comisionFija)
                    .activo(activo)
                    .build());
        }
    }

    private Rol seedRol(String nombre, String descripcion, int nivelAcceso) {
        return rolRepository.findByNombre(nombre)
                .orElseGet(() -> rolRepository.save(Rol.builder()
                        .nombre(nombre)
                        .descripcion(descripcion)
                        .nivelAcceso(nivelAcceso)
                        .activo(true)
                        .build()));
    }

    private void seedModulo(String modulo, String prefijo, List<String> acciones) {
        for (String accion : acciones) {
            String nombre = prefijo + ":" + accion;
            if (!permisoRepository.existsByNombre(nombre)) {
                permisoRepository.save(Permiso.builder()
                        .nombre(nombre)
                        .modulo(modulo)
                        .accion(accion.toUpperCase())
                        .descripcion(accion.toUpperCase() + " en el módulo de " + modulo)
                        .activo(true)
                        .build());
            }
        }
    }

    private void syncSuperUserPermissions(Rol rol) {
        // El SUPERUSER tiene acceso a TODO lo que exista en la tabla de permisos
        permisoRepository.findAll().forEach(p -> seedRolPermiso(rol, p));
    }

    private void syncAdminPermissions(Rol rol) {
        // Módulos que un ADMIN puede gestionar completamente (Staff, Stock, Clientes)
        List<String> modulosGestionable = List.of(
            "CATEGORIAS", "INVENTARIO", "EMPLEADOS", "CLIENTES", "PROVEEDORES",
            "SECTORES", "MESAS", "COMANDAS", "COMPRAS", "PRODUCTOS", "RECETAS", "CATALOGO",
            "VENTAS", "ENTREGAS", "CAJA", "DASHBOARD"
        );
        
        permisoRepository.findAll().forEach(p -> {
            if (modulosGestionable.contains(p.getModulo())) {
                seedRolPermiso(rol, p);
            }
        });

        // El ADMIN tiene permisos de SOLO LECTURA para infraestructura crítica
        permisoRepository.findByNombre("sucursales:read").ifPresent(p -> seedRolPermiso(rol, p));
        permisoRepository.findByNombre("roles:read").ifPresent(p -> seedRolPermiso(rol, p));
        
        // El ADMIN puede gestionar usuarios (su personal)
        permisoRepository.findByNombre("users:read").ifPresent(p -> seedRolPermiso(rol, p));
        permisoRepository.findByNombre("users:update").ifPresent(p -> seedRolPermiso(rol, p));
    }

    private void syncClientePermissions(Rol rol) {
        // El CLIENTE no tiene permisos administrativos.
        // Su acceso se limita a lo que el controlador permita por @AuthenticationPrincipal (su propio perfil)
        // sin necesidad de autoridades globales.
    }

    private void syncOperationalStaffPermissions(Rol mesero, Rol cajero, Rol cocinero, Rol bartender) {
        // Todo el personal operativo puede VISUALIZAR sectores y mesas (solo lectura).
        // La gestión (crear/editar/eliminar) queda reservada a ADMIN/SUPERUSER.
        List.of(mesero, cajero, cocinero, bartender)
            .forEach(rol -> assignPermisos(rol, List.of("sectores:read", "mesas:read")));

        // El MESERO crea y gestiona comandas en su sucursal (CU14). Puede asignar el
        // cliente a la comanda (necesario para luego facturarla en CU15).
        assignPermisos(mesero, List.of(
            "comandas:create", "comandas:read", "comandas:update", "producto:read", "clients:read"
        ));

        // El CAJERO consulta y cierra comandas para facturar las ventas presenciales (CU15).
        assignPermisos(cajero, List.of(
            "comandas:read", "comandas:update", "producto:read", "clients:read"
        ));

        // COCINERO y BARTENDER trabajan la preparación vía su rol (controlador por hasAnyRole).
        // Además pueden gestionar recetas (crear/editar), para lo cual necesitan leer
        // productos e insumos de inventario.
        List.of(cocinero, bartender)
            .forEach(rol -> assignPermisos(rol, List.of(
                "receta:read", "receta:create", "receta:update",
                "producto:read", "inventario:read"
            )));
    }

    private void assignPermisos(Rol rol, List<String> permisos) {
        permisos.forEach(nombre ->
            permisoRepository.findByNombre(nombre).ifPresent(p -> seedRolPermiso(rol, p)));
    }

    private void syncCajeroPermissions(Rol rol) {
        // Permisos de ventas presenciales (CU15) y gestión de caja (CU22):
        // el cajero gestiona los módulos VENTAS y CAJA por completo.
        List<String> modulosCajero = List.of("VENTAS", "CAJA");
        permisoRepository.findAll().forEach(p -> {
            if (modulosCajero.contains(p.getModulo())) {
                seedRolPermiso(rol, p);
            }
        });
    }

    private void syncRepartidorPermissions(Rol rol) {
        // Permisos de entregas para repartidores (CU30).
        // Lectura de entregas + lectura de comandas, clientes y sucursales (datos necesarios).
        assignPermisos(rol, List.of(
                "entregas:read", "entregas:update",
                "comandas:read", "clients:read",
                "producto:read", "sucursales:read"
        ));
    }

    private void seedRolPermiso(Rol rol, Permiso permiso) {
        if (!rolPermisoRepository.existsByRol_IdRolAndPermiso_IdPermiso(rol.getIdRol(), permiso.getIdPermiso())) {
            rolPermisoRepository.save(RolPermiso.builder()
                    .rol(rol)
                    .permiso(permiso)
                    .activo(true)
                    .build());
        }
    }

    private void ensureAdminUser(Rol rol) {
        Usuario admin = usuarioRepository.findByUsername(adminUsername)
                .orElseGet(() -> usuarioRepository.save(Usuario.builder()
                        .ci("SYS-0001")
                        .nombre("System")
                        .apellido("Administrator")
                        .username(adminUsername)
                        .passwordHash(passwordEncoder.encode(adminPassword))
                        .correo(adminEmail)
                        .sexo("O")
                        .tipoUsuario("S")
                        .estadoAcceso("HABILITADO")
                        .activo(true)
                        .build()));

        if (!rolUsuarioRepository.existsByUsuario_IdUsuarioAndRol_IdRol(admin.getIdUsuario(), rol.getIdRol())) {
            rolUsuarioRepository.save(RolUsuario.builder()
                    .usuario(admin)
                    .rol(rol)
                    .activo(true)
                    .build());
        }
    }
}
