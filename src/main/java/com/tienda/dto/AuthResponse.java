package com.tienda.dto;

public class AuthResponse {

    private String token;
    private long expiraEnMinutos;
    private String username;
    private String nombre;

    public AuthResponse() {}

    public AuthResponse(String token, long expiraEnMinutos, String username, String nombre) {
        this.token = token;
        this.expiraEnMinutos = expiraEnMinutos;
        this.username = username;
        this.nombre = nombre;
    }

    public String getToken() { return token; }
    public long getExpiraEnMinutos() { return expiraEnMinutos; }
    public String getUsername() { return username; }
    public String getNombre() { return nombre; }

}
