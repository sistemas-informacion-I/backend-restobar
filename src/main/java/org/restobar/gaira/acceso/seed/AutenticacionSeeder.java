package org.restobar.gaira.acceso.seed;

import java.util.List;

import org.restobar.gaira.acceso.entity.Permiso;
import org.restobar.gaira.acceso.entity.Rol;
import org.restobar.gaira.acceso.entity.RolPermiso;
import org.restobar.gaira.acceso.entity.RolUsuario;
import org.restobar.gaira.acceso.entity.Usuario;
import org.restobar.gaira.acceso.repository.PermisoRepository;
import org.restobar.gaira.acceso.repository.RolPermisoRepository;
import org.restobar.gaira.acceso.repository.RolRepository;
import org.restobar.gaira.acceso.repository.RolUsuarioRepository;
import org.restobar.gaira.acceso.repository.UsuarioRepository;
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
                                seedPermiso("clients:read", "CLIENTES", "LEER", "Leer clientes"),
                                seedPermiso("employees:read", "EMPLEADOS", "LEER", "Leer empleados"),
                                seedPermiso("providers:read", "PROVEEDORES", "LEER", "Leer proveedores"));

                Rol adminRol = rolRepository.findByNombre("ADMIN")
                                .orElseGet(() -> rolRepository.save(Rol.builder()
                                                .nombre("ADMIN")
                                                .descripcion("Rol administrador")
                                                .nivelAcceso(100)
                                                .activo(true)
                                                .build()));

                Rol userRol = rolRepository.findByNombre("USER")
                                .orElseGet(() -> rolRepository.save(Rol.builder()
                                                .nombre("USER")
                                                .descripcion("Rol usuario por defecto")
                                                .nivelAcceso(1)
                                                .activo(true)
                                                .build()));

                for (Permiso permiso : permisosBase) {
                        if (!rolPermisoRepository.existsByRol_IdRolAndPermiso_IdPermiso(adminRol.getIdRol(),
                                        permiso.getIdPermiso())) {
                                rolPermisoRepository.save(RolPermiso.builder()
                                                .rol(adminRol)
                                                .permiso(permiso)
                                                .activo(true)
                                                .build());
                        }
                }

                Permiso usuarioLeer = permisoRepository.findByNombre("users:read").orElseThrow();
                if (!rolPermisoRepository.existsByRol_IdRolAndPermiso_IdPermiso(userRol.getIdRol(),
                                usuarioLeer.getIdPermiso())) {
                        rolPermisoRepository.save(RolPermiso.builder()
                                        .rol(userRol)
                                        .permiso(usuarioLeer)
                                        .activo(true)
                                        .build());
                }

                // Crear usuario admin si no existe (ahora usuario tiene username+password directamente)
                boolean adminExiste = usuarioRepository.existsByUsername(adminUsername);
                if (!adminExiste) {
                        Usuario adminUsuario = usuarioRepository.save(Usuario.builder()
                                        .ci("ADMIN-0001")
                                        .nombre("System")
                                        .apellido("Admin")
                                        .username(adminUsername)
                                        .passwordHash(passwordEncoder.encode(adminPassword))
                                        .sexo("O")
                                        .correo(adminEmail)
                                        .intentosFallidos(0)
                                        .estadoAcceso("HABILITADO")
                                        .activo(true)
                                        .build());

                        if (!rolUsuarioRepository.existsByUsuario_IdUsuarioAndRol_IdRol(adminUsuario.getIdUsuario(),
                                        adminRol.getIdRol())) {
                                rolUsuarioRepository.save(RolUsuario.builder()
                                                .usuario(adminUsuario)
                                                .rol(adminRol)
                                                .activo(true)
                                                .build());
                        }
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
}
