package org.restobar.gaira.security.userdetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.restobar.gaira.acceso.entity.Permiso;
import org.restobar.gaira.acceso.entity.RolPermiso;
import org.restobar.gaira.acceso.entity.RolUsuario;
import org.restobar.gaira.acceso.entity.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import lombok.Getter;

@Getter
public class ApplicationUserPrincipal implements UserDetails {

    private final Long idUsuario;
    private final String username;
    private final String email;
    private final String password;
    private final boolean enabled;
    private final boolean accountNonLocked;
    private final Collection<? extends GrantedAuthority> authorities;

    public ApplicationUserPrincipal(Long idUsuario,
            String username,
            String email,
            String password,
            boolean enabled,
            boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.accountNonLocked = accountNonLocked;
        this.authorities = authorities;
    }

    /**
     * Construye el principal directamente desde el Usuario (que ahora contiene
     * username, passwordHash, intentosFallidos y estadoAcceso).
     */
    public static ApplicationUserPrincipal from(Usuario usuario) {
        LocalDateTime now = LocalDateTime.now();

        Set<GrantedAuthority> grantedAuthorities = Optional.ofNullable(usuario.getRolesUsuario())
                .orElseGet(Collections::emptySet)
                .stream()
                .filter(Objects::nonNull)
                .filter(ru -> Boolean.TRUE.equals(ru.getActivo()))
                .filter(ru -> ru.getFechaExpiracion() == null || !ru.getFechaExpiracion().isBefore(now))
                .map(RolUsuario::getRol)
                .filter(Objects::nonNull)
                .filter(rol -> Boolean.TRUE.equals(rol.getActivo()))
                .flatMap(rol -> Optional.ofNullable(rol.getRolesPermiso())
                        .orElseGet(Collections::emptySet)
                        .stream())
                .filter(Objects::nonNull)
                .filter(rp -> Boolean.TRUE.equals(rp.getActivo()))
                .filter(rp -> rp.getFechaExpiracion() == null || !rp.getFechaExpiracion().isBefore(now))
                .map(RolPermiso::getPermiso)
                .filter(Objects::nonNull)
                .filter(permiso -> Boolean.TRUE.equals(permiso.getActivo()))
                .map(Permiso::getNombre)
                .filter(StringUtils::hasText)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        boolean enabled = Boolean.TRUE.equals(usuario.getActivo());
        boolean nonLocked = !"BLOQUEADO".equals(usuario.getEstadoAcceso());

        return new ApplicationUserPrincipal(
                usuario.getIdUsuario(),
                usuario.getUsername(),
                usuario.getCorreo(),
                usuario.getPasswordHash(),
                enabled,
                nonLocked,
                grantedAuthorities);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
