package com.tienda.controller;

import com.tienda.dto.LoginRequest;
import com.tienda.dto.RegistroRequest;
import com.tienda.model.Usuario;
import com.tienda.repository.UsuarioRepository;
import com.tienda.security.JwtService;
import com.tienda.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    private Usuario usuarioDePrueba() {
        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        usuario.setNombre("Admin de la tienda");
        return usuario;
    }

    @Test
    void registroValidoDevuelve201ConToken() throws Exception {
        when(usuarioService.registrar(any(RegistroRequest.class))).thenReturn(usuarioDePrueba());
        when(jwtService.generarToken(any(Usuario.class))).thenReturn("token-falso");
        when(jwtService.getExpiracionMs()).thenReturn(3_600_000L);

        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin123\",\"nombre\":\"Admin de la tienda\",\"password\":\"ContrasenaSegura123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("token-falso"))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.expiraEnMinutos").value(60));
    }

    @Test
    void registroConContrasenaCortaDevuelve400() throws Exception {
        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"nombre\":\"Admin\",\"password\":\"123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void registroSinCamposObligatoriosDevuelve400() throws Exception {
        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginValidoDevuelve200ConToken() throws Exception {
        when(usuarioService.login(any(LoginRequest.class))).thenReturn(usuarioDePrueba());
        when(jwtService.generarToken(any(Usuario.class))).thenReturn("token-falso");
        when(jwtService.getExpiracionMs()).thenReturn(3_600_000L);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"ContrasenaSegura123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-falso"))
                .andExpect(jsonPath("$.nombre").value("Admin de la tienda"));
    }

    @Test
    void loginConJsonVacioDevuelve400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bodyInvalidoDevuelve400SinExponerStacktrace() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("esto-no-es-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El cuerpo de la solicitud no es un JSON valido"))
                .andExpect(jsonPath("$.stackTrace").doesNotExist());
    }

}
