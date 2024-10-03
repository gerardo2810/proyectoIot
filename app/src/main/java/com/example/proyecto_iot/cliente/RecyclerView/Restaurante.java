package com.example.proyecto_iot.cliente.RecyclerView;

public class Restaurante {

    private String nameTitlte;
    private double priceDelivery;
    private String category;
    private String location;
    private int imageResourceId; // Campo para el ID del recurso de imagen


    public Restaurante(String nameTitlte, double priceDelivery, String category, String location, int imageResourceId) {
        this.nameTitlte = nameTitlte;
        this.priceDelivery = priceDelivery;
        this.category = category;
        this.location = location;
        this.imageResourceId=imageResourceId;
    }

    public String getNameTitlte() {
        return nameTitlte;
    }

    public double getProductPrice() {
        return priceDelivery;
    }

    public String getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }
    public int getImageResourceId() {
        return imageResourceId;
    }
}

