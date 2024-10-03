package com.example.proyecto_iot.admin_restaurante;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;

public class ViewRestaurantScheduleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_horarios);

        Button btnCloseRestaurant = findViewById(R.id.btn_close_restaurant);

        btnCloseRestaurant.setOnClickListener(v -> {
            // Lógica para cerrar el restaurante (puedes mostrar un dialogo de confirmación)
            Toast.makeText(this, "Restaurante cerrado", Toast.LENGTH_SHORT).show();
        });
    }
}
