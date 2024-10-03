package com.example.proyecto_iot.admin_restaurante.RecyclerView;

public class Pedido {
    private String orderId;
    private String cantidad;
    private String precio;

    public Pedido(String orderId, String cantidad, String precio) {
        this.orderId = orderId;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }
}
