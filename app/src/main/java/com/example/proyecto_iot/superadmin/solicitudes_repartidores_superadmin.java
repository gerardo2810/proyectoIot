package com.example.proyecto_iot.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.superadmin.RecyclerView.RepartidorSA;
import com.example.proyecto_iot.superadmin.RecyclerView.RepartidorAdapterSA;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class solicitudes_repartidores_superadmin extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerViewSolicitudesRepartidores;
    private RepartidorAdapterSA adapter;
    private List<RepartidorSA> listaSolicitudes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_activity_solicitudes_repartidores);

        //Volver una pantalla atras
        ImageView arrowIcon = findViewById(R.id.arrow_back_icon);
        arrowIcon.setOnClickListener(v -> {
            Intent intent = new Intent(solicitudes_repartidores_superadmin.this, gestion_usuarios_superadmin.class);
            startActivity(intent);
        });
        //----------------------------------------------------------------------------

        //Gestion del Recycler View
        recyclerViewSolicitudesRepartidores = findViewById(R.id.recyclerViewListaRepartidoresSA);
        recyclerViewSolicitudesRepartidores.setLayoutManager(new LinearLayoutManager(this));

        listaSolicitudes = new ArrayList<>();
        listaSolicitudes.add(new RepartidorSA("Ana", "Armas", "02 / 10 / 2024"));
        listaSolicitudes.add(new RepartidorSA("Benito", "Bueno", "02 / 10 / 2024"));
        listaSolicitudes.add(new RepartidorSA("Carlos", "Carrion", "01 / 10 / 2024"));
        listaSolicitudes.add(new RepartidorSA("Daniela", "Delgado", "01 / 10 / 2024"));
        listaSolicitudes.add(new RepartidorSA("Eduardo", "Esquivel", "01 / 10 / 2024"));
        listaSolicitudes.add(new RepartidorSA("Francisco", "Fernandez", "30 / 09 / 2024"));
        listaSolicitudes.add(new RepartidorSA("Gabriela", "Garcia", "29 / 09 / 2024"));
        listaSolicitudes.add(new RepartidorSA("Hector", "Hidalgo", "29 / 09 / 2024"));
        listaSolicitudes.add(new RepartidorSA("Irene", "Iglesias", "29 / 09 / 2024"));
        listaSolicitudes.add(new RepartidorSA("Jorge", "Juarez", "28 / 09 / 2024"));

        adapter = new RepartidorAdapterSA(listaSolicitudes);
        recyclerViewSolicitudesRepartidores.setAdapter(adapter);
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
                    intent = new Intent(solicitudes_repartidores_superadmin.this, gestion_usuarios_superadmin.class);
                } else if (item.getItemId() == R.id.navigation_reportes) {
                    intent = new Intent(solicitudes_repartidores_superadmin.this, gestion_reportes_superadmin.class);
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