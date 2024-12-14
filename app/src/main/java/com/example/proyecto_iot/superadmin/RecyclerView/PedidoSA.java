package com.example.proyecto_iot.superadmin.RecyclerView;

import java.util.List;

public class PedidoSA {

    private String direccion;
    private int estado;
    private String fechaHora;
    private String idCliente;
    private String idRestaurante;
    private String idRepartidor;
    private String nombreRestaurante;
    private int pagoTotal;
    private List<ProductoSA> productos;
    private String qrUrl;

    // Getters y Setters
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

    public String getIdRestaurante() {
        return idRestaurante;
    }

    public void setIdRestaurante(String idRestaurante) {
        this.idRestaurante = idRestaurante;
    }

    public String getIdRepartidor() {
        return idRepartidor;
    }

    public void setIdRepartidor(String idRepartidor) {
        this.idRepartidor = idRepartidor;
    }

    public String getNombreRestaurante() {
        return nombreRestaurante;
    }

    public void setNombreRestaurante(String nombreRestaurante) {
        this.nombreRestaurante = nombreRestaurante;
    }

    public int getPagoTotal() {
        return pagoTotal;
    }

    public void setPagoTotal(int pagoTotal) {
        this.pagoTotal = pagoTotal;
    }

    public List<ProductoSA> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoSA> productos) {
        this.productos = productos;
    }

    public String getQrUrl() {
        return qrUrl;
    }

    public void setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }
}
