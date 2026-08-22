package com.tienda.security;

import com.tienda.model.Usuario;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService(
            "clave-de-prueba-tienda-2024-sena-1234567890", 60);

    private Usuario usuarioDePrueba() {
        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        usuario.setNombre("Admin");
        usuario.setFechaCreacion(Instant.now());
        return usuario;
    }

    @Test
    void generaTokenQueSePuedeValidarYExtraeElUsuario() {
        String token = jwtService.generarToken(usuarioDePrueba());

        assertThat(token).isNotBlank();
        assertThat(jwtService.getUsername(token)).contains("admin");
    }

    @Test
    void tokenInvalidoDevuelveVacio() {
        assertThat(jwtService.getUsername("no-es-un-token")).isEmpty();
    }

    @Test
    void tokenModificadoDevuelveVacio() {
        String token = jwtService.generarToken(usuarioDePrueba());
        String modificado = token.substring(0, token.length() - 4) + "XXXX";

        assertThat(jwtService.getUsername(modificado)).isEmpty();
    }

    @Test
    void tokenExpiradoDevuelveVacio() {
        JwtService expirado = new JwtService("clave-de-prueba-tienda-2024-sena-1234567890", -1);
        String token = expirado.generarToken(usuarioDePrueba());

        assertThat(expirado.getUsername(token)).isEmpty();
    }

    @Test
    void expiraEnDevuelveLosMinutosConfigurados() {
        assertThat(jwtService.getExpiracionMs()).isEqualTo(60 * 60_000L);
    }

}
