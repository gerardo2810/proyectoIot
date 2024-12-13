package com.example.proyecto_iot.superadmin.RecyclerView;

public class RepartidorSA {

    private String id;
    private String nombre;
    private String apellido;
    private String fecha;
    private String foto;

    public RepartidorSA() {
        // Constructor vacío requerido para Firestore
    }

    public RepartidorSA(String id, String nombre, String apellido, String fecha) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getFoto() {
        return foto;
    }
}
