package com.example.proyecto_iot.superadmin.RecyclerView;

public class RepartidorSA {

    private String nombre;
    private String apellido;
    private String fecha;

    public RepartidorSA(String nombre, String apellido, String fecha) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getFecha() {
        return fecha;
    }

}
