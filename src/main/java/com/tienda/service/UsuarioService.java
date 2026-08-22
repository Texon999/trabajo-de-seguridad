package com.tienda.service;

import com.tienda.dto.LoginRequest;
import com.tienda.dto.RegistroRequest;
import com.tienda.exception.BadRequestException;
import com.tienda.exception.DuplicateResourceException;
import com.tienda.model.Usuario;
import com.tienda.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario registrar(RegistroRequest request) {
        String username = request.getUsername().trim();
        String nombre = request.getNombre().trim();

        if (usuarioRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("El usuario ya esta registrado, intenta con otro nombre");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setNombre(nombre);
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setFechaCreacion(Instant.now());
        usuario.setUltimoAcceso(Instant.now());
        return usuarioRepository.save(usuario);
    }

    public Usuario login(LoginRequest request) {
        String username = request.getUsername().trim();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Intento de login fallido para el usuario {}", username);
                    return new BadRequestException("Usuario o contraseña incorrectos");
                });

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            log.warn("Contraseña incorrecta para el usuario {}", request.getUsername());
            throw new BadRequestException("Usuario o contraseña incorrectos");
        }

        usuario.setUltimoAcceso(Instant.now());
        return usuarioRepository.save(usuario);
    }

}
