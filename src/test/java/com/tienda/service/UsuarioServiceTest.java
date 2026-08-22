package com.tienda.service;

import com.tienda.dto.LoginRequest;
import com.tienda.dto.RegistroRequest;
import com.tienda.exception.BadRequestException;
import com.tienda.exception.DuplicateResourceException;
import com.tienda.model.Usuario;
import com.tienda.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(usuarioRepository, passwordEncoder);
    }

    private RegistroRequest requestValido() {
        RegistroRequest request = new RegistroRequest();
        request.setUsername("admin");
        request.setNombre("Admin de la tienda");
        request.setPassword("ContraseñaSegura123");
        return request;
    }

    @Test
    void registrarGuardaLaContrasenaHasheadaNuncaEnPlano() {
        when(usuarioRepository.existsByUsername("admin")).thenReturn(false);
        when(passwordEncoder.encode("ContraseñaSegura123")).thenReturn("$2a$10$hashficticio");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario guardado = usuarioService.registrar(requestValido());

        assertThat(guardado.getPasswordHash()).isEqualTo("$2a$10$hashficticio");
        assertThat(guardado.getPasswordHash()).isNotEqualTo("ContraseñaSegura123");
        assertThat(guardado.getFechaCreacion()).isNotNull();
    }

    @Test
    void registrarUsuarioExistenteLanzaConflicto() {
        when(usuarioRepository.existsByUsername("admin")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.registrar(requestValido()))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("ya esta registrado");
    }

    @Test
    void loginConCredencialesCorrectasActualizaElUltimoAcceso() {
        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        usuario.setPasswordHash("$2a$10$hashficticio");
        usuario.setUltimoAcceso(Instant.now().minusSeconds(3600));

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("ContraseñaSegura123", "$2a$10$hashficticio")).thenReturn(true);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        LoginRequest login = new LoginRequest();
        login.setUsername("admin");
        login.setPassword("ContraseñaSegura123");

        Instant ultimoAccesoPrevio = usuario.getUltimoAcceso();
        Usuario autenticado = usuarioService.login(login);

        assertThat(autenticado.getUltimoAcceso()).isAfter(ultimoAccesoPrevio);
    }

    @Test
    void loginConUsuarioInexistenteLanzaErrorGenerico() {
        when(usuarioRepository.findByUsername("nadie")).thenReturn(Optional.empty());

        LoginRequest login = new LoginRequest();
        login.setUsername("nadie");
        login.setPassword("lo-que-sea");

        assertThatThrownBy(() -> usuarioService.login(login))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Usuario o contraseña incorrectos");
    }

    @Test
    void loginConContrasenaIncorrectaLanzaErrorGenerico() {
        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        usuario.setPasswordHash("$2a$10$hashficticio");

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        LoginRequest login = new LoginRequest();
        login.setUsername("admin");
        login.setPassword("incorrecta");

        assertThatThrownBy(() -> usuarioService.login(login))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Usuario o contraseña incorrectos");
    }

    @Test
    void registrarRecortaLosCamposDeTexto() {
        when(usuarioRepository.existsByUsername("admin")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        RegistroRequest request = requestValido();
        request.setUsername("  admin  ");
        request.setNombre("  Admin  ");

        Usuario guardado = usuarioService.registrar(request);

        assertThat(guardado.getUsername()).isEqualTo("admin");
        assertThat(guardado.getNombre()).isEqualTo("Admin");
        verify(usuarioRepository).existsByUsername("admin");
    }

}
