package com.example.proyecto_iot.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

        //Gestion de los cardviews
        for (int i = 1; i <= 4; i++) {
            int arrowIconId = getResources().getIdentifier("arrow_icon_" + i, "id", getPackageName());
            LinearLayout arrowIcon = findViewById(arrowIconId);
            final int finalI = i;

            arrowIcon.setOnClickListener(v -> {
                Intent intent = null;
                if (finalI == 1) {
                    intent = new Intent(gestion_usuarios_superadmin.this, lista_usuarios_superadmin.class);
                } else if (finalI == 2) {
                    intent = new Intent(gestion_usuarios_superadmin.this, registro_nuevoadmin_superadmin.class);
                } else if (finalI == 3) {
                    intent = new Intent(gestion_usuarios_superadmin.this, solicitudes_repartidores_superadmin.class);
                } else if (finalI == 4) {
                    intent = new Intent(gestion_usuarios_superadmin.this, ver_logs_superadmin.class);
                }

                if (intent != null) {
                    startActivity(intent);
                }
            });
        }
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
                    intent = new Intent(gestion_usuarios_superadmin.this, gestion_usuarios_superadmin.class);
                } else if (item.getItemId() == R.id.navigation_reportes) {
                    intent = new Intent(gestion_usuarios_superadmin.this, gestion_reportes_superadmin.class);
                } else if (item.getItemId() == R.id.navigation_perfil) {
                    intent = new Intent(gestion_usuarios_superadmin.this, perfil_superadmin.class);
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