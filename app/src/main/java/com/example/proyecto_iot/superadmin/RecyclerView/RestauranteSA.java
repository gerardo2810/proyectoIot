package com.example.proyecto_iot.superadmin.RecyclerView;

public class RestauranteSA {

    private String Nombre;
    private String Ubicacion;
    private String Descripción;
    private String tipoDeComida;
    private String FotoLogo;
    private String idAdministrador;
    private boolean isOpen;

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getUbicacion() {
        return Ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        Ubicacion = ubicacion;
    }

    public String getDescripción() {
        return Descripción;
    }

    public void setDescripción(String descripción) {
        Descripción = descripción;
    }

    public String getTipoDeComida() {
        return tipoDeComida;
    }

    public void setTipoDeComida(String tipoDeComida) {
        this.tipoDeComida = tipoDeComida;
    }

    public String getFotoLogo() {
        return FotoLogo;
    }

    public void setFotoLogo(String fotoLogo) {
        FotoLogo = fotoLogo;
    }

    public String getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(String idAdministrador) {
        this.idAdministrador = idAdministrador;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
