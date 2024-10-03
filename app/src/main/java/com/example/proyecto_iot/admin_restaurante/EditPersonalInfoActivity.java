package com.example.proyecto_iot.admin_restaurante;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;

public class EditPersonalInfoActivity extends AppCompatActivity {
    private EditText etFirstName, etLastName;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_editar_info_personal);

        etFirstName = findViewById(R.id.et_name);
        btnSave = findViewById(R.id.btn_save_personal_info);

        btnSave.setOnClickListener(v -> {
            // Lógica para guardar los datos personales
            String firstName = etFirstName.getText().toString();
            // Guardar en base de datos o enviar a backend
            Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show();
        });
    }
}
