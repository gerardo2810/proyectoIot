package com.example.proyecto_iot.repartidor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NuevoPedidoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_pedido);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String idPedido = getIntent().getStringExtra("idPedido");

        db.collection("pedidos").document(idPedido)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String idRestaurante = documentSnapshot.getString("idRestaurante");
                        String direccionCliente = documentSnapshot.getString("direccion");

                        TextView textViewDireccion = findViewById(R.id.tvdireccion);
                        textViewDireccion.setText(direccionCliente);

                        // Ahora consulta los datos del restaurante
                        db.collection("restaurantes").document(idRestaurante)
                                .get()
                                .addOnSuccessListener(restauranteSnapshot -> {
                                    if (restauranteSnapshot.exists()) {
                                        String nombreRestaurante = restauranteSnapshot.getString("nombre");
                                        String direccionRestaurante = restauranteSnapshot.getString("ubicacion");

                                        // Muestra los datos del restaurante
                                        TextView nombreRestauranteTextView = findViewById(R.id.tvNombreRest);
                                        TextView direccionRestauranteTextView = findViewById(R.id.tvdireccionRest);

                                        nombreRestauranteTextView.setText(nombreRestaurante);
                                        direccionRestauranteTextView.setText(direccionRestaurante);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error al cargar los datos del restaurante", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar los datos del pedido", Toast.LENGTH_SHORT).show();
                });

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
            editor.putString("idPedido", idPedido); // ID del pedido
            editor.apply();


            Intent intent = new Intent(this, RecojoCurso1Activity.class);
            intent.putExtra("idPedido", idPedido);
            startActivity(intent);
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        myMap = googleMap;

        // Obtener datos desde el intent
        String direccion = getIntent().getStringExtra("direccion");
        String direccionRestaurante = getIntent().getStringExtra("direccionRest");

        // Utilizar Geocoding para obtener coordenadas
        obtenerCoordenadas(direccionRestaurante, coordenadasRestaurante -> {
            obtenerCoordenadas(direccion, coordenadasCliente -> {
                // Agregar marcadores con las coordenadas obtenidas
                Bitmap original = BitmapFactory.decodeResource(getResources(), R.drawable.shop_location_map);
                Bitmap resized = Bitmap.createScaledBitmap(original, 150, 150, false);
                myMap.addMarker(new MarkerOptions()
                        .position(coordenadasRestaurante)
                        .title("Punto de retiro")
                        .icon(BitmapDescriptorFactory.fromBitmap(resized)));

                Bitmap original1 = BitmapFactory.decodeResource(getResources(), R.drawable.home_address_location);
                Bitmap resized1 = Bitmap.createScaledBitmap(original1, 150, 150, false);
                myMap.addMarker(new MarkerOptions()
                        .position(coordenadasCliente)
                        .title("Punto de entrega")
                        .icon(BitmapDescriptorFactory.fromBitmap(resized1)));

                // Ajustar la cámara
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(coordenadasRestaurante);
                builder.include(coordenadasCliente);
                LatLngBounds bounds = builder.build();

                myMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            });
        });
    }

    private void obtenerCoordenadas(String direccion, OnCoordenadasObtenidas callback) {
        String apiKey = "AIzaSyAaD_Wke_n8sefeq8OMyVNlm9fJmU9CH-c"; // Reemplaza con tu clave de la API de Google
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                direccion.replace(" ", "%20") + "&key=" + apiKey;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Log.e("Geocoding", "Error al obtener coordenadas", e);
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray results = jsonObject.getJSONArray("results");
                        if (results.length() > 0) {
                            JSONObject location = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                            double lat = location.getDouble("lat");
                            double lng = location.getDouble("lng");

                            // Devolver las coordenadas a través del callback
                            runOnUiThread(() -> callback.onCoordenadasObtenidas(new LatLng(lat, lng)));
                        } else {
                            runOnUiThread(() -> Log.e("Geocoding", "No se encontraron resultados para la dirección: " + direccion));
                        }
                    } catch (JSONException e) {
                        runOnUiThread(() -> Log.e("Geocoding", "Error al analizar la respuesta de la API", e));
                    }
                } else {
                    runOnUiThread(() -> Log.e("Geocoding", "Error en la respuesta de la API: " + response.message()));
                }
            }
        });
    }

    // Interfaz para manejar las coordenadas obtenidas
    interface OnCoordenadasObtenidas {
        void onCoordenadasObtenidas(LatLng coordenadas);
    }

}
