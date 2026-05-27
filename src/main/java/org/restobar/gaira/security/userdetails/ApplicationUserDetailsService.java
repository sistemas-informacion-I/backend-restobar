package org.restobar.gaira.security.userdetails;

import org.restobar.gaira.modulo_acceso.entity.Usuario;
import org.restobar.gaira.modulo_acceso.repository.UsuarioRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final org.restobar.gaira.modulo_operaciones.repository.EmpleadoSucursalRepository empleadoSucursalRepository;

    public ApplicationUserDetailsService(UsuarioRepository usuarioRepository, 
            org.restobar.gaira.modulo_operaciones.repository.EmpleadoSucursalRepository empleadoSucursalRepository) {
        this.usuarioRepository = usuarioRepository;
        this.empleadoSucursalRepository = empleadoSucursalRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findActiveByUsernameWithAuthorities(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new DisabledException("Usuario inactivo");
        }

        if ("BLOQUEADO".equals(usuario.getEstadoAcceso())) {
            throw new LockedException("Cuenta bloqueada");
        }

        if ("SUSPENDIDO".equals(usuario.getEstadoAcceso())) {
            throw new DisabledException("Cuenta suspendida");
        }

        Long sucursalId = null;
        if ("E".equals(usuario.getTipoUsuario())) {
            sucursalId = empleadoSucursalRepository.findByEmpleado_Usuario_IdUsuarioAndActivoTrue(usuario.getIdUsuario())
                    .map(org.restobar.gaira.modulo_operaciones.entity.EmpleadoSucursal::getSucursal)
                    .map(org.restobar.gaira.modulo_operaciones.entity.Sucursal::getIdSucursal)
                    .orElse(null);
        }

        return ApplicationUserPrincipal.from(usuario, sucursalId);
    }
}
