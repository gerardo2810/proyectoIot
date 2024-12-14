package com.example.proyecto_iot.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.superadmin.RecyclerView.RestReportAdapter;
import com.example.proyecto_iot.superadmin.RecyclerView.RestauranteAdapterSA;
import com.example.proyecto_iot.superadmin.RecyclerView.RestauranteReporte;
import com.example.proyecto_iot.superadmin.RecyclerView.RestauranteSA;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class gestion_reportes_superadmin extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private RestReportAdapter adapter;
    private List<RestauranteReporte> restaurantes;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_activity_gestion_reportes);

        //Gestion del recycler
        recyclerView = findViewById(R.id.recyclerViewListaRestaurantesSA);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        restaurantes = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        adapter = new RestReportAdapter(restaurantes, restauranteUID -> {
            Intent intent = new Intent(this, reporte_restaurante_superadmin.class);
            intent.putExtra("restauranteUID", restauranteUID);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        cargarRestaurantes();
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
                    intent = new Intent(gestion_reportes_superadmin.this, gestion_usuarios_superadmin.class);
                } else if (item.getItemId() == R.id.navigation_reportes) {
                    intent = new Intent(gestion_reportes_superadmin.this, gestion_reportes_superadmin.class);
                } else if (item.getItemId() == R.id.navigation_perfil) {
                    intent = new Intent(gestion_reportes_superadmin.this, perfil_superadmin.class);
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

    private void cargarRestaurantes() {
        db.collection("restaurantes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        RestauranteReporte restaurante = new RestauranteReporte();
                        restaurante.setNombre(document.getString("nombre"));
                        restaurante.setIdAdministrador(document.getString("idAdministrador"));
                        restaurante.setUid(document.getId());
                        restaurante.setFoto(document.getString("fotoLogo"));
                        restaurantes.add(restaurante);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar restaurantes", Toast.LENGTH_SHORT).show());
    }

}