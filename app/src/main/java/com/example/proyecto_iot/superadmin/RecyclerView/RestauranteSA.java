package com.example.proyecto_iot.superadmin.RecyclerView;

public class RestauranteSA {

    private String nombre;
    private String ubicacion;
    private String descripcion;
    private String tipoDeComida;
    private String dniAdministrador;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoDeComida() {
        return tipoDeComida;
    }

    public void setTipoDeComida(String tipoDeComida) {
        this.tipoDeComida = tipoDeComida;
    }

    public String getDniAdministrador() {
        return dniAdministrador;
    }

    public void setDniAdministrador(String dniAdministrador) {
        this.dniAdministrador = dniAdministrador;
    }
}
