package com.example.proyecto_iot.admin_restaurante;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;

public class EditRestaurantInfoActivity extends AppCompatActivity {
    private EditText etRestaurantName;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_editar_info_restaurante);

        etRestaurantName = findViewById(R.id.et_restaurant_name);
        btnSave = findViewById(R.id.btn_save_restaurant_info);

        btnSave.setOnClickListener(v -> {
            // Lógica para guardar los datos del restaurante
            String restaurantName = etRestaurantName.getText().toString();
            // Guardar en base de datos o enviar a backend
            Toast.makeText(this, "Datos del restaurante guardados", Toast.LENGTH_SHORT).show();
        });
    }
}
