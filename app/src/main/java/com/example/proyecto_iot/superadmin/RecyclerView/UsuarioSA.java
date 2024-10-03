package com.example.proyecto_iot.superadmin.RecyclerView;

public class UsuarioSA {

    private String nombre;
    private String apellido;
    private String rol;
    private String estado;

    public UsuarioSA(String nombre, String apellido, String rol, String estado) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
        this.estado = estado;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getRol() {
        return rol;
    }

    public String getEstado() {
        return estado;
    }
}
