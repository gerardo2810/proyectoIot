package com.example.proyecto_iot.repartidor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.proyecto_iot.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class EntregaCurso1Activity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;
    FusedLocationProviderClient fusedLocationClient;
    LocationCallback locationCallback;
    Marker marcadorActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrega_curso_1);

        // Obtén el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Inicializa el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Button boton1 = findViewById(R.id.button3);
        boton1.setOnClickListener(v -> {

            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("ultima_vista", "EntregaCurso2Activity");
            editor.apply();


            Intent intent = new Intent(this, EntregaCurso2Activity.class);
            startActivity(intent);
        });

    }

    @SuppressWarnings("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, InicioRepartidorActivity.class); // Regresar a la vistaInicio
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia la pila de actividades
        startActivity(intent);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        myMap = googleMap;

        LatLng destino = new LatLng(-12.071507, -77.090343); // Coordenadas de punto de entrega

        Bitmap original1 = BitmapFactory.decodeResource(getResources(), R.drawable.home_address_location);
        Bitmap resized1 = Bitmap.createScaledBitmap(original1, 150, 150, false);
        myMap.addMarker(new MarkerOptions()
                .position(destino)
                .title("Punto de entrega")
                .icon(BitmapDescriptorFactory.fromBitmap(resized1)));

        // Ajusta la cámara
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(destino);
        myMap.setOnMyLocationChangeListener(location -> {
            LatLng nuevaUbicacion = new LatLng(location.getLatitude(), location.getLongitude());
            builder.include(nuevaUbicacion);
            myMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
        });

        // Configura actualizaciones en tiempo real de tu ubicación
        configurarActualizacionesUbicacion(destino);

    }

    public void configurarActualizacionesUbicacion(LatLng destino) {

        int selfPermissionFineLocation = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int selfPermissionCoarseLocation = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);


        if (selfPermissionFineLocation == PackageManager.PERMISSION_GRANTED &&
                selfPermissionCoarseLocation == PackageManager.PERMISSION_GRANTED) {
            // Configura la solicitud de actualizaciones de ubicación
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(5000); // Actualización cada 5 segundos

            // Configura el callback para actualizaciones de ubicación
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    if (locationResult != null) {
                        for (Location location : locationResult.getLocations()) {
                            actualizarUbicacionEnMapa(location, destino);
                        }
                    }
                }
            };

            // Inicia las actualizaciones de ubicación
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            Toast.makeText(this, "Permisos de ubicación no otorgados", Toast.LENGTH_SHORT).show();
        }
    }

    public void actualizarUbicacionEnMapa(Location location, LatLng destino) {
        LatLng nuevaUbicacion = new LatLng(location.getLatitude(), location.getLongitude());

        if (marcadorActual == null) {
            // Crea el marcador inicial si no existe
            Bitmap original = BitmapFactory.decodeResource(getResources(), R.drawable.delivery_bike_map);
            Bitmap resized = Bitmap.createScaledBitmap(original, 150, 150, false);
            marcadorActual = myMap.addMarker(new MarkerOptions()
                    .position(nuevaUbicacion)
                    .title("Tu ubicación")
                    .icon(BitmapDescriptorFactory.fromBitmap(resized)));
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nuevaUbicacion, 15));
        } else {
            // Actualiza la posición del marcador existente
            marcadorActual.setPosition(nuevaUbicacion);

            // Ajusta la cámara para que incluya tanto tu posición actual como el destino
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(destino);
            builder.include(nuevaUbicacion);
            myMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
        }
    }


}
