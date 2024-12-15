package com.example.proyecto_iot.superadmin.RecyclerView;

public class LogSA {

    private String mensaje;
    private String usuarioUID;
    private String rol;
    private String fecha;
    private String hora;

    // Constructor vacío (necesario para Firebase)
    public LogSA() {}

    // Constructor completo
    public LogSA(String mensaje, String usuarioUID, String rol, String fecha, String hora) {
        this.mensaje = mensaje;
        this.usuarioUID = usuarioUID;
        this.rol = rol;
        this.fecha = fecha;
        this.hora = hora;
    }

    // Getters y Setters
    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getUsuarioUID() {
        return usuarioUID;
    }

    public void setUsuarioUID(String usuarioUID) {
        this.usuarioUID = usuarioUID;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

}
