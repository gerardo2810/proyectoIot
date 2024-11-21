package com.example.proyecto_iot.admin_restaurante.RecyclerView;

public class Categoria {
    private String id;
    private String Nombre;
    private String iconFoto;

    public Categoria() {
    }

    public Categoria(String id, String Nombre, String imageResId) {
        this.id = id;
        this.Nombre = Nombre;
        this.iconFoto = iconFoto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getIconFoto() {
        return iconFoto;
    }

    public void setIconFoto(String iconFoto) {
        this.iconFoto = iconFoto;
    }
}
