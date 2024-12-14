package com.example.proyecto_iot.admin_restaurante.RecyclerView;

import java.util.List;

public class Pedido {
    private String id;
    private String direccion;
    private int estado;
    private String fechaHora;
    private String idCliente;
    private String idRepartidor;
    private String nombreCliente;
    private String idRestaurante;
    private String nombreRestaurante;
    private double pagoTotal;
    private List<ProductoPedido> productos;
    private String qrUrl;
    private String nombreRepartidor;
    private boolean repartidorAsignado;

    // Default constructor required for Firebase
    public Pedido() {
    }

    public Pedido(String direccion, int estado, String fechaHora, String idCliente, String idRepartidor,
                  String idRestaurante, String nombreRestaurante, double pagoTotal, List<ProductoPedido> productos, String qrUrl) {
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

    public List<ProductoPedido> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoPedido> productos) {
        this.productos = productos;
    }

    public String getQrUrl() {
        return qrUrl;
    }

    public void setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNombreRepartidor() {
        return nombreRepartidor;
    }

    public void setNombreRepartidor(String nombreRepartidor) {
        this.nombreRepartidor = nombreRepartidor;
    }

    public boolean isRepartidorAsignado() {
        return repartidorAsignado;
    }

    public void setRepartidorAsignado(boolean repartidorAsignado) {
        this.repartidorAsignado = repartidorAsignado;
    }
}
