package com.example.proyecto_iot.admin_restaurante.RecyclerView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RestauranteViewModel extends ViewModel {
    private final MutableLiveData<String> idRestaurante = new MutableLiveData<>();

    public void setIdRestaurante(String id) {
        idRestaurante.setValue(id);
    }

    public LiveData<String> getIdRestaurante() {
        return idRestaurante;
    }
}