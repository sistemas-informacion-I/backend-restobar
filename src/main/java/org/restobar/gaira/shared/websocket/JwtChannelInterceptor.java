package org.restobar.gaira.shared.websocket;

import lombok.RequiredArgsConstructor;
import org.restobar.gaira.security.jwt.JwtService;
import org.restobar.gaira.security.userdetails.ApplicationUserDetailsService;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final ApplicationUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authorization = accessor.getFirstNativeHeader("Authorization");

            if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
                String token = authorization.substring(7);
                try {
                    String username = jwtService.extractUsername(token);

                    if (StringUtils.hasText(username)) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        if (jwtService.isTokenValid(token, userDetails)) {
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            accessor.setUser(authentication);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        } else if (accessor != null && StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            if (destination != null && destination.startsWith("/topic/sucursal/")) {
                // destination is like /topic/sucursal/1/comandas
                String[] parts = destination.split("/");
                if (parts.length >= 4) {
                    try {
                        Long requestedSucursalId = Long.parseLong(parts[3]);
                        if (accessor.getUser() != null && accessor.getUser() instanceof UsernamePasswordAuthenticationToken auth) {
                            if (auth.getPrincipal() instanceof org.restobar.gaira.security.userdetails.ApplicationUserPrincipal principal) {
                                Long userSucursalId = principal.getSucursalId();
                                // Si es un empleado (tiene sucursalId), validar. Si es superadmin o similar (sucursalId nulo o algo distinto), permitir u omitir según lógica de negocio.
                                if (userSucursalId != null && !userSucursalId.equals(requestedSucursalId)) {
                                    throw new org.springframework.security.access.AccessDeniedException("No autorizado para suscribirse a esta sucursal");
                                }
                            }
                        } else {
                            throw new org.springframework.security.access.AccessDeniedException("No autorizado");
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return message;
    }
}
