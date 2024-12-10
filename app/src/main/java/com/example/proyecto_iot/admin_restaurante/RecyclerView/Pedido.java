package com.example.proyecto_iot.admin_restaurante.RecyclerView;

import java.util.List;

public class Pedido {
    private String direccion;
    private int estado;
    private String fechaHora;
    private String idCliente;
    private String idRepartidor;
    private String idRestaurante;
    private String nombreRestaurante;
    private double pagoTotal;
    private List<Producto> productos;
    private String qrUrl;

    // Default constructor required for Firebase
    public Pedido() {
    }

    public Pedido(String direccion, int estado, String fechaHora, String idCliente, String idRepartidor,
                  String idRestaurante, String nombreRestaurante, double pagoTotal, List<Producto> productos, String qrUrl) {
        this.direccion = direccion;
        this.estado = estado;
        this.fechaHora = fechaHora;
        this.idCliente = idCliente;
        this.idRepartidor = idRepartidor;
        this.idRestaurante = idRestaurante;
        this.nombreRestaurante = nombreRestaurante;
        this.pagoTotal = pagoTotal;
        this.productos = productos;
        this.qrUrl = qrUrl;
    }

    // Getters and Setters
    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdRepartidor() {
        return idRepartidor;
    }

    public void setIdRepartidor(String idRepartidor) {
        this.idRepartidor = idRepartidor;
    }

    public String getIdRestaurante() {
        return idRestaurante;
    }

    public void setIdRestaurante(String idRestaurante) {
        this.idRestaurante = idRestaurante;
    }

    public String getNombreRestaurante() {
        return nombreRestaurante;
    }

    public void setNombreRestaurante(String nombreRestaurante) {
        this.nombreRestaurante = nombreRestaurante;
    }

    public double getPagoTotal() {
        return pagoTotal;
    }

    public void setPagoTotal(double pagoTotal) {
        this.pagoTotal = pagoTotal;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public String getQrUrl() {
        return qrUrl;
    }

    public void setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }
}
