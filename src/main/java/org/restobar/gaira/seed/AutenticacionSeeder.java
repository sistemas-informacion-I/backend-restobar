package org.restobar.gaira.seed;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@SuppressWarnings("null")
public class AutenticacionSeeder implements CommandLineRunner {

        private final PermisoRepository permisoRepository;
        private final RolRepository rolRepository;
        private final RolPermisoRepository rolPermisoRepository;
        private final UsuarioRepository usuarioRepository;
        private final RolUsuarioRepository rolUsuarioRepository;
        private final PasswordEncoder passwordEncoder;

        @Value("${seed.admin.username}")
        private String adminUsername;

        @Value("${seed.admin.password}")
        private String adminPassword;

        @Value("${seed.admin.email}")
        private String adminEmail;

        public AutenticacionSeeder(PermisoRepository permisoRepository,
                        RolRepository rolRepository,
                        RolPermisoRepository rolPermisoRepository,
                        UsuarioRepository usuarioRepository,
                        RolUsuarioRepository rolUsuarioRepository,
                        PasswordEncoder passwordEncoder) {
                this.permisoRepository = permisoRepository;
                this.rolRepository = rolRepository;
                this.rolPermisoRepository = rolPermisoRepository;
                this.usuarioRepository = usuarioRepository;
                this.rolUsuarioRepository = rolUsuarioRepository;
                this.passwordEncoder = passwordEncoder;
        }

