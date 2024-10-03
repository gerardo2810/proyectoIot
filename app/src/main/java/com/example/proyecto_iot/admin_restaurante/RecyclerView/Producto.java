package com.example.proyecto_iot.admin_restaurante.RecyclerView;

public class Producto {
    private String id;
    private String name;
    private String descripcion;
    private int stock;
    private double price;
    private boolean isActive;
    private int imageResId; // ID del recurso de la imagen del producto

    public Producto(String id, String name, String descripcion, int stock, double price, boolean isActive, int imageResId) {
        this.id = id;
        this.name = name;
        this.descripcion = descripcion;
        this.stock = stock;
        this.price = price;
        this.isActive = isActive;
        this.imageResId = imageResId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
