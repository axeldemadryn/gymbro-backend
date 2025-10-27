package com.gym.backend.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String username = jwtUtil.extraerUsername(jwt);
        String tipoToken = jwtUtil.extraerTipo(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (!userDetails.isEnabled()) { // Si el usuario no está activo
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Tu cuenta aún no fue verificada. Revisa tu correo.\"}");
                return;
            }

            // Bloquear tokens que no sean de sesión
            if (!"sesion".equals(tipoToken)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Token no válido para endpoints protegidos.\"}");
                return;
            }

            if (jwtUtil.validarToken(jwt)) {
                CustomUserDetails customUser = (CustomUserDetails) userDetails;
                LocalDateTime lastLogout = customUser.getLastLogout();

                Date tokenIssueDate = jwtUtil.extraerClaims(jwt).getIssuedAt();

                // Si el token fue emitido antes del último logout → invalido
                if (lastLogout != null
                        && tokenIssueDate.toInstant().isBefore(lastLogout.atZone(ZoneId.systemDefault()).toInstant())) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\":\"Token inválido: usuario ya hizo logout\"}");
                    return;
                }

                LocalDateTime fechaYHoraRegistro = customUser.getfechaYHoraRegistro();

                // Token emitido antes del registro → inválido
                if (fechaYHoraRegistro != null && tokenIssueDate.toInstant()
                        .isBefore(fechaYHoraRegistro.atZone(ZoneId.systemDefault()).toInstant())) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter()
                            .write("{\"error\":\"Token inválido: generado antes del registro del usuario\"}");
                    return;
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
