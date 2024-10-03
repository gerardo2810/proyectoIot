package com.example.proyecto_iot.admin_restaurante.RecyclerView;

public class Plato {
    private String nombre;
    private String categoria;
    private String descripcion;
    private String cantVendida;
    private String ganancia;
    private String precio;
    private int imageResId;

    public Plato(String nombre, String categoria, String descripcion, String precio, String cantVendida, String ganancia,  int imageResId) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.precio = precio;
        this.cantVendida = cantVendida;
        this.ganancia = ganancia;
        this.imageResId = imageResId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCantVendida() {
        return cantVendida;
    }

    public void setCantVendida(String cantVendida) {
        this.cantVendida = cantVendida;
    }

    public String getGanancia() {
        return ganancia;
    }

    public void setGanancia(String ganancia) {
        this.ganancia = ganancia;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}
