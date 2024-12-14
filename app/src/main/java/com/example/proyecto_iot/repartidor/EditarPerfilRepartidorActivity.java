package com.example.proyecto_iot.repartidor;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.superadmin.editar_perfil_superadmin;
import com.example.proyecto_iot.superadmin.gestion_reportes_superadmin;
import com.example.proyecto_iot.superadmin.gestion_usuarios_superadmin;
import com.example.proyecto_iot.superadmin.perfil_superadmin;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditarPerfilRepartidorActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    EditText etNombre, etApellido, etFecha, etDireccion, etCorreo, etTelefono, etDni;
    ImageView ivFoto;
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil_repartidor);

        // Inicializar vistas
        etNombre = findViewById(R.id.et_name);
        etApellido = findViewById(R.id.et_lastname);
        etDni = findViewById(R.id.et_dni);
        etFecha = findViewById(R.id.et_fecha);
        etCorreo = findViewById(R.id.et_correo);
        etTelefono = findViewById(R.id.et_telefono);
        etDireccion = findViewById(R.id.et_domicilio);
        ivFoto = findViewById(R.id.imageViewSelected);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        cargarDatosUsuario();

        //Volver una pantalla atras
        LinearLayout regresar = findViewById(R.id.header_layout);

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Regresa a PerfilRepartidorActivity
                finish();
            }
        });
        //----------------------------------------------------------------------------
        //Gestion de la bottom navigation bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        int selectedItemId = getIntent().getIntExtra("SELECTED_ITEM_ID", R.id.navigation_perfil);
        bottomNavigationView.setSelectedItemId(selectedItemId);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                if (item.getItemId() == R.id.navigation_home) {
                    intent = new Intent(EditarPerfilRepartidorActivity.this, InicioRepartidorActivity.class);
                } else if (item.getItemId() == R.id.navigation_historial) {
                    intent = new Intent(EditarPerfilRepartidorActivity.this, HistorialRepartidorActivity.class);
                } else if (item.getItemId() == R.id.navigation_perfil) {
                    intent = new Intent(EditarPerfilRepartidorActivity.this, PerfilRepartidorActivity.class);
                }
                if (intent != null) {
                    intent.putExtra("SELECTED_ITEM_ID", item.getItemId());
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });
        //----------------------------------------------------------------------------


    }

    public void cargarDatosUsuario() {
        String userId = auth.getCurrentUser().getUid(); // Obtén el UID del usuario actual

        DocumentReference userDoc = db.collection("repartidores").document(userId);
        userDoc.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Asignar datos a los campos de texto
                etNombre.setText(documentSnapshot.getString("nombre"));
                etApellido.setText(documentSnapshot.getString("apellido"));
                etDni.setText(documentSnapshot.getString("dni"));
                etFecha.setText(documentSnapshot.getString("nacimiento"));
                etCorreo.setText(documentSnapshot.getString("correo"));
                etTelefono.setText(documentSnapshot.getString("telefono"));
                etDireccion.setText(documentSnapshot.getString("direccion"));
                // Cargar imagen de perfil
                String fotoUrl = documentSnapshot.getString("foto");
                if (fotoUrl != null && !fotoUrl.isEmpty()) {
                    Glide.with(this)
                            .load(fotoUrl)
                            .placeholder(R.drawable.placeholder)
                            .into(ivFoto);
                }
            } else {
                Toast.makeText(this, "No se encontraron datos del usuario.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al cargar los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
