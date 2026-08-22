package com.tienda.service;

import com.tienda.dto.ProductoRequest;
import com.tienda.dto.ProductoResponse;
import com.tienda.exception.BadRequestException;
import com.tienda.exception.ResourceNotFoundException;
import com.tienda.model.Producto;
import com.tienda.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<ProductoResponse> listarTodos() {
        return productoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ProductoResponse buscarPorId(String id) {
        return toResponse(obtenerProducto(id));
    }

    public List<ProductoResponse> listarActivos() {
        return productoRepository.findByActivo(true).stream().map(this::toResponse).toList();
    }

    public List<ProductoResponse> listarPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria).stream().map(this::toResponse).toList();
    }

    public List<ProductoResponse> listarPorRangoPrecio(Double min, Double max) {
        if (min == null || max == null) {
            throw new BadRequestException("Debes enviar los parametros min y max");
        }
        if (min < 0 || max < 0) {
            throw new BadRequestException("Los precios no pueden ser negativos");
        }
        if (min > max) {
            throw new BadRequestException("El precio minimo no puede ser mayor al maximo");
        }
        return productoRepository.findByPrecioBetween(min, max).stream().map(this::toResponse).toList();
    }

    public ProductoResponse crear(ProductoRequest request, String usuario, String ip) {
        Producto producto = new Producto();
        producto.setNombre(request.getNombre().trim());
        producto.setPrecio(request.getPrecio());
        producto.setCategoria(request.getCategoria() != null ? request.getCategoria().trim() : null);
        producto.setStock(request.getStock());
        producto.setActivo(request.getActivo() != null ? request.getActivo() : true);
        producto.setMarca(request.getMarca() != null ? request.getMarca().trim() : null);
        producto.setCreadoEn(Instant.now());
        producto.setCreadoPor(usuario);
        producto.setIpOrigen(ip);

        Producto guardado = productoRepository.save(producto);
        log.info("Producto creado '{}' por {} desde {}", guardado.getNombre(), usuario, ip);
        return toResponse(guardado);
    }

    public ProductoResponse actualizar(String id, ProductoRequest request, String usuario, String ip) {
        Producto producto = obtenerProducto(id);
        producto.setNombre(request.getNombre().trim());
        producto.setPrecio(request.getPrecio());
        producto.setCategoria(request.getCategoria() != null ? request.getCategoria().trim() : null);
        producto.setStock(request.getStock());
        producto.setActivo(request.getActivo() != null ? request.getActivo() : true);
        producto.setMarca(request.getMarca() != null ? request.getMarca().trim() : null);

        Producto guardado = productoRepository.save(producto);
        log.info("Producto {} actualizado por {} desde {}", guardado.getId(), usuario, ip);
        return toResponse(guardado);
    }

    public void eliminar(String id, String usuario, String ip) {
        Producto producto = obtenerProducto(id);
        productoRepository.deleteById(producto.getId());
        log.info("Producto {} eliminado por {} desde {}", producto.getId(), usuario, ip);
    }

    private Producto obtenerProducto(String id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }

    private ProductoResponse toResponse(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getCategoria(),
                producto.getStock(),
                producto.getActivo(),
                producto.getMarca(),
                producto.getCreadoEn(),
                producto.getCreadoPor());
    }

}
