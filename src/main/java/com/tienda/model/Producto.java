package com.tienda.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "productos")
public class Producto {

    @Id
    private String id;

    private String nombre;
    private Double precio;
    private String categoria;
    private Integer stock;
    private Boolean activo;
    private String marca;

    private Instant creadoEn;
    private String creadoPor;
    private String ipOrigen;

    public Producto() {}

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public Double getPrecio() { return precio; }
    public String getCategoria() { return categoria; }
    public Integer getStock() { return stock; }
    public Boolean getActivo() { return activo; }
    public String getMarca() { return marca; }
    public Instant getCreadoEn() { return creadoEn; }
    public String getCreadoPor() { return creadoPor; }
    public String getIpOrigen() { return ipOrigen; }

    public void setId(String id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPrecio(Double precio) { this.precio = precio; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setStock(Integer stock) { this.stock = stock; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public void setMarca(String marca) { this.marca = marca; }
    public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }
    public void setCreadoPor(String creadoPor) { this.creadoPor = creadoPor; }
    public void setIpOrigen(String ipOrigen) { this.ipOrigen = ipOrigen; }

}
