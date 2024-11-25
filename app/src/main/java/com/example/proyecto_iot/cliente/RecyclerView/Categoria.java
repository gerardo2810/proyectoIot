package com.example.proyecto_iot.cliente.RecyclerView;

public class Categoria {
    private String nombre;
    private String iconFoto;
    private String idRestaurante;

    // Constructor
    public Categoria(String nombre, String iconFoto, String idRestaurante) {
        this.nombre = nombre;
        this.iconFoto = iconFoto;
        this.idRestaurante = idRestaurante;
    }

    // Getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIconFoto() {
        return iconFoto;
    }

    public void setIconFoto(String iconFoto) {
        this.iconFoto = iconFoto;
    }

    public String getIdRestaurante() {
        return idRestaurante;
    }

    public void setIdRestaurante(String idRestaurante) {
        this.idRestaurante = idRestaurante;
    }
}
