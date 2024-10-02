package com.example.proyecto_iot.cliente.RecyclerView;

public class Pedido {
    private String nombreRestaurante;
    private String estado;
    private String fecha;

    public Pedido(String nombreRestaurante, String estado, String fecha) {
        this.nombreRestaurante = nombreRestaurante;
        this.estado = estado;
        this.fecha = fecha;
    }

    public String getNombreRestaurante() {
        return nombreRestaurante;
    }

    public String getEstado() {
        return estado;
    }

    public String getFecha() {
        return fecha;
    }
}
