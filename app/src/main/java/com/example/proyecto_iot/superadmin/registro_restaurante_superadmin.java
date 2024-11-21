package com.example.proyecto_iot.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.superadmin.RecyclerView.Administrador;
import com.example.proyecto_iot.superadmin.RecyclerView.RestauranteSA;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class registro_restaurante_superadmin extends AppCompatActivity {

    Administrador administrador;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    private EditText textFieldNombre, textFieldUbicacion, textFieldDescripccion;
    private Spinner spinnerTipoComida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_activity_registro_restaurante);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        administrador = (Administrador) getIntent().getSerializableExtra("administrador");


        Button btnGuardar = findViewById(R.id.buttonRegistrarRest);

        textFieldNombre = findViewById(R.id.editTextNombreRest);
        textFieldUbicacion = findViewById(R.id.editTextUbicacionRest);
        textFieldDescripccion = findViewById(R.id.editTextDescripcionRest);

        //Gestion de spinner tipo comida
        spinnerTipoComida = findViewById(R.id.spinnerTipoComida);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.tipo_de_comidas, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoComida.setAdapter(adapter1);
        //----------------------------------------------------------------------------

        btnGuardar.setOnClickListener(view -> {
            String nombre = textFieldNombre.getText().toString();
            String ubicacion = textFieldUbicacion.getText().toString();
            String tipoComida = spinnerTipoComida.getSelectedItem().toString();
            String descripcion = textFieldDescripccion.getText().toString();

            if (nombre.isEmpty() || descripcion.isEmpty()) {

                Toast.makeText(this, "Debe completar todos los campos", Toast.LENGTH_LONG).show();

            }else {

                RestauranteSA restaurante = new RestauranteSA();
                restaurante.setNombre(nombre);
                restaurante.setUbicacion(ubicacion);
                restaurante.setTipoDeComida(tipoComida);
                restaurante.setDescripcion(descripcion);
                restaurante.setDniAdministrador(administrador.getDni());

                registrarAdmin(administrador.getCorreo(), administrador.getPasswd());

                db.collection("restaurantes")
                        .document(nombre)
                        .set(restaurante)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Administrador y restaurante registrados exitosamente", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, gestion_usuarios_superadmin.class);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Algo ocurrió al intentar registrados el administrador y restaurante", Toast.LENGTH_SHORT).show();
                        });
            }
        });

    }

    private void registrarAdmin(String email, String password) {

        db.collection("administradores")
                .document()
                .set(administrador)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        // Error al registrar
                        Toast.makeText(this, "Error al agregar la info a la collecion: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, gestion_usuarios_superadmin.class);
                        startActivity(intent);
                    }
                });

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        // Error al registrar
                        Toast.makeText(this, "Error registro admin: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, gestion_usuarios_superadmin.class);
                        startActivity(intent);
                    }
                });


    }

}