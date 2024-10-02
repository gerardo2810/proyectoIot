package com.example.proyecto_iot.admin_restaurante.RecyclerView;

public class Order {

    private String estado;
    private String orderId;
    private String date;
    private String cliente;
    private String direccion;
    private String precio;
    private String repartidor;

    public Order(String estado, String orderId, String date, String cliente, String direccion, String precio, String repartidor) {
        this.estado = estado;
        this.orderId = orderId;
        this.date = date;
        this.cliente = cliente;
        this.direccion = direccion;
        this.precio = precio;
        this.repartidor = repartidor;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(String repartidor) {
        this.repartidor = repartidor;
    }
}

