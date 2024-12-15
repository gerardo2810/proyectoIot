package com.example.proyecto_iot.superadmin.RecyclerView;

public class UsuarioSA {

    private String id;
    private String nombre;
    private String rol;
    private String estado;
    private String foto;

    public UsuarioSA(String id, String nombre, String rol, String estado, String foto) {
        this.id = id;
        this.nombre = nombre;
        this.rol = rol;
        this.estado = estado;
        this.foto = foto;
    }

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

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
