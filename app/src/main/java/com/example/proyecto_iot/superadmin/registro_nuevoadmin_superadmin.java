package com.example.proyecto_iot.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class registro_nuevoadmin_superadmin extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Spinner spinnerTipoAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_activity_registro_admin);

        //Volver una pantalla atras
        ImageView arrowIcon = findViewById(R.id.arrow_back_icon);
        arrowIcon.setOnClickListener(v -> {
            Intent intent = new Intent(registro_nuevoadmin_superadmin.this, gestion_usuarios_superadmin.class);
            startActivity(intent);
        });
        //----------------------------------------------------------------------------

        //Gestion del Formulario
        spinnerTipoAdmin = findViewById(R.id.spinnerTipoAdmin);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.superadmin_lista_tipoRegistro, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoAdmin.setAdapter(adapter);

        Button btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(v -> registrarAdministrador());
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
                    intent = new Intent(registro_nuevoadmin_superadmin.this, gestion_usuarios_superadmin.class);
                } else if (item.getItemId() == R.id.navigation_reportes) {
                    intent = new Intent(registro_nuevoadmin_superadmin.this, gestion_reportes_superadmin.class);
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

    private void registrarAdministrador() {
        String tipoSeleccionado = spinnerTipoAdmin.getSelectedItem().toString();

        if (tipoSeleccionado.equals("-Seleccionar-")) {
            Toast.makeText(this, "Debe seleccionar una opción", Toast.LENGTH_SHORT).show();
        } else if (tipoSeleccionado.equals("Existente")) {
            mostrarDialogRestaurante();
        } else if (tipoSeleccionado.equals("Nuevo")) {
            Intent intent = new Intent(registro_nuevoadmin_superadmin.this, gestion_reportes_superadmin.class);
            startActivity(intent);
        }
    }

    private void mostrarDialogRestaurante() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Elija el restaurante al que pertenecerá el administrador:");

        // Crear el spinner para el diálogo
        Spinner spinnerRestaurante = new Spinner(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.superadmin_lista_restaurante, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRestaurante.setAdapter(adapter);

        // Agregar el spinner al diálogo
        builder.setView(spinnerRestaurante);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String restauranteSeleccionado = spinnerRestaurante.getSelectedItem().toString();
            if (restauranteSeleccionado.equals("-Seleccionar-")) {
                Toast.makeText(this, "Debe elegir un restaurante", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Administrador creado con éxito para " + restauranteSeleccionado, Toast.LENGTH_SHORT).show();
                // Redirigir a otra actividad
                Intent intent = new Intent(registro_nuevoadmin_superadmin.this, gestion_usuarios_superadmin.class); // Cambia "OtraActividad" por tu actividad deseada
                startActivity(intent);
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}