package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;

public class PedidoDetallesActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_mas_detalles);

        ImageView backButton = findViewById(R.id.back_poraceptar);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Termina esta actividad para volver a la anterior (PorAceptarFragment)
                finish();
            }
        });
    }

}
