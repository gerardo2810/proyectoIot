package com.example.proyecto_iot.superadmin.RecyclerView;

public class ProductoResumen {

    private String nombre;    // Nombre del producto
    private int cantidad;     // Cantidad total del producto
    private double precio;    // Precio unitario del producto
    private double total;     // Total acumulado para este producto (cantidad * precio)

    // Constructor
    public ProductoResumen(String nombre, int cantidad, double precio, double total) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.total = total;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
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

    // Método para sumar la cantidad
    public void sumarCantidad(int cantidadAdicional) {
        this.cantidad += cantidadAdicional;
    }

    // Método para sumar el total
    public void sumarTotal(double totalAdicional) {
        this.total += totalAdicional;
    }

}
