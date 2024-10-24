package com.example.proyecto_iot.superadmin.RecyclerView;

public class ReporteSA {

    private String nombre_restaurante;
    private String admin_restaurante;
    private String fecha;
    private String tipo_reporte;

    public ReporteSA(String nombre_restaurante, String admin_restaurante, String fecha, String tipo_reporte) {
        this.nombre_restaurante = nombre_restaurante;
        this.admin_restaurante = admin_restaurante;
        this.fecha = fecha;
        this.tipo_reporte = tipo_reporte;
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

    public String getTipo_reporte() {
        return tipo_reporte;
    }
}
