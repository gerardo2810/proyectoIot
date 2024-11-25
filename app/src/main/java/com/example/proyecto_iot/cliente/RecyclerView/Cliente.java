package com.example.proyecto_iot.cliente.RecyclerView;

import java.util.List;

public class Cliente {
    private String id;
    private String nombre;
    private String email;
    private String direccion;
    private String fotoURL;
    private List<String> favoritos; // Lista de IDs de restaurantes favoritos

    public Cliente(String id, String nombre, String email, String direccion, String fotoURL, List<String> favoritos) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.direccion = direccion;
        this.fotoURL = fotoURL;
        this.favoritos = favoritos;
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getFotoURL() {
        return fotoURL;
    }

    public void setFotoURL(String fotoURL) {
        this.fotoURL = fotoURL;
    }

    public List<String> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(List<String> favoritos) {
        this.favoritos = favoritos;
    }
}