        @Override
        @Transactional
        public void run(String... args) {
                List<Permiso> permisosBase = List.of(
                                seedPermiso("users:create", "USUARIOS", "CREAR", "Crear usuarios"),
                                seedPermiso("users:read", "USUARIOS", "LEER", "Leer usuarios"),
                                seedPermiso("users:update", "USUARIOS", "ACTUALIZAR", "Actualizar usuarios"),
                                seedPermiso("users:delete", "USUARIOS", "ELIMINAR", "Eliminar usuarios"),
                                seedPermiso("roles:create", "ROLES", "CREAR", "Crear roles"),
                                seedPermiso("roles:read", "ROLES", "LEER", "Leer roles"),
                                seedPermiso("roles:update", "ROLES", "ACTUALIZAR", "Actualizar roles"),
                                seedPermiso("roles:delete", "ROLES", "ELIMINAR", "Eliminar roles"),
                                seedPermiso("permissions:create", "PERMISOS", "CREAR", "Crear permisos"),
                                seedPermiso("permissions:read", "PERMISOS", "LEER", "Leer permisos"),
                                seedPermiso("permissions:update", "PERMISOS", "ACTUALIZAR", "Actualizar permisos"),
                                seedPermiso("permissions:delete", "PERMISOS", "ELIMINAR", "Eliminar permisos"),
                                seedPermiso("sessions:read", "SESIONES", "LEER", "Leer sesiones"),
                                seedPermiso("sessions:revoke", "SESIONES", "ACTUALIZAR", "Revocar sesiones"),
                                seedPermiso("audit:read", "AUDITORIA", "LEER", "Leer auditoría"),
                                seedPermiso("clients:create", "CLIENTES", "CREAR", "Crear clientes"),
                                seedPermiso("clients:read", "CLIENTES", "LEER", "Leer clientes"),
                                seedPermiso("clients:update", "CLIENTES", "ACTUALIZAR", "Actualizar clientes"),
                                seedPermiso("clients:delete", "CLIENTES", "ELIMINAR", "Eliminar clientes"),
                                seedPermiso("employees:create", "EMPLEADOS", "CREAR", "Crear empleados"),
                                seedPermiso("employees:read", "EMPLEADOS", "LEER", "Leer empleados"),
                                seedPermiso("employees:update", "EMPLEADOS", "ACTUALIZAR", "Actualizar empleados"),
                                seedPermiso("employees:delete", "EMPLEADOS", "ELIMINAR", "Eliminar empleados"),
                                seedPermiso("providers:create", "PROVEEDORES", "CREAR", "Crear proveedores"),
                                seedPermiso("providers:read", "PROVEEDORES", "LEER", "Leer proveedores"),
                                seedPermiso("providers:update", "PROVEEDORES", "ACTUALIZAR", "Actualizar proveedores"),
                                seedPermiso("providers:delete", "PROVEEDORES", "ELIMINAR", "Eliminar proveedores"),
                                // Operaciones
                                seedPermiso("sucursales:create", "SUCURSALES", "CREAR", "Crear sucursales"),
                                seedPermiso("sucursales:read", "SUCURSALES", "LEER", "Leer sucursales"),
                                seedPermiso("sucursales:update", "SUCURSALES", "ACTUALIZAR", "Actualizar sucursales"),
                                seedPermiso("sucursales:delete", "SUCURSALES", "ELIMINAR", "Eliminar sucursales"),
                                seedPermiso("sectores:create", "SECTORES", "CREAR", "Crear sectores"),
                                seedPermiso("sectores:read", "SECTORES", "LEER", "Leer sectores"),
                                seedPermiso("sectores:update", "SECTORES", "ACTUALIZAR", "Actualizar sectores"),
                                seedPermiso("sectores:delete", "SECTORES", "ELIMINAR", "Eliminar sectores"),
                                seedPermiso("mesas:create", "MESAS", "CREAR", "Crear mesas"),
                                seedPermiso("mesas:read", "MESAS", "LEER", "Leer mesas"),
                                seedPermiso("mesas:update", "MESAS", "ACTUALIZAR", "Actualizar mesas"),
                                seedPermiso("mesas:delete", "MESAS", "ELIMINAR", "Eliminar mesas"),
                                // --- CICLOS FUTUROS ---
                                // Inventario
                                seedPermiso("inventory:read", "INVENTARIO", "LEER", "Ver stock e insumos"),
                                seedPermiso("inventory:create", "INVENTARIO", "CREAR", "Registrar nuevos insumos"),
                                seedPermiso("inventory:update", "INVENTARIO", "ACTUALIZAR", "Modificar stock/insumos"),
                                // Ventas
                                seedPermiso("sales:create", "VENTAS", "CREAR_VENTA", "Realizar ventas"),
                                seedPermiso("sales:cancel", "VENTAS", "ANULAR_VENTA", "Anular ventas realizadas"),
                                seedPermiso("sales:history", "VENTAS", "VER_HISTORIAL", "Ver historial de ventas"),
                                // Caja
                                seedPermiso("cash:open", "CAJA", "ABRIR_CAJA", "Abrir turno de caja"),
                                seedPermiso("cash:close", "CAJA", "CERRAR_CAJA", "Cerrar turno de caja"),
                                seedPermiso("cash:movements", "CAJA", "VER_MOVIMIENTOS", "Ver flujo de efectivo"));

                Rol superuserRol = rolRepository.findByNombre("SUPERUSER")
                                .orElseGet(() -> rolRepository.save(Rol.builder()
                                                .nombre("SUPERUSER")
                                                .descripcion("Rol superusuario (Acceso Global)")
                                                .nivelAcceso(100)
                                                .activo(true)
                                                .build()));

                Rol adminRol = rolRepository.findByNombre("ADMIN")
                                .orElseGet(() -> rolRepository.save(Rol.builder()
                                                .nombre("ADMIN")
                                                .descripcion("Rol dueño de sucursal")
                                                .nivelAcceso(90)
                                                .activo(true)
                                                .build()));

                Rol userRol = rolRepository.findByNombre("USER")
                                .orElseGet(() -> rolRepository.save(Rol.builder()
                                                .nombre("USER")
                                                .descripcion("Rol usuario por defecto")
                                                .nivelAcceso(1)
                                                .activo(true)
                                                .build()));

                rolRepository.findByNombre("CLIENTE")
                                .orElseGet(() -> rolRepository.save(Rol.builder()
                                                .nombre("CLIENTE")
                                                .descripcion("Rol para clientes del sistema")
                                                .nivelAcceso(2)
                                                .activo(true)
                                                .build()));

                for (Permiso permiso : permisosBase) {
                        // Ambos tienen los permisos base definidos en la lista inicial (operaciones)
                        seedRolPermiso(superuserRol, permiso);
                        seedRolPermiso(adminRol, permiso);
                }

                // Superuser: Recibe TODO (incluyendo auditoría, roles y sucursales)
                syncAllPermissions(superuserRol);

                // Admin: Recibe permisos operativos y de gestión local
                // (Filtramos para que NO tenga acceso a la infraestructura global)
                syncOperationalPermissions(adminRol);

                Permiso usuarioLeer = permisoRepository.findByNombre("users:read").orElseThrow();
                seedRolPermiso(userRol, usuarioLeer);

                ensureAdminUser(superuserRol);

                // Roles operativos adicionales
                seedRol("CAJERO", "Responsable de caja y cobros", 10);
                seedRol("BARTENDER", "Encargado de la barra y bebidas", 5);
                seedRol("COCINERO", "Personal de cocina", 5);
                seedRol("MESERO", "Atención a mesas y pedidos", 5);
        }

