package com.example.proyecto_iot.cliente.RecyclerView;

public class Restaurante {

    private String nombre;
    private double precioDelivery;
    private String tipoDeComida;
    private String ubicacion;
    private  String fotoPortada;
    private String fotoLogo; // URL de la imagen

    public Restaurante(String nombre, double precioDelivery, String tipoDeComida, String ubicacion, String fotoPortada,String fotoLogo) {
        this.nombre = nombre;
        this.precioDelivery = precioDelivery;
        this.tipoDeComida = tipoDeComida;
        this.ubicacion = ubicacion;
        this.fotoPortada=fotoPortada;
        this.fotoLogo = fotoLogo;
    }

    // Getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecioDelivery() {
        return precioDelivery;
    }

    public void setPrecioDelivery(double precioDelivery) {
        this.precioDelivery = precioDelivery;
    }

    public String getTipoDeComida() {
        return tipoDeComida;
    }

    public void setTipoDeComida(String tipoDeComida) {
        this.tipoDeComida = tipoDeComida;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getFotoLogo() {
        return fotoLogo;
    }

    public void setFotoLogo(String fotoLogo) {
        this.fotoLogo = fotoLogo;
    }
    public String getFotoPortada() {
        return fotoLogo;
    }

    public void setFotoPortada(String fotoLogo) {
        this.fotoLogo = fotoLogo;
    }
}
