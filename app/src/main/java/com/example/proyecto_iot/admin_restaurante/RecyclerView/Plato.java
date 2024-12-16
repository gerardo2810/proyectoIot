package com.example.proyecto_iot.admin_restaurante.RecyclerView;

public class Plato {
    private String idProducto;
    private String cantVendida; // Ej: "20 unidades"

    public Plato(String idProducto, String cantVendida) {
        this.idProducto = idProducto;
        this.cantVendida = cantVendida;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public String getCantVendida() {
        return cantVendida;
    }
}
