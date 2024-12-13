package com.example.proyecto_iot.repartidor;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.SeguimientoPedidoActivity;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RecojoCurso1Activity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;
    FusedLocationProviderClient fusedLocationClient;
    LocationCallback locationCallback;
    Marker marcadorActual;

    private final String CHANNEL_ID = "order_ready_channel";
    private Handler handler;
    private Runnable runnable;
    private int interval = 30 * 100; // 10 segundos en milisegundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recojo_curso_1);

        // Obtén el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Inicializa el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Crear canal de notificación
        createNotificationChannel();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(RecojoCurso1Activity.this,
                    new String[]{POST_NOTIFICATIONS},
                    101);
        }

        // Iniciar despues de 10 segundos
        iniciarNotificacion();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String idPedido = getIntent().getStringExtra("idPedido");
        db.collection("pedidos").document(idPedido)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String idRestaurante = documentSnapshot.getString("idRestaurante");
                        String estadoPedidotexto = "Listo para recoger";

                        TextView tvEstadoPedido = findViewById(R.id.textView5);
                        String texto5 = "Estado: "+ estadoPedidotexto;
                        tvEstadoPedido.setText(texto5);

                        // Ahora consulta los datos del restaurante
                        db.collection("restaurantes").document(idRestaurante)
                                .get()
                                .addOnSuccessListener(restauranteSnapshot -> {
                                    if (restauranteSnapshot.exists()) {
                                        String nombreRestaurante = restauranteSnapshot.getString("nombre");
                                        String direccionRestaurante = restauranteSnapshot.getString("ubicacion");
                                        String logoUrl = restauranteSnapshot.getString("fotoLogo");

                                        // Muestra los datos del restaurante
                                        TextView nombreRestauranteTextView = findViewById(R.id.product_name);
                                        TextView direccionRestauranteTextView = findViewById(R.id.product_description);
                                        ImageView imageViewRestaurante = findViewById(R.id.product_image);

                                        nombreRestauranteTextView.setText(nombreRestaurante);
                                        direccionRestauranteTextView.setText(direccionRestaurante);
                                        Glide.with(this)
                                                .load(logoUrl) // URL del logo
                                                .placeholder(R.drawable.baseline_file_upload_24) // Imagen temporal mientras carga
                                                .into(imageViewRestaurante); // Tu ImageView
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

        Button boton1 = findViewById(R.id.button3);
        boton1.setOnClickListener(v -> {

            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("ultima_vista", "RecojoCurso2Activity");
            editor.putString("idPedido", idPedido); // ID del pedido
            editor.apply();


            Intent intent = new Intent(this, RecojoCurso2Activity.class);
            intent.putExtra("idPedido", idPedido);
            startActivity(intent);
        });

    }

    private void iniciarNotificacion() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                sendNotification();
            }
        };
        handler.postDelayed(runnable, interval); // despues de 10 segundos
    }

    @SuppressWarnings("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, InicioRepartidorActivity.class); // Regresar a la vistaInicio
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia la pila de actividades
        startActivity(intent);
    }

    // Método para crear el canal de notificación (solo Android 8.0 o superior)
    public void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Pedido listo para recojer channel";
            String description = "Canal para notificaciones del pedido listo para recojer";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Método para enviar notificaciones
    private void sendNotification() {
        Intent intent = new Intent(this, RecojoCurso2Activity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_check_circle_outline_24) // Cambia este icono por uno propio
                .setContentTitle("Pedido listo para recoger!")
                .setContentText("Ya puedes acercarte! El pedido se encuentra listo para recogerlo :D")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true); // La notificación desaparece al hacer clic

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, builder.build());
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        myMap = googleMap;

        // Consulta los datos del restaurante
        String idPedido = getIntent().getStringExtra("idPedido");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("pedidos").document(idPedido)
                .get()
                .addOnSuccessListener(pedidoSnapshot -> {
                    if (pedidoSnapshot.exists()) {
                        String idRestaurante = pedidoSnapshot.getString("idRestaurante");

                        // Ahora consulta los datos del restaurante
                        db.collection("restaurantes").document(idRestaurante)
                                .get()
                                .addOnSuccessListener(restauranteSnapshot -> {
                                    if (restauranteSnapshot.exists()) {
                                        String direccionRestaurante = restauranteSnapshot.getString("ubicacion");

                                        // Convertir la dirección en coordenadas
                                        obtenerCoordenadas(direccionRestaurante, coordenadas -> {
                                            if (coordenadas != null) {
                                                LatLng destino = coordenadas;
                                                // Agrega el marcador al mapa
                                                Bitmap original1 = BitmapFactory.decodeResource(getResources(), R.drawable.shop_location_map);
                                                Bitmap resized1 = Bitmap.createScaledBitmap(original1, 150, 150, false);
                                                myMap.addMarker(new MarkerOptions()
                                                        .position(destino)
                                                        .title("Punto de retiro")
                                                        .icon(BitmapDescriptorFactory.fromBitmap(resized1)));
                                                // Ajustar la cámara al destino
                                                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destino, 15));
                                                // Configura actualizaciones de ubicación en tiempo real
                                                configurarActualizacionesUbicacion(destino);
                                            } else {
                                                Toast.makeText(this, "No se pudieron obtener las coordenadas del restaurante.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
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

    private void obtenerCoordenadas(String direccion, OnCoordenadasObtenidasCallback callback) {
        Geocoder geocoder = new Geocoder(this);
        new Thread(() -> {
            try {
                List<Address> direcciones = geocoder.getFromLocationName(direccion, 1);
                if (direcciones != null && !direcciones.isEmpty()) {
                    Address direccionObtenida = direcciones.get(0);
                    LatLng coordenadas = new LatLng(direccionObtenida.getLatitude(), direccionObtenida.getLongitude());
                    runOnUiThread(() -> callback.onCoordenadasObtenidas(coordenadas));
                } else {
                    runOnUiThread(() -> callback.onCoordenadasObtenidas(null));
                }
            } catch (Exception e) {
                runOnUiThread(() -> callback.onCoordenadasObtenidas(null));
            }
        }).start();
    }

    interface OnCoordenadasObtenidasCallback {
        void onCoordenadasObtenidas(LatLng coordenadas);
    }

}
