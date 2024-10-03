package com.example.proyecto_iot.repartidor.RecyclerView;

public class GananciaxDia {
    private String fecha;
    private String nombreRestaurante;
    private String gananciaPedido;
    private String total;

    public GananciaxDia(String fecha, String nombreRestaurante, String gananciaPedido, String total) {
        this.fecha = fecha;
        this.nombreRestaurante = nombreRestaurante;
        this.gananciaPedido = gananciaPedido;
        this.total = total;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNombreRestaurante() {
        return nombreRestaurante;
    }

    public void setNombreRestaurante(String nombreRestaurante) {
        this.nombreRestaurante = nombreRestaurante;
    }

    public String getGananciaPedido() {
        return gananciaPedido;
    }

    public void setGananciaPedido(String gananciaPedido) {
        this.gananciaPedido = gananciaPedido;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
