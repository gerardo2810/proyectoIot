package com.example.proyecto_iot.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.superadmin.RecyclerView.LogSA;
import com.example.proyecto_iot.superadmin.RecyclerView.RepartidorSA;
import com.example.proyecto_iot.superadmin.RecyclerView.SolicitudesRepartidoresAdapter;
import com.example.proyecto_iot.superadmin.RecyclerView.VerLogsAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class ver_logs_superadmin extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerViewVerLogs;
    private VerLogsAdapter adapter;
    private List<LogSA> listaLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_activity_ver_logs);

        //Volver una pantalla atras
        ImageView arrowIcon = findViewById(R.id.arrow_back_icon);
        arrowIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ver_logs_superadmin.this, gestion_usuarios_superadmin.class);
            startActivity(intent);
        });
        //----------------------------------------------------------------------------

        //Gestion del Recycler View
        recyclerViewVerLogs = findViewById(R.id.recyclerViewLogsSA);
        recyclerViewVerLogs.setLayoutManager(new LinearLayoutManager(this));

        listaLogs = new ArrayList<>();
        listaLogs.add(new LogSA("El administrador Ana Armas ha cambiado el nombre de su restaurante 'Pardos Chicken' a 'Marlos Chicken' "));
        listaLogs.add(new LogSA("El repartidor Benito Bueno ha finalizado y recibido su pago por la entrega N°1993321 "));

        adapter = new VerLogsAdapter(listaLogs);
        recyclerViewVerLogs.setAdapter(adapter);
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
                    intent = new Intent(ver_logs_superadmin.this, gestion_usuarios_superadmin.class);
                } else if (item.getItemId() == R.id.navigation_reportes) {
                    intent = new Intent(ver_logs_superadmin.this, gestion_reportes_superadmin.class);
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