package com.example.proyecto_iot.cliente.RecyclerView;

public class Restaurante {
    private String id; // Campo para el ID del documento
    private String nombre;
    private double precioDelivery;
    private String tipoDeComida;
    private String ubicacion;
    private  String fotoPortada;
    private String fotoLogo;
    private  int ventas;
    private boolean open;

    public Restaurante(String nombre, double precioDelivery, String tipoDeComida, String ubicacion, String fotoPortada,String fotoLogo, int ventas, boolean open) {
        this.nombre = nombre;
        this.precioDelivery = precioDelivery;
        this.tipoDeComida = tipoDeComida;
        this.ubicacion = ubicacion;
        this.fotoPortada=fotoPortada;
        this.fotoLogo = fotoLogo;
        this.ventas=ventas;
        this.open=open;
    }
    // Constructor sin el campo "cantidad" (opcional)
    public Restaurante(String nombre, double precioDelivery, String tipoDeComida, String ubicacion, String fotoPortada, String fotoLogo) {
        this(nombre, precioDelivery, tipoDeComida, ubicacion, fotoPortada, fotoLogo, 0, true); // Asignar un valor predeterminado a "cantidad"
    }

    public Restaurante(String id,String nombre, double precioDelivery, String tipoDeComida, String ubicacion, String fotoPortada,String fotoLogo, int ventas, boolean open) {
        this.id=id;
        this.nombre = nombre;
        this.precioDelivery = precioDelivery;
        this.tipoDeComida = tipoDeComida;
        this.ubicacion = ubicacion;
        this.fotoPortada=fotoPortada;
        this.fotoLogo = fotoLogo;
        this.ventas=ventas;
        this.open=open;
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
        return fotoPortada;
    }

    public void setFotoPortada(String fotoLogo) {
        this.fotoPortada = fotoLogo;
    }
    public int getVentas() {
        return ventas;
    }

    public void getVentas(int ventas) {
        this.ventas = ventas;
    }
}
