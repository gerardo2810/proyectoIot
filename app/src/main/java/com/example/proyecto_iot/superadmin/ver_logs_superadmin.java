package com.example.proyecto_iot.superadmin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class ver_logs_superadmin extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerViewLogs;
    private LogAdapterSA adapter;
    private List<LogSA> logsList;
    private FirebaseFirestore firestore;
    private Spinner spinnerRol;
    private Button btnFecha;
    private Calendar fechaInicio, fechaFin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superadmin_activity_ver_logs);

        btnFecha = findViewById(R.id.btnFecha);
        btnFecha.setOnClickListener(v -> mostrarDialogoFecha());

        //Gestion de spinner Rol
        spinnerRol = findViewById(R.id.spinnerRol);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.superadmin_lista_roles_2, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(adapter1);

        spinnerRol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String rolSeleccionado = spinnerRol.getSelectedItem().toString();
                // Resetear fechas al cambiar el rol
                fechaInicio = null;
                fechaFin = null;
                btnFecha.setText("Seleccionar Fecha");

                // Cargar los logs filtrados por rol
                if (rolSeleccionado.equals("-Seleccionar-")) {
                    loadLogs(null, null, null);
                } else {
                    loadLogs(rolSeleccionado, null, null);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        //----------------------------------------------------------------------------

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
        loadLogs(null, null, null);
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

    private void loadLogs(String rol, Calendar fechaInicio, Calendar fechaFin) {
        firestore.collection("logs")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    logsList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        LogSA log = document.toObject(LogSA.class);
                        if (log != null) {
                            boolean agregarLog = true;

                            // Filtrar por rol
                            if (rol != null && !rol.equals(log.getRol())) {
                                agregarLog = false;
                            }

                            // Filtrar por rango de fechas
                            if (fechaInicio != null && fechaFin != null) {
                                String[] fechaLogSplit = log.getFecha().split("-");
                                Calendar fechaLog = Calendar.getInstance();
                                fechaLog.set(
                                        Integer.parseInt(fechaLogSplit[0]),
                                        Integer.parseInt(fechaLogSplit[1]) - 1,
                                        Integer.parseInt(fechaLogSplit[2])
                                );

                                if (fechaLog.before(fechaInicio) || fechaLog.after(fechaFin)) {
                                    agregarLog = false;
                                }
                            }

                            if (agregarLog) {
                                logsList.add(log);
                            }
                        }
                    }

                    // Ordenar logs por fecha y hora de manera descendente
                    Collections.sort(logsList, (log1, log2) -> {
                        String datetime1 = log1.getFecha() + " " + log1.getHora();
                        String datetime2 = log2.getFecha() + " " + log2.getHora();
                        return datetime2.compareTo(datetime1); // Orden descendente
                    });

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar logs", Toast.LENGTH_SHORT).show();
                });
    }

    private void mostrarDialogoFecha() {
        Calendar calendario = Calendar.getInstance();

        DatePickerDialog datePickerInicio = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    fechaInicio = Calendar.getInstance();
                    fechaInicio.set(year, month, dayOfMonth);

                    mostrarDialogoFechaFin();
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
        );
        datePickerInicio.setTitle("Seleccionar Fecha de Inicio");
        datePickerInicio.show();
    }

    private void mostrarDialogoFechaFin() {
        Calendar calendario = Calendar.getInstance();

        DatePickerDialog datePickerFin = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    fechaFin = Calendar.getInstance();
                    fechaFin.set(year, month, dayOfMonth);

                    btnFecha.setText(
                            "Inicio: " + fechaInicio.get(Calendar.DAY_OF_MONTH) + "/"
                                    + (fechaInicio.get(Calendar.MONTH) + 1) + "/"
                                    + fechaInicio.get(Calendar.YEAR) + "\n" +
                                    "Fin: " + fechaFin.get(Calendar.DAY_OF_MONTH) + "/"
                                    + (fechaFin.get(Calendar.MONTH) + 1) + "/"
                                    + fechaFin.get(Calendar.YEAR)
                    );

                    // Obtener rol seleccionado
                    String rolSeleccionado = spinnerRol.getSelectedItem().toString();
                    if (rolSeleccionado.equals("-Seleccionar-")) {
                        rolSeleccionado = null; // Mostrar todos los roles
                    }

                    // Cargar logs filtrados por rol y fechas
                    loadLogs(rolSeleccionado, fechaInicio, fechaFin);
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
        );
        datePickerFin.setTitle("Seleccionar Fecha de Fin");
        datePickerFin.show();
    }

}