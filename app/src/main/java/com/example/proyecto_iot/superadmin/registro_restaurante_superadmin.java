package com.example.proyecto_iot.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.superadmin.RecyclerView.RestauranteSA;
import com.google.firebase.firestore.FirebaseFirestore;

public class registro_restaurante_superadmin extends AppCompatActivity {

    FirebaseFirestore db;
    private EditText textFieldNombre, textFieldDescripccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_activity_registro_restaurante);

        db = FirebaseFirestore.getInstance();

        String dniAdmin = getIntent().getStringExtra("dni_admin");

        Button btnGuardar = findViewById(R.id.buttonRegistrarRest);

        textFieldNombre = findViewById(R.id.editTextNombreRest);
        textFieldDescripccion = findViewById(R.id.editTextDescripcionRest);

        btnGuardar.setOnClickListener(view -> {
            String nombre = textFieldNombre.getText().toString();
            String descripcion = textFieldDescripccion.getText().toString();

            if (nombre.isEmpty() || descripcion.isEmpty()) {

                Toast.makeText(this, "Debe completar todos los campos", Toast.LENGTH_LONG).show();

            }else {

                RestauranteSA restaurante = new RestauranteSA();
                restaurante.setNombre(nombre);
                restaurante.setDescripcion(descripcion);
                restaurante.setDniAdministrador(dniAdmin);

                db.collection("restaurantes")
                        .document(nombre)
                        .set(restaurante)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Restaurante creado exitosamente", Toast.LENGTH_SHORT).show();
                            // Redirigir a otra actividad
                            Intent intent = new Intent(this, gestion_usuarios_superadmin.class); // Cambia "OtraActividad" por tu actividad deseada
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Algo ocurrió al intentar crear el restaurante", Toast.LENGTH_SHORT).show();
                        });
            }
        });

    }
}