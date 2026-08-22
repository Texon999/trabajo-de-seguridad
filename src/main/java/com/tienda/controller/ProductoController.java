package com.tienda.controller;

import com.tienda.dto.ProductoRequest;
import com.tienda.dto.ProductoResponse;
import com.tienda.service.ProductoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<ProductoResponse> getAll() {
        return productoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    @GetMapping("/activos")
    public List<ProductoResponse> getActivos() {
        return productoService.listarActivos();
    }

    @GetMapping("/categoria/{cat}")
    public List<ProductoResponse> getByCategoria(@PathVariable String cat) {
        return productoService.listarPorCategoria(cat);
    }

    @GetMapping("/precio")
    public List<ProductoResponse> getByRangoPrecio(@RequestParam Double min,
                                                   @RequestParam Double max) {
        return productoService.listarPorRangoPrecio(min, max);
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> create(@Valid @RequestBody ProductoRequest request,
                                                   @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
                                                   HttpServletRequest httpRequest) {
        ProductoResponse creado = productoService.crear(
                request, usuarioActual(), obtenerIp(httpRequest, forwardedFor));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> update(@PathVariable String id,
                                                   @Valid @RequestBody ProductoRequest request,
                                                   @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
                                                   HttpServletRequest httpRequest) {
        ProductoResponse actualizado = productoService.actualizar(
                id, request, usuarioActual(), obtenerIp(httpRequest, forwardedFor));
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id,
                                       @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
                                       HttpServletRequest httpRequest) {
        productoService.eliminar(id, usuarioActual(), obtenerIp(httpRequest, forwardedFor));
        return ResponseEntity.noContent().build();
    }

    private String usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "anonimo";
    }

    private String obtenerIp(HttpServletRequest request, String forwardedFor) {
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

}
