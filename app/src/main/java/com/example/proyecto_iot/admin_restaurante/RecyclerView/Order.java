package com.example.proyecto_iot.admin_restaurante.RecyclerView;

public class Order {

    private String orderId;
    private String address;
    private String price;
    private String status;

    public Order(String orderId, String address, String price, String status) {
        this.orderId = orderId;
        this.address = address;
        this.price = price;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getAddress() {
        return address;
    }

    public String getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }
}

