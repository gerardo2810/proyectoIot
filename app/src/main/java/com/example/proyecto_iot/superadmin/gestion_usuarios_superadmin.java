package com.example.proyecto_iot.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class gestion_usuarios_superadmin extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_activity_gestion_usuarios);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_usuarios) {
                    Intent intentoUsuario = new Intent(gestion_usuarios_superadmin.this, gestion_usuarios_superadmin.class);
                    startActivity(intentoUsuario);
                    return true;
                } else if (item.getItemId() == R.id.navigation_reportes) {
                    Intent intentoReporte = new Intent(gestion_usuarios_superadmin.this, gestion_reportes_superadmin.class);
                    startActivity(intentoReporte);
                    return true;
                }
                return false;
            }
        });
    }
}