package com.example.proyecto_iot.cliente.RecyclerView;

import java.io.Serializable;

public class Producto implements Serializable {
    private  String id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int cantidad;
    private String imageUrl; // URL de la imagen desde Firebase

    public Producto(String id,String nombre, String descripcion, double precio, int cantidad, String imageUrl) {
        this.id =id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.cantidad = cantidad;
        this.imageUrl = imageUrl;
    }
    public Producto(String id, String descripcion, int cantidad) {
        this.id =id;
        this.descripcion = descripcion;
        this.cantidad = cantidad;

    }
    public String getId(){
            return  id;
    }
    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void incrementarCantidad() {
        this.cantidad++;
    }

    public void disminuirCantidad() {
        if (this.cantidad > 1) {
            this.cantidad--;
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public double getTotal() {
        return this.precio * this.cantidad;
    }

}
