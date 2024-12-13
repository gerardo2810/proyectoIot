package com.example.proyecto_iot.cliente.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private String idPedido;
    private String idRestaurante;
    private String nombreRestaurante;
    private String fecha;
    private int pedidoId;
    private int imageResourceId;
    private int estado;
    private double pagoTotal;
    private String direccion;
    private String fechaHora;
    private String idRepartidor;
    private List<Producto> productos;
    public Pedido(String nombreRestaurante, int estado, String fecha, int pedidoId,int imageResourceId) {
        this.nombreRestaurante = nombreRestaurante;
        this.estado = estado;
        this.fecha = fecha;
        this.pedidoId=pedidoId;
        this.imageResourceId=imageResourceId;
    }
    public Pedido(String idRestaurante, String nombreRestaurante, int estado) {
        this.idRestaurante = idRestaurante;
        this.nombreRestaurante = nombreRestaurante;
        this.estado = estado;
    }
    public Pedido(String idPedido, String idRestaurante, String nombreRestaurante, int estado, String fechaHora, String direccion, double pagoTotal, List<Producto> productos) {
        this.idPedido = idPedido;
        this.idRestaurante = idRestaurante;
        this.nombreRestaurante = nombreRestaurante;
        this.estado = estado;
        this.fechaHora = fechaHora;
        this.direccion = direccion;
        this.pagoTotal = pagoTotal;
        this.productos = (productos != null) ? productos : new ArrayList<>();
    }


    public String getIdRestaurante() {
        return idRestaurante;
    }
    public String getIdRepartidor() {
        return idRepartidor;
    }
    public List<Producto> getProductos() {
        return productos;
    }
    public String getFechaHora() {
        return fechaHora;
    }
    public  String getIdPedido(){
        return idPedido;
    }

    public String getNombreRestaurante() {
        return nombreRestaurante;
    }

    public int getEstado() {
        return estado;
    }

    public String getFecha() {
        return fecha;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public double getPagoTotal() {
        return pagoTotal;
    }
    public String getDireccion() {
        return direccion;
    }

}
