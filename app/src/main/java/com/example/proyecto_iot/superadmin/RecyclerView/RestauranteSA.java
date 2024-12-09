package com.example.proyecto_iot.superadmin.RecyclerView;

public class RestauranteSA {

    private String nombre;
    private String eslogan;
    private String ubicacion;
    private String descripcion;
    private String tipoDeComida;
    private String fotoLogo;
    private String fotoPortada;
    private String idAdministrador;
    private boolean isOpen;
    private int ventas;
    private double precioDelivery;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEslogan() {
        return eslogan;
    }

    public void setEslogan(String eslogan) {
        this.eslogan = eslogan;
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

    public String getFotoLogo() {
        return fotoLogo;
    }

    public void setFotoLogo(String fotoLogo) {
        this.fotoLogo = fotoLogo;
    }

    public String getFotoPortada() {
        return fotoPortada;
    }

    public void setFotoPortada(String fotoPortada) {
        this.fotoPortada = fotoPortada;
    }

    public String getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(String idAdministrador) {
        this.idAdministrador = idAdministrador;
    }



    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public int getVentas() {
        return ventas;
    }

    public void setVentas(int ventas) {
        this.ventas = ventas;
    }

    public double getPrecioDelivery() {
        return precioDelivery;
    }

    public void setPrecioDelivery(double precioDelivery) {
        this.precioDelivery = precioDelivery;
    }
}
