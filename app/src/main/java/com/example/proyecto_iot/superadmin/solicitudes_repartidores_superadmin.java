package com.example.proyecto_iot.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class solicitudes_repartidores_superadmin extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerViewSolicitudesRepartidores;
    private RepartidorAdapterSA adapter;
    private List<RepartidorSA> repartidorList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_activity_solicitudes_repartidores);

        db = FirebaseFirestore.getInstance();

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

        adapter = new RepartidorAdapterSA(this, repartidorList);
        recyclerViewSolicitudesRepartidores.setAdapter(adapter);

        cargarRepartidores();
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

    private void cargarRepartidores() {
        db.collection("repartidores")
                .whereEqualTo("aceptado", false) // Filtrar los documentos con "aceptado" en false
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Limpiar la lista de repartidores
                        repartidorList.clear();

                        // Iterar sobre los documentos obtenidos
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            RepartidorSA repartidor = document.toObject(RepartidorSA.class);
                            repartidor.setId(document.getId()); // Asignar el ID del documento
                            if(document.getBoolean("habilitado")){
                                repartidorList.add(repartidor);
                            }
                        }

                        // Notificar al adaptador que los datos han cambiado
                        adapter.notifyDataSetChanged();
                    } else {
                        // Manejar errores si la consulta falla
                        Log.e("Firestore", "Error al obtener documentos: " + task.getException());
                    }
                });
    }

}