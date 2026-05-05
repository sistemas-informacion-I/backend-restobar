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

        seedModulo("INVENTARIO", "inventario", List.of("create", "read", "update", "delete"));

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
            "INVENTARIO", "EMPLEADOS", "CLIENTES", "PROVEEDORES", 
            "SECTORES", "MESAS"
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
