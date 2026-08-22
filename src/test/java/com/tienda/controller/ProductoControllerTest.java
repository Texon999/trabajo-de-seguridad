package com.tienda.controller;

import com.tienda.dto.ProductoRequest;
import com.tienda.dto.ProductoResponse;
import com.tienda.exception.ResourceNotFoundException;
import com.tienda.repository.UsuarioRepository;
import com.tienda.security.JwtService;
import com.tienda.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    private ProductoResponse productoDePrueba() {
        return new ProductoResponse("1", "Teclado mecanico", 250000.0, "perifericos",
                10, true, "Logitech", Instant.now(), "admin");
    }

    @Test
    void listarProductosDevuelve200ConLaLista() throws Exception {
        when(productoService.listarTodos()).thenReturn(List.of(productoDePrueba()));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Teclado mecanico"))
                .andExpect(jsonPath("$[0].precio").value(250000.0));
    }

    @Test
    void buscarProductoInexistenteDevuelve404() throws Exception {
        when(productoService.buscarPorId("999")).thenThrow(new ResourceNotFoundException("Producto no encontrado"));

        mockMvc.perform(get("/api/productos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Producto no encontrado"));
    }

    @Test
    void crearProductoValidoDevuelve201() throws Exception {
        when(productoService.crear(any(ProductoRequest.class), eq("anonimo"), any(String.class)))
                .thenReturn(productoDePrueba());

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Teclado mecanico\",\"precio\":250000,\"categoria\":\"perifericos\",\"marca\":\"Logitech\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Teclado mecanico"));
    }

    @Test
    void crearProductoSinNombreDevuelve400() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"precio\":250000}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void crearProductoSinPrecioDevuelve400() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Teclado mecanico\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearProductoConPrecioNegativoDevuelve400() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Teclado\",\"precio\":-100}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearProductoConNombreMuyCortoDevuelve400() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"A\",\"precio\":100}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void eliminarProductoDevuelve204() throws Exception {
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());

        verify(productoService).eliminar(eq("1"), eq("anonimo"), any(String.class));
    }

    @Test
    void eliminarProductoInexistenteDevuelve404() throws Exception {
        doThrow(new ResourceNotFoundException("Producto no encontrado"))
                .when(productoService).eliminar(eq("999"), eq("anonimo"), any(String.class));

        mockMvc.perform(delete("/api/productos/999"))
                .andExpect(status().isNotFound());
    }

}
