package com.example.proyecto_iot.admin_restaurante.RecyclerView;

public class ProductoPedido {
    private int cantidad;
    private String descripcion;
    private String id;
    private String imageUrl;
    private String nombre;
    private double precio;
    private double total;

    // Default constructor required for Firebase
    public ProductoPedido() {
    }

    public ProductoPedido(int cantidad, String descripcion, String id, String imageUrl, String nombre, double precio, double total) {
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.id = id;
        this.imageUrl = imageUrl;
        this.nombre = nombre;
        this.precio = precio;
        this.total = total;
    }

    // Getters and Setters
    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
