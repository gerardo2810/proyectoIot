package com.example.proyecto_iot.superadmin.RecyclerView;

public class RestauranteSA {

    private String nombre;
    private String descripcion;
    private String dniAdministrador;

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() { return descripcion; }

    public String getDniAdministrador() { return dniAdministrador; }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setDniAdministrador(String dniAdministrador) {
        this.dniAdministrador = dniAdministrador;
    }
}
