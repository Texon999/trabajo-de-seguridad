package com.tienda.dto;

import java.time.Instant;

public class ProductoResponse {

    private String id;
    private String nombre;
    private Double precio;
    private String categoria;
    private Integer stock;
    private Boolean activo;
    private String marca;
    private Instant creadoEn;
    private String creadoPor;

    public ProductoResponse() {}

    public ProductoResponse(String id, String nombre, Double precio, String categoria,
                            Integer stock, Boolean activo, String marca,
                            Instant creadoEn, String creadoPor) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.stock = stock;
        this.activo = activo;
        this.marca = marca;
        this.creadoEn = creadoEn;
        this.creadoPor = creadoPor;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public Double getPrecio() { return precio; }
    public String getCategoria() { return categoria; }
    public Integer getStock() { return stock; }
    public Boolean getActivo() { return activo; }
    public String getMarca() { return marca; }
    public Instant getCreadoEn() { return creadoEn; }
    public String getCreadoPor() { return creadoPor; }

}
