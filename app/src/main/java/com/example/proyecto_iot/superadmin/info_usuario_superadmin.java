package com.example.proyecto_iot.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.FirebaseFirestore;

public class info_usuario_superadmin extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private String UID, rol;
    private FirebaseFirestore db;
    private String nombrecompleto;
    private boolean habilitado;
    private SwitchMaterial switchEstadoCuenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_activity_info_usuario);

        Intent intent = getIntent();
        UID = intent.getStringExtra("usuario_id");
        rol = intent.getStringExtra("rol");
        Log.d("VERIFICAR", "Rol: " + rol);
        switchEstadoCuenta = findViewById(R.id.switchEstadoCuenta);
        db = FirebaseFirestore.getInstance();

        //Volver una pantalla atras
        ImageView arrowIcon = findViewById(R.id.arrow_back_icon);
        arrowIcon.setOnClickListener(v -> {
            Intent intent2 = new Intent(info_usuario_superadmin.this, lista_usuarios_superadmin.class);
            startActivity(intent2);
        });
        //----------------------------------------------------------------------------

        //Gestion de la Informacion
        if(rol.equals("Administradores")) {
            db.collection("administradores").document(UID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Asignar los valores a los TextView
                            habilitado = documentSnapshot.getBoolean("habilitado");
                            TextView textViewNombre = findViewById(R.id.textViewNombre);
                            nombrecompleto = documentSnapshot.getString("nombre") + " " + documentSnapshot.getString("apellido");
                            TextView textViewDNI = findViewById(R.id.textViewDNI);
                            TextView textViewCorreo = findViewById(R.id.textViewCorreo);
                            TextView textViewTelefono = findViewById(R.id.textViewTelefono);
                            TextView textViewRol = findViewById(R.id.textViewRol);
                            TextView textViewRestaurante = findViewById(R.id.textViewRestaurante);
                            TextView textViewDireccion = findViewById(R.id.textViewDireccion);

                            textViewNombre.setText(nombrecompleto);
                            textViewDNI.setText(documentSnapshot.getString("dni"));
                            textViewCorreo.setText(documentSnapshot.getString("correo"));
                            textViewTelefono.setText(documentSnapshot.getString("telefono"));
                            textViewRol.setText("Administrador");
                            textViewRestaurante.setText(documentSnapshot.getString("restaurante"));
                            textViewDireccion.setText(documentSnapshot.getString("direccion"));
                            switchEstadoCuenta.setChecked(habilitado);

                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al cargar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
        if(rol.equals("Repartidores")) {
            db.collection("repartidores").document(UID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Asignar los valores a los TextView
                            habilitado = documentSnapshot.getBoolean("habilitado");
                            TextView textViewNombre = findViewById(R.id.textViewNombre);
                            nombrecompleto = documentSnapshot.getString("nombre") + " " + documentSnapshot.getString("apellido");
                            TextView textViewDNI = findViewById(R.id.textViewDNI);
                            TextView textViewCorreo = findViewById(R.id.textViewCorreo);
                            TextView textViewTelefono = findViewById(R.id.textViewTelefono);
                            TextView textViewRol = findViewById(R.id.textViewRol);
                            TextView textViewRestaurante = findViewById(R.id.textViewRestaurante);
                            TextView textViewDireccion = findViewById(R.id.textViewDireccion);

                            textViewNombre.setText(nombrecompleto);
                            textViewDNI.setText(documentSnapshot.getString("dni"));
                            textViewCorreo.setText(documentSnapshot.getString("correo"));
                            textViewTelefono.setText(documentSnapshot.getString("telefono"));
                            textViewRol.setText("Repartidor");
                            textViewRestaurante.setText("No asociado");
                            textViewDireccion.setText(documentSnapshot.getString("direccion"));
                            switchEstadoCuenta.setChecked(habilitado);

                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al cargar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
        if(rol.equals("Clientes")) {
            db.collection("clientes").document(UID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Asignar los valores a los TextView
                            habilitado = documentSnapshot.getBoolean("habilitado");
                            TextView textViewNombre = findViewById(R.id.textViewNombre);
                            nombrecompleto = documentSnapshot.getString("Nombre") + " " + documentSnapshot.getString("Apellido");
                            TextView textViewDNI = findViewById(R.id.textViewDNI);
                            TextView textViewCorreo = findViewById(R.id.textViewCorreo);
                            TextView textViewTelefono = findViewById(R.id.textViewTelefono);
                            TextView textViewRol = findViewById(R.id.textViewRol);
                            TextView textViewRestaurante = findViewById(R.id.textViewRestaurante);
                            TextView textViewDireccion = findViewById(R.id.textViewDireccion);

                            textViewNombre.setText(nombrecompleto);
                            textViewDNI.setText(documentSnapshot.getString("DNI"));
                            textViewCorreo.setText(documentSnapshot.getString("Email"));
                            textViewTelefono.setText(documentSnapshot.getString("Telefono"));
                            textViewRol.setText("Cliente");
                            textViewRestaurante.setText("No asociado");
                            textViewDireccion.setText(documentSnapshot.getString("Direccion"));
                            switchEstadoCuenta.setChecked(habilitado);

                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al cargar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

        switchEstadoCuenta.setOnCheckedChangeListener((buttonView, isChecked) -> {
            habilitado = isChecked; // Actualiza la variable local
            if(rol.equals("Administradores")) {
                db.collection("administradores").document(UID)
                        .update("habilitado", habilitado)
                        .addOnSuccessListener(unused -> {
                            String message = habilitado ? "Cuenta habilitada" : "Cuenta deshabilitada";
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            switchEstadoCuenta.setChecked(!isChecked);
                            Toast.makeText(this, "Error al actualizar estado", Toast.LENGTH_SHORT).show();
                        });
            }
            if(rol.equals("Repartidores")) {
                db.collection("repartidores").document(UID)
                        .update("habilitado", habilitado);
            }
            if(rol.equals("Clientes")) {
                db.collection("clientes").document(UID)
                        .update("habilitado", habilitado);
            }

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
                    intent = new Intent(info_usuario_superadmin.this, gestion_usuarios_superadmin.class);
                } else if (item.getItemId() == R.id.navigation_reportes) {
                    intent = new Intent(info_usuario_superadmin.this, gestion_reportes_superadmin.class);
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

}