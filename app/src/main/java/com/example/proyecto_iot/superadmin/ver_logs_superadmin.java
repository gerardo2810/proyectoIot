package com.example.proyecto_iot.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.superadmin.RecyclerView.LogSA;
import com.example.proyecto_iot.superadmin.RecyclerView.LogAdapterSA;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ver_logs_superadmin extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerViewLogs;
    private LogAdapterSA adapter;
    private List<LogSA> logsList;
    private FirebaseFirestore firestore;

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
        recyclerViewLogs = findViewById(R.id.recyclerViewLogsSA);
        recyclerViewLogs.setLayoutManager(new LinearLayoutManager(this));

        logsList = new ArrayList<>();
        adapter = new LogAdapterSA(logsList);
        recyclerViewLogs.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

        // Cargar los logs desde Firebase
        loadLogs();
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

    private void loadLogs() {
        firestore.collection("logs")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    logsList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        LogSA log = document.toObject(LogSA.class);
                        if (log != null) {
                            logsList.add(log);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar logs", Toast.LENGTH_SHORT).show();
                });
    }

}