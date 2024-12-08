package com.example.proyecto_iot.repartidor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.LoginActivity;
import com.example.proyecto_iot.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class NuevoPedidoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_pedido);

        TextView btnCancelar = findViewById(R.id.tvCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Regresa a inicioRepartidorActivity
                finish();
            }
        });

        // Obtén el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button boton1 = findViewById(R.id.button2);
        boton1.setOnClickListener(v -> {

            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("ultima_vista", "RecojoCurso1Activity");
            editor.apply();


            Intent intent = new Intent(this, RecojoCurso1Activity.class);
            startActivity(intent);
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        myMap = googleMap;

        // Define las ubicaciones de origen y destino
        LatLng origen = new LatLng(-12.0682373, -77.0769483); // Coordenadas de punto de retiro
        LatLng destino = new LatLng(-12.071507, -77.090343); // Coordenadas de punto de entrega

        // Agrega marcadores

        Bitmap original = BitmapFactory.decodeResource(getResources(), R.drawable.shop_location_map);
        Bitmap resized = Bitmap.createScaledBitmap(original, 150, 150, false);
        myMap.addMarker(new MarkerOptions()
                .position(origen)
                .title("Punto de retiro")
                .icon(BitmapDescriptorFactory.fromBitmap(resized)));

        Bitmap original1 = BitmapFactory.decodeResource(getResources(), R.drawable.home_address_location);
        Bitmap resized1 = Bitmap.createScaledBitmap(original1, 150, 150, false);
        myMap.addMarker(new MarkerOptions()
                .position(destino)
                .title("Punto de entrega")
                .icon(BitmapDescriptorFactory.fromBitmap(resized1)));

        // Ajusta la cámara
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(origen);
        builder.include(destino);
        LatLngBounds bounds = builder.build();

        myMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

    }

}
