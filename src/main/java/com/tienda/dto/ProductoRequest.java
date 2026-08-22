package com.tienda.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProductoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    @Digits(integer = 10, fraction = 2, message = "El precio tiene un formato invalido")
    private Double precio;

    @Size(max = 50, message = "La categoria no puede superar 50 caracteres")
    private String categoria;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    private Boolean activo;

    @Size(max = 50, message = "La marca no puede superar 50 caracteres")
    private String marca;

    public String getNombre() { return nombre; }
    public Double getPrecio() { return precio; }
    public String getCategoria() { return categoria; }
    public Integer getStock() { return stock; }
    public Boolean getActivo() { return activo; }
    public String getMarca() { return marca; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPrecio(Double precio) { this.precio = precio; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setStock(Integer stock) { this.stock = stock; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public void setMarca(String marca) { this.marca = marca; }

}
