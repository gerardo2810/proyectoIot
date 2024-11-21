package com.example.proyecto_iot.admin_restaurante.RecyclerView;

public class Producto {
    private String id;
    private String Nombre;
    private String Descripcion;
    private String Imagen;
    private String Precio;
    private String Stock;
    private String TiempoPreparacion;
    private String idCategoria;
    private Boolean isActive;

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

    public String getPrecio() {
        return Precio;
    }

    public void setPrecio(String precio) {
        Precio = precio;
    }

    public String getStock() {
        return Stock;
    }

    public void setStock(String stock) {
        Stock = stock;
    }

    public String getTiempoPreparacion() {
        return TiempoPreparacion;
    }

    public void setTiempoPreparacion(String tiempoPreparacion) {
        TiempoPreparacion = tiempoPreparacion;
    }

    public String getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(String idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
