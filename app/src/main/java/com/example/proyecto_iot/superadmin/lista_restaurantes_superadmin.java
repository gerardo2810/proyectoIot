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
import com.example.proyecto_iot.superadmin.RecyclerView.LogAdapterSA;
import com.example.proyecto_iot.superadmin.RecyclerView.LogSA;
import com.example.proyecto_iot.superadmin.RecyclerView.RestauranteAdapterSA;
import com.example.proyecto_iot.superadmin.RecyclerView.RestauranteSA;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class lista_restaurantes_superadmin extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerViewRestaurantesReportes;
    private RestauranteAdapterSA adapter;
    private List<RestauranteSA> listaRestaurante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_activity_lista_restaurantes);

        //Volver una pantalla atras
        ImageView arrowIcon = findViewById(R.id.arrow_back_icon);
        arrowIcon.setOnClickListener(v -> {
            Intent intent = new Intent(lista_restaurantes_superadmin.this, gestion_usuarios_superadmin.class);
            startActivity(intent);
        });
        //----------------------------------------------------------------------------

        //Gestion del Recycler View
        recyclerViewRestaurantesReportes = findViewById(R.id.recyclerViewListaRestaurantesSA);
        recyclerViewRestaurantesReportes.setLayoutManager(new LinearLayoutManager(this));

        listaRestaurante = new ArrayList<>();
        listaRestaurante.add(new RestauranteSA("Huaca Pucllana"));
        listaRestaurante.add(new RestauranteSA("Cala"));
        listaRestaurante.add(new RestauranteSA("Costanera 700"));
        listaRestaurante.add(new RestauranteSA("El Mercado"));
        listaRestaurante.add(new RestauranteSA("Malabar"));
        listaRestaurante.add(new RestauranteSA("Mayta"));
        listaRestaurante.add(new RestauranteSA("Amoramar"));
        listaRestaurante.add(new RestauranteSA("La Picantería"));
        listaRestaurante.add(new RestauranteSA("Siete Sopas"));
        listaRestaurante.add(new RestauranteSA("Segundo Muelle"));

        adapter = new RestauranteAdapterSA(listaRestaurante);
        recyclerViewRestaurantesReportes.setAdapter(adapter);
        //----------------------------------------------------------------------------

        //Gestion de la bottom navigation bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        int selectedItemId = getIntent().getIntExtra("SELECTED_ITEM_ID", R.id.navigation_reportes);
        bottomNavigationView.setSelectedItemId(selectedItemId);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                if (item.getItemId() == R.id.navigation_usuarios) {
                    intent = new Intent(lista_restaurantes_superadmin.this, gestion_usuarios_superadmin.class);
                } else if (item.getItemId() == R.id.navigation_reportes) {
                    intent = new Intent(lista_restaurantes_superadmin.this, gestion_reportes_superadmin.class);
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