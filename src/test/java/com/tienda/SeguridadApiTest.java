package com.tienda;

import com.jayway.jsonpath.JsonPath;
import com.tienda.model.Producto;
import com.tienda.model.Usuario;
import com.tienda.repository.ProductoRepository;
import com.tienda.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SeguridadApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private ProductoRepository productoRepository;

    @MockBean
    private MongoTemplate mongoTemplate;

    private String usernamePrueba;

    @BeforeEach
    void configurarMocks() {
        when(usuarioRepository.existsByUsername(anyString())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioRepository.findByUsername(anyString())).thenAnswer(inv -> {
            Usuario usuario = new Usuario();
            usuario.setUsername(inv.getArgument(0));
            usuario.setNombre("Usuario Test");
            usuario.setFechaCreacion(Instant.now());
            usuario.setUltimoAcceso(Instant.now());
            return Optional.of(usuario);
        });
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> {
            Producto producto = inv.getArgument(0);
            if (producto.getId() == null) {
                producto.setId("id-producto-mock");
            }
            return producto;
        });
        when(productoRepository.findById(anyString())).thenAnswer(inv -> {
            Producto producto = new Producto();
            producto.setId(inv.getArgument(0));
            producto.setNombre("Teclado mecanico");
            producto.setPrecio(150000.0);
            return Optional.of(producto);
        });
    }

    @Test
    void consultarProductosEsPublico() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk());
    }

    @Test
    void crearProductoSinTokenDevuelve401() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Teclado\",\"precio\":100}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void eliminarProductoSinTokenDevuelve401() throws Exception {
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenInvalidoDevuelve401AlEscribir() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .header("Authorization", "Bearer token-invalido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Teclado\",\"precio\":100}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginConCredencialesIncorrectasDevuelve400() throws Exception {
        when(usuarioRepository.findByUsername("nadie")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"nadie\",\"password\":\"incorrecta\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registroLoginYEscrituraConTokenFuncionan() throws Exception {
        usernamePrueba = "test" + System.nanoTime();
        String registro = "{\"username\":\"" + usernamePrueba
                + "\",\"nombre\":\"Usuario Test\",\"password\":\"ContrasenaSegura123\"}";

        String respuestaRegistro = mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registro))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String token = JsonPath.parse(respuestaRegistro).read("$.token");

        String respuestaCreado = mockMvc.perform(post("/api/productos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Teclado mecanico\",\"precio\":150000}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String idProducto = JsonPath.parse(respuestaCreado).read("$.id");

        mockMvc.perform(delete("/api/productos/" + idProducto)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk());
    }

}
