package com.example.proyecto_iot.repartidor;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.SeguimientoPedidoActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class RecojoCurso1Activity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;

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

        Button boton1 = findViewById(R.id.button3);
        boton1.setOnClickListener(v -> {

            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("ultima_vista", "RecojoCurso2Activity");
            editor.apply();


            Intent intent = new Intent(this, RecojoCurso2Activity.class);
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

        // Define las ubicaciones de origen y destino
        LatLng origen = new LatLng(-12.0682373, -77.0769483); // Coordenadas de ubicacion
        LatLng destino = new LatLng(-12.071507, -77.090343); // Coordenadas del restaurante

        // Agrega marcadores
        myMap.addMarker(new MarkerOptions()
                .position(origen)
                .title("Tu ubicacion")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        myMap.addMarker(new MarkerOptions()
                .position(destino)
                .title("Punto de retiro")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        // Ajusta la cámara
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(origen);
        builder.include(destino);
        LatLngBounds bounds = builder.build();

        myMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

}
