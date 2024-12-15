package com.example.proyecto_iot.cliente.RecyclerView;

public class Categoria {
    private String nombre;
    private int iconFoto;
    private String idRestaurante;

    // Constructor
    public Categoria(String nombre, int iconFoto, String idRestaurante) {
        this.nombre = nombre;
        this.iconFoto = iconFoto;
        this.idRestaurante = idRestaurante;
    }
    public Categoria(String nombre, int iconFoto) {
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

    public int getIconFoto() {
        return iconFoto;
    }

    public void setIconFoto(int iconFoto) {
        this.iconFoto = iconFoto;
    }

    public String getIdRestaurante() {
        return idRestaurante;
    }

    public void setIdRestaurante(String idRestaurante) {
        this.idRestaurante = idRestaurante;
    }
}
