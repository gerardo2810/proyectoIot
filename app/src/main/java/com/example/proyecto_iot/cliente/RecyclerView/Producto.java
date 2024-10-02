package com.example.proyecto_iot.cliente.RecyclerView;



public class Producto {
    private String nombre;
    private String descripcion;
    private double precio;
    private int cantidad;  // Nueva propiedad para manejar la cantidad del producto

    public Producto(String nombre, String descripcion, double precio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.cantidad = 1; // Inicializamos con una cantidad mínima de 1
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

    public double getTotal() {
        return this.precio * this.cantidad;
    }
}

