package com.example.proyecto_iot.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.example.proyecto_iot.superadmin.RecyclerView.RestauranteSA;
import com.example.proyecto_iot.superadmin.RecyclerView.UsuarioAdapterSA;
import com.example.proyecto_iot.superadmin.RecyclerView.UsuarioSA;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class reportes_recibidos_superadmin extends AppCompatActivity {

    private Spinner spinnerTipoReporte, spinnerRestaurante;
    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerViewReportesRecibidos;
    private ReporteAdapterSA adapter;
    private List<ReporteSA> listaReportesOriginal;
    private List<ReporteSA> listaReportesFiltrada;

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

        //Gestion del Recycler View
        recyclerViewReportesRecibidos = findViewById(R.id.recyclerViewReportesRecibidos);
        recyclerViewReportesRecibidos.setLayoutManager(new LinearLayoutManager(this));

        listaReportesOriginal = cargarReportes();
        listaReportesFiltrada = new ArrayList<>(listaReportesOriginal);

        adapter = new ReporteAdapterSA(listaReportesFiltrada);
        recyclerViewReportesRecibidos.setAdapter(adapter);
        //----------------------------------------------------------------------------

        //Gestion de spinner Tipo Reparto
        spinnerTipoReporte = findViewById(R.id.spinnerTipoReporte);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.superadmin_lista_tipoReporte, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoReporte.setAdapter(adapter1);

        spinnerTipoReporte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String rolSeleccionado = parent.getItemAtPosition(position).toString();
                Log.d("Spinner", "Rol seleccionado: " + rolSeleccionado); // Agregar este log
                filtrarUsuarios();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada si no hay nada seleccionado
            }
        });
        //----------------------------------------------------------------------------

        //Gestion de spinner Restaurante
        spinnerRestaurante = findViewById(R.id.spinnerRestaurante);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.superadmin_lista_restaurante, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRestaurante.setAdapter(adapter2);

        spinnerRestaurante.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String estadoSeleccionado = parent.getItemAtPosition(position).toString();
                Log.d("Spinner", "Estado seleccionado: " + estadoSeleccionado);
                filtrarUsuarios();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada si no hay nada seleccionado
            }
        });
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

    // Método para cargar restaurantes (simulado)
    private List<ReporteSA> cargarReportes() {

        List<ReporteSA> listaReportes = new ArrayList<>();

        listaReportes = new ArrayList<>();
        listaReportes.add(new ReporteSA("Papa Johns", "Ana Armas", "03 / 10 / 2024", "Por Plato"));
        listaReportes.add(new ReporteSA("Pizza Hut", "Benito Bueno", "02 / 10 / 2024", "Por Usuarios"));
        listaReportes.add(new ReporteSA("Pardos Chicken", "Ana Armas", "02 / 10 / 2024", "Por Plato"));
        listaReportes.add(new ReporteSA("KFC", "Benito Bueno", "01 / 10 / 2024", "Por Usuarios"));
        listaReportes.add(new ReporteSA("Astrid y Gaston", "Carlos Carrion", "30 / 09 / 2024", "Por Usuarios"));
        listaReportes.add(new ReporteSA("Papa Johns", "Ana Armas", "03 / 09 / 2024", "Por Plato"));
        listaReportes.add(new ReporteSA("Pizza Hut", "Benito Bueno", "02 / 09 / 2024", "Por Usuarios"));
        listaReportes.add(new ReporteSA("Pardos Chicken", "Ana Armas", "02 / 09 / 2024", "Por Plato"));
        listaReportes.add(new ReporteSA("KFC", "Benito Bueno", "01 / 09 / 2024", "Por Usuarios"));
        listaReportes.add(new ReporteSA("Astrid y Gaston", "Carlos Carrion", "30 / 08 / 2024", "Por Usuarios"));
        listaReportes.add(new ReporteSA("Papa Johns", "Ana Armas", "03 / 08 / 2024", "Por Usuarios"));
        listaReportes.add(new ReporteSA("Pizza Hut", "Benito Bueno", "02 / 08 / 2024", "Por Usuarios"));
        listaReportes.add(new ReporteSA("Pardos Chicken", "Ana Armas", "02 / 08 / 2024", "Por Usuarios"));
        listaReportes.add(new ReporteSA("KFC", "Benito Bueno", "01 / 08 / 2024", "Por Plato"));
        listaReportes.add(new ReporteSA("Astrid y Gaston", "Carlos Carrion", "30 / 07 / 2024", "Por Plato"));

        return listaReportes;
    }

    // Método para filtrar usuarios según el rol seleccionado
    private void filtrarUsuarios() {
        listaReportesFiltrada.clear();

        String tipoSeleccionado = spinnerTipoReporte.getSelectedItem().toString();
        String restauranteSeleccionado = spinnerRestaurante.getSelectedItem().toString();

        if (tipoSeleccionado.equals("-Seleccionar-") && restauranteSeleccionado.equals("-Seleccionar-")) {
            listaReportesFiltrada.addAll(listaReportesOriginal);
        } else if (!tipoSeleccionado.equals("-Seleccionar-") && restauranteSeleccionado.equals("-Seleccionar-")){
            if (tipoSeleccionado.equals("Por Plato")) {
                for (ReporteSA reporte : listaReportesOriginal) {
                    if (reporte.getTipo_reporte().equals("Por Plato")) {
                        listaReportesFiltrada.add(reporte);
                    }
                }
            } else if (tipoSeleccionado.equals("Por Usuarios")) {
                for (ReporteSA reporte : listaReportesOriginal) {
                    if (reporte.getTipo_reporte().equals("Por Usuarios")) {
                        listaReportesFiltrada.add(reporte);
                    }
                }
            }
        } else if (tipoSeleccionado.equals("-Seleccionar-") && !restauranteSeleccionado.equals("-Seleccionar-")){
            for (ReporteSA reporte : listaReportesOriginal) {
                if (reporte.getNombre_restaurante().equals(restauranteSeleccionado)) {
                    listaReportesFiltrada.add(reporte);
                }
            }
        } else if (!tipoSeleccionado.equals("-Seleccionar-") && !restauranteSeleccionado.equals("-Seleccionar-")){
            if (tipoSeleccionado.equals("Por Plato")) {
                for (ReporteSA reporte : listaReportesOriginal) {
                    if (reporte.getNombre_restaurante().equals(restauranteSeleccionado) && reporte.getTipo_reporte().equals("Por Plato")) {
                        listaReportesFiltrada.add(reporte);
                    }
                }
            } else if (tipoSeleccionado.equals("Por Usuarios")) {
                for (ReporteSA reporte : listaReportesOriginal) {
                    if (reporte.getNombre_restaurante().equals(restauranteSeleccionado) && reporte.getTipo_reporte().equals("Por Usuarios")) {
                        listaReportesFiltrada.add(reporte);
                    }
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

}