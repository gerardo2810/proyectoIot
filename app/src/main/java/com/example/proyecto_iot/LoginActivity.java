package com.example.proyecto_iot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.repartidor.RegistroRepartidorActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

    }

    public void abrirPagRegistroRepartidor (View view) {
        Intent intent = new Intent(this, RegistroRepartidorActivity.class);
        startActivity(intent);
    }
}
