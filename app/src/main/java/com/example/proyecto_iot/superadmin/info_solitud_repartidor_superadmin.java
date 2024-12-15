package com.example.proyecto_iot.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class info_solitud_repartidor_superadmin extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String UID;
    private String nombrecompleto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_activity_info_solitud_repartidor);

        Intent intent = getIntent();
        UID = intent.getStringExtra("document_id");

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        Button btnAceptar = findViewById(R.id.btnAceptar);
        Button btnRechazar = findViewById(R.id.btnRechazar);

        //Volver una pantalla atras
        ImageView arrowIcon = findViewById(R.id.arrow_back_icon);
        arrowIcon.setOnClickListener(v -> {
            Intent intent2 = new Intent(info_solitud_repartidor_superadmin.this, solicitudes_repartidores_superadmin.class);
            startActivity(intent2);
        });
        //----------------------------------------------------------------------------

        //Gestion de la Informacion
        db.collection("repartidores").document(UID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Asignar los valores a los TextView
                        TextView textViewNombre = findViewById(R.id.textViewNombre);
                        TextView textViewDNI = findViewById(R.id.textViewDNI);
                        TextView textViewCorreo = findViewById(R.id.textViewCorreo);
                        TextView textViewTelefono = findViewById(R.id.textViewTelefono);
                        TextView textViewFechNaci = findViewById(R.id.textViewFechNaci);
                        TextView textViewDireccion = findViewById(R.id.textViewDireccion);
                        nombrecompleto = documentSnapshot.getString("nombre") + " " + documentSnapshot.getString("apellido");

                        textViewNombre.setText(nombrecompleto);
                        textViewDNI.setText(documentSnapshot.getString("dni"));
                        textViewCorreo.setText(documentSnapshot.getString("correo"));
                        textViewTelefono.setText(documentSnapshot.getString("telefono"));
                        textViewFechNaci.setText(documentSnapshot.getString("nacimiento"));
                        textViewDireccion.setText(documentSnapshot.getString("direccion"));
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        btnAceptar.setOnClickListener(v -> {
            db.collection("repartidores").document(UID)
                    .update("aceptado", true)
                    .addOnSuccessListener(aVoid ->{
                            guardarLog("Se aceptó la solicitud para ser repartidor de " + nombrecompleto, "Super Administrador");
                            Intent intent3 = new Intent(this, solicitudes_repartidores_superadmin.class);
                            Toast.makeText(this, "Se aceptó la solicitud de " + nombrecompleto, Toast.LENGTH_SHORT).show();
                            startActivity(intent3);
                            finish();
                    })
                    .addOnFailureListener(e ->
                    {
                        Intent intent3 = new Intent(this, solicitudes_repartidores_superadmin.class);
                        Toast.makeText(this, "Error, intentelo de nuevo ", Toast.LENGTH_SHORT).show();
                        startActivity(intent3);
                        finish();
                    });
        });

        btnRechazar.setOnClickListener(v -> {
            db.collection("repartidores").document(UID)
                    .update("habilitado", false)
                    .addOnSuccessListener(aVoid ->{
                        guardarLog("Se rechazó la solicitud para ser repartidor de " + nombrecompleto, "Super Administrador");
                        Intent intent3 = new Intent(this, solicitudes_repartidores_superadmin.class);
                        Toast.makeText(this, "Se rechazó la solicitud de " + nombrecompleto, Toast.LENGTH_SHORT).show();
                        startActivity(intent3);
                        finish();
                    })
                    .addOnFailureListener(e ->
                    {
                        Intent intent3 = new Intent(this, solicitudes_repartidores_superadmin.class);
                        Toast.makeText(this, "Error, intentelo de nuevo ", Toast.LENGTH_SHORT).show();
                        startActivity(intent3);
                        finish();
                    });
        });
        //----------------------------------------------------------------------------

        //Gestion de la bottom navigation bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        int selectedItemId = getIntent().getIntExtra("SELECTED_ITEM_ID", R.id.navigation_usuarios);
        bottomNavigationView.setSelectedItemId(selectedItemId);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                if (item.getItemId() == R.id.navigation_usuarios) {
                    intent = new Intent(info_solitud_repartidor_superadmin.this, gestion_usuarios_superadmin.class);
                } else if (item.getItemId() == R.id.navigation_reportes) {
                    intent = new Intent(info_solitud_repartidor_superadmin.this, gestion_reportes_superadmin.class);
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

    public void guardarLog(String mensaje, String rol) {
        // Obtener el UID del usuario logueado
        String usuarioUID = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "Usuario desconocido";

        // Obtener fecha y hora actuales
        String fechaActual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String horaActual = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        // Crear un mapa para guardar el log
        HashMap<String, Object> logData = new HashMap<>();
        logData.put("mensaje", mensaje);
        logData.put("usuarioUID", usuarioUID);
        logData.put("rol", rol);
        logData.put("fecha", fechaActual);
        logData.put("hora", horaActual);

        // Guardar el log en Firestore
        db.collection("logs")
                .add(logData)
                .addOnSuccessListener(documentReference -> {
                    // Éxito al guardar el log
                    System.out.println("Log guardado con éxito. ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    // Error al guardar el log
                    System.err.println("Error al guardar el log: " + e.getMessage());
                });
    }

}