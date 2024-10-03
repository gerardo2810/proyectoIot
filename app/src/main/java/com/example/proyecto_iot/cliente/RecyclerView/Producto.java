package com.example.proyecto_iot.cliente.RecyclerView;



public class Producto {
    private String nombre;
    private String descripcion;
    private double precio;
    private int cantidad;  // Nueva propiedad para manejar la cantidad del producto
    private int imageResourceId; // Campo para el ID del recurso de imagen

    public Producto(String nombre, String descripcion, double precio,int cantidad, int imageResourceId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.cantidad = cantidad; // Inicializamos con una cantidad mínima de 1
        this.imageResourceId= imageResourceId;
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

    public double getTotal() {
        return this.precio * this.cantidad;
    }
    public int getImageResourceId() {
        return imageResourceId;
    }
}