        private void seedRol(String nombre, String descripcion, int nivelAcceso) {
                if (!rolRepository.existsByNombre(nombre)) {
                        rolRepository.save(Rol.builder()
                                        .nombre(nombre)
                                        .descripcion(descripcion)
                                        .nivelAcceso(nivelAcceso)
                                        .activo(true)
                                        .build());
                }
        }

        private Permiso seedPermiso(String nombre, String modulo, String accion, String descripcion) {
                return permisoRepository.findByNombre(nombre)
                                .orElseGet(() -> permisoRepository.save(Permiso.builder()
                                                .nombre(nombre)
                                                .modulo(modulo)
                                                .accion(accion)
                                                .descripcion(descripcion)
                                                .activo(true)
                                                .build()));
        }

        private void syncAllPermissions(Rol rol) {
                for (Permiso permiso : permisoRepository.findAll()) {
                        seedRolPermiso(rol, permiso);
                }
        }

        private void syncOperationalPermissions(Rol rol) {
                // Módulos de infraestructura y seguridad (Solo para Superusuario)
                List<String> modulosRestringidos = List.of("AUDITORIA", "ROLES", "PERMISOS", "SUCURSALES", "SESIONES", "USUARIOS");
                
                for (Permiso permiso : permisoRepository.findAll()) {
                        if (!modulosRestringidos.contains(permiso.getModulo())) {
                                seedRolPermiso(rol, permiso);
                        }
                }

                // El ADMIN necesita ver Sucursales y Roles para gestionar su personal, pero NO editarlos
                permisoRepository.findByNombre("sucursales:read").ifPresent(p -> seedRolPermiso(rol, p));
                permisoRepository.findByNombre("roles:read").ifPresent(p -> seedRolPermiso(rol, p));
        }

        private void ensureAdminUser(Rol adminRol) {
                Usuario adminUsuario = usuarioRepository.findByUsername(adminUsername)
                                .orElseGet(() -> usuarioRepository.save(Usuario.builder()
                                                .ci("ADMIN-0001")
                                                .nombre("System")
                                                .apellido("Admin")
                                                .username(adminUsername)
                                                .passwordHash(passwordEncoder.encode(adminPassword))
                                                .sexo("O")
                                                .correo(adminEmail)
                                                .tipoUsuario("S")
                                                .intentosFallidos(0)
                                                .estadoAcceso("HABILITADO")
                                                .activo(true)
                                                .build()));

                if (!rolUsuarioRepository.existsByUsuario_IdUsuarioAndRol_IdRol(adminUsuario.getIdUsuario(),
                                adminRol.getIdRol())) {
                        rolUsuarioRepository.save(RolUsuario.builder()
                                        .usuario(adminUsuario)
                                        .rol(adminRol)
                                        .activo(true)
                                        .build());
                }
        }
    private void seedRolPermiso(Rol rol, Permiso permiso) {
        if (!rolPermisoRepository.existsByRol_IdRolAndPermiso_IdPermiso(rol.getIdRol(),
                permiso.getIdPermiso())) {
            rolPermisoRepository.save(RolPermiso.builder()
                    .rol(rol)
                    .permiso(permiso)
                    .activo(true)
                    .build());
        }
    }
}
