package com.example.proyecto_iot.superadmin.RecyclerView;

public class ReporteSA {

    private String nombre_restaurante;
    private String admin_restaurante;
    private String fecha;

    public ReporteSA(String nombre_restaurante, String admin_restaurante, String fecha) {
        this.nombre_restaurante = nombre_restaurante;
        this.admin_restaurante = admin_restaurante;
        this.fecha = fecha;
    }

    public String getNombre_restaurante() {
        return nombre_restaurante;
    }

    public String getAdmin_restaurante() {
        return admin_restaurante;
    }

    public String getFecha() {
        return fecha;
    }
}
