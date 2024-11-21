package com.example.proyecto_iot.repartidor.RecyclerView;

public class PedidoRecoger {
    //private int imageResourceId;
    private String nombreRestaurante;
    private String cantidad;
    private String direccion;

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNombreRestaurante() {
        return nombreRestaurante;
    }

    public void setNombreRestaurante(String nombreRestaurante) {
        this.nombreRestaurante = nombreRestaurante;
    }
    //public int getImageResourceId() {
        //return imageResourceId;
    //}

    //public void setImageResourceId(int imageResourceId) {
        //this.imageResourceId = imageResourceId;
    //}
}
