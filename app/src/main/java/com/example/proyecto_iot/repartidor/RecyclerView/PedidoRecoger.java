package com.example.proyecto_iot.repartidor.RecyclerView;

public class PedidoRecoger {

    private String idPedido;
    private String cantidad;
    private String direccion;
    private String idRestaurante;
    private String fotoLogo;
    private String direccionRest;

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getIdRestaurante() {
        return idRestaurante;
    }

    public void setIdRestaurante(String idRestaurante) {
        this.idRestaurante = idRestaurante;
    }

    public String getFotoLogo() {
        return fotoLogo;
    }

    public void setFotoLogo(String fotoLogo) {
        this.fotoLogo = fotoLogo;
    }

    public String getDireccionRest() {
        return direccionRest;
    }

    public void setDireccionRest(String direccionRest) {
        this.direccionRest = direccionRest;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }
}
