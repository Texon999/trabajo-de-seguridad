package com.tienda.controller;

import com.tienda.dto.AuthResponse;
import com.tienda.dto.LoginRequest;
import com.tienda.dto.RegistroRequest;
import com.tienda.model.Usuario;
import com.tienda.security.JwtService;
import com.tienda.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public AuthController(UsuarioService usuarioService, JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
    }

    @PostMapping("/registro")
    public ResponseEntity<AuthResponse> registro(@Valid @RequestBody RegistroRequest request) {
        Usuario usuario = usuarioService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(armarRespuesta(usuario));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = usuarioService.login(request);
        return ResponseEntity.ok(armarRespuesta(usuario));
    }

    private AuthResponse armarRespuesta(Usuario usuario) {
        String token = jwtService.generarToken(usuario);
        return new AuthResponse(
                token,
                jwtService.getExpiracionMs() / 60_000,
                usuario.getUsername(),
                usuario.getNombre());
    }

}
