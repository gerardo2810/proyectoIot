package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;

public class AbrirRestauranteActivity extends AppCompatActivity {
    private Button buttonOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.restaurante_activity_abrir_restaurante);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Recibir el idRestaurante del intent
        Intent intent = getIntent();
        String idRestaurante = intent.getStringExtra("idRestaurante");

        buttonOpen = findViewById(R.id.open_button);
        buttonOpen.setOnClickListener(v -> {
            // Pasar el idRestaurante a InicioRestauranteActivity
            Intent inicioIntent = new Intent(AbrirRestauranteActivity.this, InicioRestauranteActivity.class);
            inicioIntent.putExtra("idRestaurante", idRestaurante); // Pasar el idRestaurante
            startActivity(inicioIntent);
            finish();
        });
    }
}
