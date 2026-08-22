package com.tienda.security;

import com.tienda.model.Usuario;
import com.tienda.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final long timeoutInactividadMin;

    public JwtAuthFilter(JwtService jwtService,
                         UsuarioRepository usuarioRepository,
                         @Value("${jwt.timeout-inactividad-minutos}") long timeoutInactividadMin) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.timeoutInactividadMin = timeoutInactividadMin;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            Optional<String> username = jwtService.getUsername(token);

            if (username.isPresent()) {
                Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username.get());

                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();

                    if (sesionActiva(usuario)) {
                        usuario.setUltimoAcceso(Instant.now());
                        usuarioRepository.save(usuario);

                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                usuario.getUsername(),
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } else {
                        log.info("Sesion expirada por inactividad para el usuario {}", usuario.getUsername());
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean sesionActiva(Usuario usuario) {
        Instant ultimo = usuario.getUltimoAcceso();
        if (ultimo == null) {
            return true;
        }
        long minutos = ChronoUnit.MINUTES.between(ultimo, Instant.now());
        return minutos < timeoutInactividadMin;
    }

}
