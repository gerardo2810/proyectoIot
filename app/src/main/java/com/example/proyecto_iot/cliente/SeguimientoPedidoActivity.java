package com.example.proyecto_iot.cliente;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.pm.PackageManager;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class SeguimientoPedidoActivity extends AppCompatActivity {

    // Declaración de las variables para las vistas
    private ImageView qrIcon;
    private TextView cancelOrder, payHere, scanQr, tvDetalleOrden, tvVerificacionEnvio;
    private LinearLayout qrButton, verificationButton, backArrow;
    private CardView orderDetailsCard, qrCard; // Agregar CardView para los detalles y el QR

    // Variables para el seguimiento de estados
    private LinearLayout layoutEstados;
    private Handler handler;
    private Runnable runnable;
    private int estadoActual = 0;
    private int interval = 2 * 60 * 100; // 2 minutos en milisegundos
    private String[] estados = {"Recibido", "En preparación", "En camino", "Entregado"};
    private int[] imagenesEstados = {R.drawable.placeholder, R.drawable.reportes, R.drawable.reportes_1, R.drawable.repartidor_superadmin};
    private int[] imagenesRevisado = {R.drawable.baseline_check_circle_outline_24, R.drawable.baseline_check_circle_outline_24, R.drawable.baseline_check_circle_outline_24};  // Imágenes revisadas para cada estado

    // ID del canal de notificaciones
    private final String CHANNEL_ID = "order_tracking_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seguimiento_pedido_cliente);

        // Crear canal de notificación
        createNotificationChannel();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        1);
            }
        }

        // Ajustar los márgenes para las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Enlazar las vistas con sus respectivos IDs en el layout
        backArrow = findViewById(R.id.header_layout);
        qrIcon = findViewById(R.id.qr_icon);
        cancelOrder = findViewById(R.id.see_more);
        payHere = findViewById(R.id.text_pay_here);
        scanQr = findViewById(R.id.text_scan_qr);
        qrButton = findViewById(R.id.qr_button);

        // Enlazar el layout donde se mostrarán los estados
        layoutEstados = findViewById(R.id.layout_estados);

        // Ocultar todos los estados excepto el primero (Recibido)
        findViewById(R.id.contenedor_preparacion).setVisibility(View.GONE);
        findViewById(R.id.contenedor_camino).setVisibility(View.GONE);
        findViewById(R.id.contenedor_entregado).setVisibility(View.GONE);
        findViewById(R.id.linea_1).setVisibility(View.GONE);
        findViewById(R.id.linea_2).setVisibility(View.GONE);
        findViewById(R.id.linea_3).setVisibility(View.GONE);

        // Enlazar las vistas de los textos "Detalle de Orden" y "Verificación de Envío"
        tvDetalleOrden = findViewById(R.id.tv_detalle_orden);
        tvVerificacionEnvio = findViewById(R.id.tv_verificacion_envio);

        // Enlazar las CardViews para los detalles de la orden y el QR
        orderDetailsCard = findViewById(R.id.order_details_card);
        qrCard = findViewById(R.id.qr_card);

        // Mostrar "Detalle de orden" al inicio y ocultar "Verificación de envío"
        orderDetailsCard.setVisibility(View.VISIBLE);
        qrCard.setVisibility(View.GONE);

        // Iniciar la actualización de los estados cada 2 minutos
        iniciarTrackingEstados();

        // Listener para la flecha de retroceso - Dirige a "RealizarPedidoActivity"
        backArrow.setOnClickListener(view -> {
            Intent intent = new Intent(SeguimientoPedidoActivity.this, RealizarPedidoActivity.class);
            startActivity(intent);
        });

        // Listener para el botón de "Cancelar Órden" - Dirige a "InicioClienteActivity"
        cancelOrder.setOnClickListener(view -> {
            Intent intent = new Intent(SeguimientoPedidoActivity.this, InicioClienteActivity.class);
            startActivity(intent);
        });

        // Listener para el botón de QR - Dirige a "SeguimientoPedidoActivity"
        qrButton.setOnClickListener(view -> {
            Intent intent = new Intent(SeguimientoPedidoActivity.this, SeguimientoPedidoActivity.class);
            startActivity(intent);
        });

        // Listener para "Detalle de Orden"
        tvDetalleOrden.setOnClickListener(v -> {
            // Mostrar Detalle de Orden y ocultar Verificación de envío
            orderDetailsCard.setVisibility(View.VISIBLE);
            qrCard.setVisibility(View.GONE);

            // Cambiar colores para indicar selección
            findViewById(R.id.btn_detalle_orden).setBackgroundResource(R.drawable.rounded_background_white);  // Fondo seleccionado
            findViewById(R.id.btn_verificacion_envio).setBackgroundResource(R.drawable.rounded_background_grey);  // Fondo no seleccionado
        });

        // Listener para "Verificación de Envío"
        tvVerificacionEnvio.setOnClickListener(v -> {
            // Mostrar Verificación de envío y ocultar Detalle de Orden
            qrCard.setVisibility(View.VISIBLE);
            orderDetailsCard.setVisibility(View.GONE);

            // Cambiar colores para indicar selección
            findViewById(R.id.btn_verificacion_envio).setBackgroundResource(R.drawable.rounded_background_white);  // Fondo seleccionado
            findViewById(R.id.btn_detalle_orden).setBackgroundResource(R.drawable.rounded_background_grey);  // Fondo no seleccionado
        });

        // Configuración del BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_restaurantes) {
                startActivity(new Intent(SeguimientoPedidoActivity.this, InicioClienteActivity.class));
                return true;
            } else if (id == R.id.nav_carrito) {
                startActivity(new Intent(SeguimientoPedidoActivity.this, CarritoClienteActivity.class));
                return true;
            } else if (id == R.id.navigation_ordenes) {
                startActivity(new Intent(SeguimientoPedidoActivity.this, HistorialPedidosActivity.class));
                return true;
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(SeguimientoPedidoActivity.this, PerfilClienteActivity.class));
                return true;
            }

            return false;
        });
    }

    private void iniciarTrackingEstados() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (estadoActual < estados.length) {
                    actualizarEstado();
                    handler.postDelayed(this, interval); // Repetir cada 2 minutos
                }
            }
        };
        handler.post(runnable); // Iniciar el proceso
    }

    private void actualizarEstado() {
        // Cambiar la imagen del estado anterior a "check" (si no es el primer estado)
        if (estadoActual > 0) {
            if (estadoActual == 1) {
                // Reemplazar la imagen del estado "Recibido" por la de "check"
                ImageView estadoRecibido = findViewById(R.id.estado_recibido);
                estadoRecibido.setImageResource(R.drawable.baseline_check_circle_outline_24);
            } else if (estadoActual == 2) {
                // Reemplazar la imagen del estado "En preparación" por la de "check"
                ImageView estadoPreparacion = findViewById(R.id.estado_preparacion);
                estadoPreparacion.setImageResource(R.drawable.baseline_check_circle_outline_24);
            } else if (estadoActual == 3) {
                // Reemplazar la imagen del estado "En camino" por la de "check"
                ImageView estadoEnCamino = findViewById(R.id.estado_en_camino);
                estadoEnCamino.setImageResource(R.drawable.baseline_check_circle_outline_24);
            }
        }

        // Notificaciones para cada estado
        if (estadoActual == 1) {
            sendNotification("Pedido en preparación", "Tu pedido ha pasado a preparación.");
            findViewById(R.id.linea_1).setVisibility(View.VISIBLE);
            findViewById(R.id.contenedor_preparacion).setVisibility(View.VISIBLE);
        } else if (estadoActual == 2) {
            sendNotification("Pedido en camino", "Tu pedido está en camino.");
            findViewById(R.id.linea_2).setVisibility(View.VISIBLE);
            findViewById(R.id.contenedor_camino).setVisibility(View.VISIBLE);
        } else if (estadoActual == 3) {
            sendNotification("Pedido entregado", "Tu pedido ha sido entregado.");
            findViewById(R.id.linea_3).setVisibility(View.VISIBLE);
            findViewById(R.id.contenedor_entregado).setVisibility(View.VISIBLE);
        }

        // Incrementar el estado actual para la próxima ejecución
        estadoActual++;
    }

    // Método para crear el canal de notificación (solo Android 8.0 o superior)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Seguimiento de Pedido";
            String description = "Notificaciones del seguimiento del pedido";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso otorgado: puedes continuar enviando notificaciones
            } else {
                // Permiso denegado: aquí puedes mostrar un mensaje explicando por qué es necesario
            }
        }
    }

    // Método para enviar notificaciones
    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, SeguimientoPedidoActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_check_circle_outline_24) // Cambia este icono por uno propio
                .setContentTitle(title)
                .setContentText(message)
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
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // Detener el handler cuando la actividad se destruye
    }
}
