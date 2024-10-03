package com.example.proyecto_iot.cliente.RecyclerView;

public class Pedido {
    private String nombreRestaurante;
    private String estado;
    private String fecha;
    private int pedidoId;
    public Pedido(String nombreRestaurante, String estado, String fecha, int pedidoId) {
        this.nombreRestaurante = nombreRestaurante;
        this.estado = estado;
        this.fecha = fecha;
        this.pedidoId=pedidoId;
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

    public int getPedidoId() {
        return pedidoId;
    }
}
