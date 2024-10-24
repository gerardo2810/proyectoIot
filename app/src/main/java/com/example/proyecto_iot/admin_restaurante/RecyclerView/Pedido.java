package com.example.proyecto_iot.admin_restaurante.RecyclerView;

public class Pedido {
    private String cliente;
    private String orderId;
    private String cantidad;
    private String precio;
    private String tiempo;
    private String repartidor;
    private boolean isReady;

    public Pedido(String cliente, String orderId, String cantidad, String precio, String tiempo, String repartidor) {
        this.cliente = cliente;
        this.orderId = orderId;
        this.cantidad = cantidad;
        this.precio = precio;
        this.tiempo = tiempo;
        this.repartidor = repartidor;
        this.isReady = false;
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

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(String repartidor) {
        this.repartidor = repartidor;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }
}
