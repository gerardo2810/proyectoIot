package com.example.proyecto_iot.repartidor.RecyclerView;

public class PedidoRecoger {
    private String cantidad;
    private String direccion;

    public PedidoRecoger(String cantidad, String direccion) {
        this.cantidad = cantidad;
        this.direccion = direccion;
    }

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
}
