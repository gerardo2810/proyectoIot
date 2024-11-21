package com.example.proyecto_iot.repartidor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.repartidor.RecyclerView.PedidoRecoger;
import com.example.proyecto_iot.repartidor.RecyclerView.PedidosRecogerAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InicioRepartidorActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerViewListaPedidosRecoger;
    private List<PedidoRecoger> listaPedidos;
    private PedidosRecogerAdapter adapter;
    TextView textViewUbicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_repartidor);

        textViewUbicacion = findViewById(R.id.textViewUbicacion);

        mostrarUbicacion();

        Button volverBtn = findViewById(R.id.buttonRegresar);
        volverBtn.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            String ultimaVista = prefs.getString("ultima_vista", "InicioRepartidorActivity"); // Por defecto, si no hay nada guardado, ir a activityInicio.

            // Redirigir a la última vista donde se quedó el usuario
            try {
                Class<?> clase = Class.forName("com.example.proyecto_iot.repartidor." + ultimaVista); // Asegúrate de que el paquete sea correcto
                Intent intent = new Intent(InicioRepartidorActivity.this, clase);
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        //----------------------------------------------------------------------------
        Intent intent = getIntent();
        if (intent.getBooleanExtra("showDialog", false)) {
            mostrarAlerta();
        }

        //Gestion de la bottom navigation bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        int selectedItemId = getIntent().getIntExtra("SELECTED_ITEM_ID", R.id.navigation_home);
        bottomNavigationView.setSelectedItemId(selectedItemId);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                if (item.getItemId() == R.id.navigation_home) {
                    intent = new Intent(InicioRepartidorActivity.this, InicioRepartidorActivity.class);
                } else if (item.getItemId() == R.id.navigation_historial) {
                    intent = new Intent(InicioRepartidorActivity.this, HistorialRepartidorActivity.class);
                }else if (item.getItemId() == R.id.navigation_perfil) {
                    intent = new Intent(InicioRepartidorActivity.this, PerfilRepartidorActivity.class);
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
        recyclerViewListaPedidosRecoger = findViewById(R.id.recyclerViewListaPedidosRecoger);
        recyclerViewListaPedidosRecoger.setLayoutManager(new LinearLayoutManager(this));
        listaPedidos = new ArrayList<>();
        adapter = new PedidosRecogerAdapter(this,listaPedidos);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        recyclerViewListaPedidosRecoger.setAdapter(adapter);
        db.collection("pedidos")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.d("msg-test", "Error al obtener documentos: ", e);
                        return;
                    }

                    listaPedidos.clear();
                    for (QueryDocumentSnapshot document : querySnapshot) {

                        PedidoRecoger pedidoRecoger = document.toObject(PedidoRecoger.class);
                        listaPedidos.add(pedidoRecoger);
                    }
                    adapter.notifyDataSetChanged();

                });

    }

    public void mostrarAlerta(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Registro Exitoso");
        alertDialog.setMessage("¡Se registró correctamente el pedido!");
        alertDialog.setPositiveButton("Cerrar",
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("msgAlerta","Positive");
                    }
                });
        alertDialog.show();
    }

    public void mostrarUbicacion() {

        int selfPermissionFineLocation = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int selfPermissionCoarseLocation = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);

        if (selfPermissionFineLocation == PackageManager.PERMISSION_GRANTED &&
                selfPermissionCoarseLocation == PackageManager.PERMISSION_GRANTED) {

            //tenemos permisos
            FusedLocationProviderClient providerClient = LocationServices.getFusedLocationProviderClient(this);
            providerClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    double latitud = location.getLatitude();
                    double longitud = location.getLongitude();
                    obtenerDireccion(latitud, longitud);
                }
                else{
                    textViewUbicacion.setText("Ubicación no disponible");
                }
            });

        } else {
            //no tenemos permisos, se deben solicitar
            locationPermissionLauncher.launch(new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
            });

        }

    }

    ActivityResultLauncher<String[]> locationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                Boolean fineLocationGranted = result.get(android.Manifest.permission.ACCESS_FINE_LOCATION);
                Boolean coarseLocationGranted = result.get(android.Manifest.permission.ACCESS_COARSE_LOCATION);
                if (fineLocationGranted != null && fineLocationGranted) {
                    Log.d("msg-test-locationPermissionLauncher", "Permiso de ubicación precisa concedido");
                    mostrarUbicacion();
                } else if (coarseLocationGranted != null && coarseLocationGranted) {
                    Log.d("msg-test-locationPermissionLauncher", "Permiso de ubicación aproximada concedido");
                } else {
                    Log.d("msg-test-locationPermissionLauncher", "Ningún permiso concedido");
                }
            }
    );

    private void obtenerDireccion(double latitud, double longitud) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            // Obtiene una lista de direcciones cercanas
            List<Address> direcciones = geocoder.getFromLocation(latitud, longitud, 1);
            if (direcciones != null && !direcciones.isEmpty()) {
                Address direccion = direcciones.get(0);

                // Construye la dirección
                String direccionCompleta = direccion.getAddressLine(0);
                textViewUbicacion.setText(direccionCompleta); // Muestra la dirección
            } else {
                textViewUbicacion.setText("Dirección no disponible");
            }
        } catch (IOException e) {
            e.printStackTrace();
            textViewUbicacion.setText("Error al obtener la dirección");
        }
    }
}
