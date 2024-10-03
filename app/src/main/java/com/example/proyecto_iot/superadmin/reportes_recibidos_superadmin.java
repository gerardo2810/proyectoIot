package com.example.proyecto_iot.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.superadmin.RecyclerView.ReporteAdapterSA;
import com.example.proyecto_iot.superadmin.RecyclerView.ReporteSA;
import com.example.proyecto_iot.superadmin.RecyclerView.UsuarioAdapterSA;
import com.example.proyecto_iot.superadmin.RecyclerView.UsuarioSA;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class reportes_recibidos_superadmin extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerViewReportesRecibidos;
    private ReporteAdapterSA adapter;
    private List<ReporteSA> listaReportes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_activity_reportes_recibidos);

        //Volver una pantalla atras
        ImageView arrowIcon = findViewById(R.id.arrow_back_icon);
        arrowIcon.setOnClickListener(v -> {
            Intent intent = new Intent(reportes_recibidos_superadmin.this, gestion_reportes_superadmin.class);
            intent.putExtra("SELECTED_ITEM_ID", R.id.navigation_reportes);
            startActivity(intent);
        });
        //----------------------------------------------------------------------------

        //Gestion de spinners
        Spinner spinner1 = findViewById(R.id.spinnerTipoReporte);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.superadmin_lista_tipoReporte, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);

        Spinner spinner2 = findViewById(R.id.spinnerRestaurante);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.superadmin_lista_restaurante, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        //----------------------------------------------------------------------------

        //Gestion del Recycler View
        recyclerViewReportesRecibidos = findViewById(R.id.recyclerViewReportesRecibidos);
        recyclerViewReportesRecibidos.setLayoutManager(new LinearLayoutManager(this));

        listaReportes = new ArrayList<>();
        listaReportes.add(new ReporteSA("Papa Johns", "Ana Armas", "03 / 10 / 2024"));
        listaReportes.add(new ReporteSA("Pardos Chickens", "Benito Bueno", "02 / 10 / 2024"));
        listaReportes.add(new ReporteSA("Central", "Ana Armas", "02 / 10 / 2024"));
        listaReportes.add(new ReporteSA("Maido", "Benito Bueno", "01 / 10 / 2024"));
        listaReportes.add(new ReporteSA("Astrid y Gastón", "Carlos Carrion", "30 / 09 / 2024"));
        listaReportes.add(new ReporteSA("Rafael", "Daniela Delgado", "29 / 09 / 2024"));
        listaReportes.add(new ReporteSA("La Mar", "Eduardo Esquivel", "28 / 09 / 2024"));
        listaReportes.add(new ReporteSA("Isolina", "Francisco Fernandez", "27 / 09 / 2024"));
        listaReportes.add(new ReporteSA("Panchita", "Gabriela Garcia", "26 / 09 / 2024"));
        listaReportes.add(new ReporteSA("Tanta", "Hector Hidalgo", "25 / 09 / 2024"));
        listaReportes.add(new ReporteSA("Osso", "Irene Iglesias", "24 / 09 / 2024"));
        listaReportes.add(new ReporteSA("Fiesta", "Jorge Juarez", "23 / 09 / 2024"));

        adapter = new ReporteAdapterSA(listaReportes);
        recyclerViewReportesRecibidos.setAdapter(adapter);
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
                    intent = new Intent(reportes_recibidos_superadmin.this, gestion_usuarios_superadmin.class);
                } else if (item.getItemId() == R.id.navigation_reportes) {
                    intent = new Intent(reportes_recibidos_superadmin.this, gestion_reportes_superadmin.class);
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