package com.example.proyecto_iot.admin_restaurante.RecyclerView;

public class Producto {
    private String id;
    private String Nombre;
    private String Descripcion;
    private String Imagen;
    private double Precio;
    private int Stock;
    private int TiempoPreparacion;
    private int cantidadDeVentas;
    private String idCategoria;
    private boolean isActive;

    public Producto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getImagen() {
        return Imagen;
    }

    public void setImagen(String imagen) {
        Imagen = imagen;
    }

    public double getPrecio() {
        return Precio;
    }

    public void setPrecio(double precio) {
        Precio = precio;
    }

    public int getStock() {
        return Stock;
    }

    public void setStock(int stock) {
        Stock = stock;
    }

    public int getTiempoPreparacion() {
        return TiempoPreparacion;
    }

    public void setTiempoPreparacion(int tiempoPreparacion) {
        TiempoPreparacion = tiempoPreparacion;
    }

    public int getCantidadDeVentas() {
        return cantidadDeVentas;
    }

    public void setCantidadDeVentas(int cantidadDeVentas) {
        this.cantidadDeVentas = cantidadDeVentas;
    }

    public String getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(String idCategoria) {
        this.idCategoria = idCategoria;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
