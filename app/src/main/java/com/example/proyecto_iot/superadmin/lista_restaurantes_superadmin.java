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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class lista_restaurantes_superadmin extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerViewRestaurantesReportes;
    private RestauranteAdapterSA adapter;

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

        List<RestauranteSA> listaRestaurante = new ArrayList<>();
        adapter = new RestauranteAdapterSA(listaRestaurante);
        recyclerViewRestaurantesReportes.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference restaurantesRef = db.collection("restaurantes");
        // Obtener los datos
        restaurantesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                listaRestaurante.clear(); // Limpiar lista antes de agregar elementos
                for (QueryDocumentSnapshot document : task.getResult()) {
                    RestauranteSA restaurante = document.toObject(RestauranteSA.class);
                    listaRestaurante.add(restaurante);
                }
                adapter.notifyDataSetChanged(); // Notificar al adaptador sobre los cambios
            } else {
                // Manejar el error si la tarea no fue exitosa
            }
        });
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