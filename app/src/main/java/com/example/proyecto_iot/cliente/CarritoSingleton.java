package com.example.proyecto_iot.cliente;

import com.example.proyecto_iot.cliente.RecyclerView.Producto;

import java.util.ArrayList;
import java.util.List;

public class CarritoSingleton {

    private static CarritoSingleton instance;
    private List<Producto> productos;

    private CarritoSingleton() {
        productos = new ArrayList<>();
    }

    public static CarritoSingleton getInstance() {
        if (instance == null) {
            instance = new CarritoSingleton();
        }
        return instance;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public void addProducto(Producto producto) {
        productos.add(producto);
    }

    public void clearCarrito() {
        productos.clear();
    }
}
