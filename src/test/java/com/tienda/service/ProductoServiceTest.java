package com.tienda.service;

import com.tienda.dto.ProductoRequest;
import com.tienda.dto.ProductoResponse;
import com.tienda.exception.BadRequestException;
import com.tienda.exception.ResourceNotFoundException;
import com.tienda.model.Producto;
import com.tienda.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    private ProductoService productoService;

    @BeforeEach
    void setUp() {
        productoService = new ProductoService(productoRepository);
    }

    private ProductoRequest requestValido() {
        ProductoRequest request = new ProductoRequest();
        request.setNombre("Teclado mecanico");
        request.setPrecio(250000.0);
        request.setCategoria("perifericos");
        request.setMarca("Logitech");
        request.setStock(10);
        request.setActivo(true);
        return request;
    }

    @Test
    void crearMapeaElDtoYRegistraAuditoria() {
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductoResponse creado = productoService.crear(requestValido(), "admin", "192.168.1.10");

        assertThat(creado.getNombre()).isEqualTo("Teclado mecanico");
        assertThat(creado.getPrecio()).isEqualTo(250000.0);
        assertThat(creado.getCreadoPor()).isEqualTo("admin");
        assertThat(creado.getCreadoEn()).isNotNull();
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void crearActivaElProductoPorDefecto() {
        ProductoRequest request = requestValido();
        request.setActivo(null);
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductoResponse creado = productoService.crear(request, "admin", "127.0.0.1");

        assertThat(creado.getActivo()).isTrue();
    }

    @Test
    void buscarPorIdInexistenteLanzaNotFound() {
        when(productoRepository.findById("abc")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.buscarPorId("abc"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Producto no encontrado");
    }

    @Test
    void rangoDePrecioConMinMayorQueMaxLanzaError() {
        assertThatThrownBy(() -> productoService.listarPorRangoPrecio(5000.0, 1000.0))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("minimo no puede ser mayor");
    }

    @Test
    void rangoDePrecioConParametrosFaltantesLanzaError() {
        assertThatThrownBy(() -> productoService.listarPorRangoPrecio(null, 1000.0))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("min y max");
    }

    @Test
    void rangoDePrecioValidoFiltraEntreExtremos() {
        Producto barato = new Producto();
        barato.setNombre("Mouse");
        barato.setPrecio(50000.0);
        when(productoRepository.findByPrecioBetween(10000.0, 100000.0))
                .thenReturn(List.of(barato));

        List<ProductoResponse> resultados = productoService.listarPorRangoPrecio(10000.0, 100000.0);

        assertThat(resultados).hasSize(1);
        assertThat(resultados.get(0).getNombre()).isEqualTo("Mouse");
    }

    @Test
    void actualizarProductoInexistenteLanzaNotFoundYNoGuarda() {
        when(productoRepository.findById("xyz")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.actualizar("xyz", requestValido(), "admin", "127.0.0.1"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void eliminarBorraElProductoEncontrado() {
        Producto producto = new Producto();
        producto.setId("abc");
        producto.setNombre("Teclado");
        when(productoRepository.findById("abc")).thenReturn(Optional.of(producto));

        productoService.eliminar("abc", "admin", "127.0.0.1");

        verify(productoRepository).deleteById("abc");
    }

}
