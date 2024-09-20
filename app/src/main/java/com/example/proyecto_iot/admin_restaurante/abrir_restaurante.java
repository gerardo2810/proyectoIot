package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;

public class abrir_restaurante extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_abrir_restaurante);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView home = findViewById(R.id.menu_home);
        ImageView ordenes = findViewById(R.id.ordenes);
        ImageView carta = findViewById(R.id.carta);
        ImageView horarios = findViewById(R.id.horarios);
        ImageView reportes = findViewById(R.id.reportes);
        ImageView perfil = findViewById(R.id.perfil);

        Button abriRestu = findViewById(R.id.open_button);
        abriRestu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(abrir_restaurante.this, AceptarPedidoActivity.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(abrir_restaurante.this, AceptarPedidoActivity.class);
                startActivity(intent);
            }
        });

        ordenes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(abrir_restaurante.this, historial_ordenes.class);
                startActivity(intent);
            }
        });

        carta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(abrir_restaurante.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        horarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(abrir_restaurante.this, horarios_restaurante.class);
                startActivity(intent);
            }
        });

        reportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(abrir_restaurante.this, reportes_restaurante.class);
                startActivity(intent);
            }
        });

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(abrir_restaurante.this, perfil_admin_restaurante.class);
                startActivity(intent);
            }
        });


    }
}