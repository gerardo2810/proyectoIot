package com.example.proyecto_iot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.admin_restaurante.AbrirRestauranteActivity;
import com.example.proyecto_iot.cliente.inicio_cliente;
import com.example.proyecto_iot.repartidor.InicioRepartidorActivity;
import com.example.proyecto_iot.superadmin.gestion_usuarios_superadmin;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_cliente).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, inicio_cliente.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_admin_restaurante).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AbrirRestauranteActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_superadmin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, gestion_usuarios_superadmin.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_repartidor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InicioRepartidorActivity.class);
                startActivity(intent);
            }
        });
    }
}