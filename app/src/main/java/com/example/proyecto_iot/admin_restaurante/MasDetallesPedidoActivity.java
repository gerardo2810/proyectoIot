package com.example.proyecto_iot.admin_restaurante;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;

public class MasDetallesPedidoActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_ver_detalles_pedido);

        ImageView backButton = findViewById(R.id.back_again);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Termina esta actividad para volver a la anterior (PorAceptarFragment)
                finish();
            }
        });
    }
}
