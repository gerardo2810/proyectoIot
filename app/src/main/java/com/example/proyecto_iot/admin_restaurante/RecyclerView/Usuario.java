package com.example.proyecto_iot.admin_restaurante.RecyclerView;

public class Usuario {
    private String nombre;
    private String edad;
    private String dni;
    private String correo;
    private String telefono;
    private String cantPedidos;
    private String gastado;

    public Usuario(String nombre, String edad, String dni, String correo, String telefono, String cantPedidos, String gastado) {
        this.nombre = nombre;
        this.edad = edad;
        this.dni = dni;
        this.correo = correo;
        this.telefono = telefono;
        this.cantPedidos = cantPedidos;
        this.gastado = gastado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCantPedidos() {
        return cantPedidos;
    }

    public void setCantPedidos(String cantPedidos) {
        this.cantPedidos = cantPedidos;
    }

    public String getGastado() {
        return gastado;
    }

    public void setGastado(String gastado) {
        this.gastado = gastado;
    }
}
