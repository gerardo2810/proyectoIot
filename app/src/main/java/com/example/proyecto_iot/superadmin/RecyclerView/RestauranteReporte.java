package com.example.proyecto_iot.superadmin.RecyclerView;

public class RestauranteReporte {

    private String uid;
    private String nombre;
    private String idAdministrador;
    private String foto; // URL de la foto

    public RestauranteReporte() {}

    public RestauranteReporte(String uid, String nombre, String idAdministrador, String foto) {
        this.uid = uid;
        this.nombre = nombre;
        this.idAdministrador = idAdministrador;
        this.foto = foto;
    }

    public String getUid() { return uid; }
    public String getNombre() { return nombre; }
    public String getIdAdministrador() { return idAdministrador; }
    public String getFoto() { return foto; }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setIdAdministrador(String idAdministrador) {
        this.idAdministrador = idAdministrador;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
