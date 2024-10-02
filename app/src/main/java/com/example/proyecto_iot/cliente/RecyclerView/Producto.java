package com.example.proyecto_iot.cliente.RecyclerView;



public class Producto {

    private String nombre;
    private String descripcion;
    private double precio;
    private int cantidad;

    public Producto(String nombre, String descripcion, double precio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.cantidad = 1; // Cantidad inicial es 1
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

    public void incrementarCantidad() {
        this.cantidad++;
    }

    public void disminuirCantidad() {
        if (this.cantidad > 1) {
            this.cantidad--;
        }
    }
}
