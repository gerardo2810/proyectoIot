package com.example.proyecto_iot.repartidor;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditarPerfilRepartidorActivity extends AppCompatActivity {

    EditText etNombre, etFecha, etDireccion, etCorreo, etTelefono, etDni;
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil_repartidor);

        etNombre = findViewById(R.id.et_name);
        etDni = findViewById(R.id.et_dni);
        etFecha = findViewById(R.id.et_fecha);
        etCorreo = findViewById(R.id.et_correo);
        etTelefono = findViewById(R.id.et_telefono);
        etDireccion = findViewById(R.id.et_domicilio);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        cargarDatosUsuario();


    }

    public void cargarDatosUsuario() {
        String userId = auth.getCurrentUser().getUid(); // Obtén el UID del usuario actual

        DocumentReference userDoc = db.collection("repartidores").document(userId);
        userDoc.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Asignar datos a los campos de texto
                etNombre.setText(documentSnapshot.getString("nombre"));
                etDni.setText(documentSnapshot.getString("dni"));
                etFecha.setText(documentSnapshot.getString("nacimiento"));
                etCorreo.setText(documentSnapshot.getString("correo"));
                etTelefono.setText(documentSnapshot.getString("telefono"));
                etDireccion.setText(documentSnapshot.getString("direccion"));
            } else {
                Toast.makeText(this, "No se encontraron datos del usuario.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al cargar los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
