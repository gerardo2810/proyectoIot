package com.example.proyecto_iot.repartidor;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistorialRepartidorActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerViewListaGanancias;
    List<GananciaxDia> listaGananciasDia;
    GananciasDiaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_repartidor);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String repartidorId = auth.getCurrentUser().getUid(); // Obtener UID del repartidor


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
        adapter = new GananciasDiaAdapter(this,listaGananciasDia);
        recyclerViewListaGanancias.setAdapter(adapter);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("historialPedidos")
                .whereEqualTo("idRepartidor", repartidorId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaGananciasDia.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String fecha = document.getString("fecha");
                        String nombreRestaurante = document.getString("nombreRestaurante");

                        GananciaxDia gananciaxDia = new GananciaxDia();
                        gananciaxDia.setFecha(fecha);
                        gananciaxDia.setNombreRestaurante(nombreRestaurante);
                        listaGananciasDia.add(gananciaxDia);
                    }
                    adapter.notifyDataSetChanged();
                });

    }
}
