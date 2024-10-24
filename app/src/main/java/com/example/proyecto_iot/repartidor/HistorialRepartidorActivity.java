package com.example.proyecto_iot.repartidor;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.repartidor.RecyclerView.GananciasDiaAdapter;
import com.example.proyecto_iot.repartidor.RecyclerView.GananciaxDia;
import com.example.proyecto_iot.repartidor.RecyclerView.PedidoRecoger;
import com.example.proyecto_iot.repartidor.RecyclerView.PedidosRecogerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class HistorialRepartidorActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerViewListaGanancias;
    private List<GananciaxDia> listaGananciasDia;
    private GananciasDiaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_repartidor);

        //Gestion de la bottom navigation bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        int selectedItemId = getIntent().getIntExtra("SELECTED_ITEM_ID", R.id.navigation_home);
        bottomNavigationView.setSelectedItemId(selectedItemId);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                if (item.getItemId() == R.id.navigation_home) {
                    intent = new Intent(HistorialRepartidorActivity.this, InicioRepartidorActivity.class);
                } else if (item.getItemId() == R.id.navigation_historial) {
                    intent = new Intent(HistorialRepartidorActivity.this, HistorialRepartidorActivity.class);
                }else if (item.getItemId() == R.id.navigation_perfil) {
                    intent = new Intent(HistorialRepartidorActivity.this, PerfilRepartidorActivity.class);
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
        //Gestion del Recycler View
        recyclerViewListaGanancias = findViewById(R.id.recyclerViewListaGanacias);
        recyclerViewListaGanancias.setLayoutManager(new LinearLayoutManager(this));
        listaGananciasDia = new ArrayList<>();
        listaGananciasDia.add(new GananciaxDia("Fecha: 20/06/2024","Restaurante: El Tío Bigote","Ganancia: S/. 10.00","S/. 10.00"));
        listaGananciasDia.add(new GananciaxDia("Fecha: 19/06/2024","Restaurante: El Tío Bigote","Ganancia: S/. 10.00","S/. 10.00"));
        listaGananciasDia.add(new GananciaxDia("Fecha: 18/06/2024","Restaurante: Pizza Party","Ganancia: S/. 10.00","S/. 10.00"));
        listaGananciasDia.add(new GananciaxDia("Fecha: 17/06/2024","Restaurante: Pizza Party","Ganancia: S/. 10.00","S/. 10.00"));
        listaGananciasDia.add(new GananciaxDia("Fecha: 16/06/2024","Restaurante: Miguelón","Ganancia: S/. 10.00","S/. 10.00"));
        listaGananciasDia.add(new GananciaxDia("Fecha: 15/06/2024","Restaurante: Miguelón","Ganancia: S/. 10.00","S/. 10.00"));

        adapter = new GananciasDiaAdapter(listaGananciasDia);
        recyclerViewListaGanancias.setAdapter(adapter);

    }
}
