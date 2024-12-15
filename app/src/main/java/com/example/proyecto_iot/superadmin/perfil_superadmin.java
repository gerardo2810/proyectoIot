package com.example.proyecto_iot.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.LoginActivity;
import com.example.proyecto_iot.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class perfil_superadmin extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_activity_perfil);

        // Obtener la instancia de FirebaseAuth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //Gestion de los cardviews
        LinearLayout personalInfoLayout = findViewById(R.id.edit_personal_info);
        LinearLayout logout = findViewById(R.id.logOutBttn);

        personalInfoLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, editar_perfil_superadmin.class);
            startActivity(intent);
        });

        logout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
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
                if (item.getItemId() == R.id.navigation_usuarios) {
                    intent = new Intent(perfil_superadmin.this, gestion_usuarios_superadmin.class);
                } else if (item.getItemId() == R.id.navigation_reportes) {
                    intent = new Intent(perfil_superadmin.this, gestion_reportes_superadmin.class);
                } else if (item.getItemId() == R.id.navigation_perfil) {
                    intent = new Intent(perfil_superadmin.this, perfil_superadmin.class);
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