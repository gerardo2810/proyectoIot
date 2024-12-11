package com.example.proyecto_iot.cliente;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
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

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.RecyclerView.Producto;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SeguimientoPedidoActivity extends AppCompatActivity {

    // Declaración de las variables para las vistas
    private ImageView qrIcon;
    private TextView cancelOrder, payHere, scanQr, tvDetalleOrden, tvVerificacionEnvio;
    private LinearLayout qrButton, verificationButton, backArrow;
    private CardView orderDetailsCard, qrCard; // Agregar CardView para los detalles y el QR

    // Variables para el seguimiento de estados
    private LinearLayout layoutEstados;

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

        // Obtener los datos enviados desde la actividad anterior
        Intent intent1 = getIntent();
        // Extraer los valores enviados en el Intent
        String pedidoId = intent1.getStringExtra("pedidoId");
        String direccion = intent1.getStringExtra("direccion");
        String fechaHora = intent1.getStringExtra("fechaHora");
        double precioTotal = intent1.getDoubleExtra("precioTotal", 0.0);
        double precioDelivery = intent1.getDoubleExtra("precioDelivery", 0.0);
        String nombreRestaurante = intent1.getStringExtra("nombreRestaurante");
        String idRestaurante = intent1.getStringExtra("idRestaurante");
        ArrayList<Producto> productos = (ArrayList<Producto>) intent1.getSerializableExtra("productos");

        // Imprimir los valores recibidos para verificar
        System.out.println("----- Datos recibidos en RealizarPedidoActivity -----");
        System.out.println("Pedido ID: " + pedidoId);
        System.out.println("Dirección: " + direccion);
        System.out.println("Fecha y Hora: " + fechaHora);
        System.out.println("Precio Total: " + precioTotal);
        System.out.println("Precio Delivery: " + precioDelivery);
        System.out.println("Nombre Restaurante: " + nombreRestaurante);
        System.out.println("ID Restaurante: " + idRestaurante);
        System.out.println("Productos: ");
        if (productos != null) {
            for (Producto producto : productos) {
                System.out.println(" - Producto ID: " + producto.getId());
                System.out.println(" - Nombre Producto: " + producto.getNombre());
                System.out.println(" - Cantidad: " + producto.getCantidad());
                System.out.println(" - Precio Unitario: " + producto.getPrecio());
            }
        } else {
            System.out.println("No se recibieron productos.");
        }


        // Enlazar las vistas de los textos "Detalle de Orden" y "Verificación de Envío"
        tvDetalleOrden = findViewById(R.id.tv_detalle_orden);
        tvVerificacionEnvio = findViewById(R.id.tv_verificacion_envio);
        TextView nameProductos = findViewById(R.id.order_title);
        TextView ordenTrackTextView = findViewById(R.id.ordenTrack);


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Escuchar cambios en el pedido en tiempo real
        if (pedidoId != null) {
            db.collection("pedidos").document(pedidoId)
                    .addSnapshotListener((documentSnapshot, e) -> {
                        if (e != null) {
                            System.err.println("Error al escuchar los cambios del pedido: " + e.getMessage());
                            return;
                        }
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            // Obtener datos del pedido
                            int estado = documentSnapshot.getLong("estado").intValue();
                            String idRepartidor = documentSnapshot.getString("idRepartidor");

                            // Actualizar el estado en la interfaz
                            actualizarEstado(estado, idRepartidor);

                            // Mostrar notificación al cambiar de estado
                            sendNotification(
                                    "Estado del pedido actualizado",
                                    "Tu pedido ahora está: " + obtenerEstadoPedido(estado)
                            );
                        }
                    });
        }



        FirebaseFirestore db2 = FirebaseFirestore.getInstance();
        // Verificar que el idRestaurante no sea nulo ni vacío
        if (idRestaurante != null && !idRestaurante.isEmpty()) {
            // Consultar Firestore para obtener los datos del restaurante
            db2.collection("restaurantes").document(idRestaurante)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Obtener los campos del documento
                            String nombreRestaurante1 = documentSnapshot.getString("nombre");
                            String direccionRestaurante = documentSnapshot.getString("ubicacion");

                            // Imprimir el nombre y la dirección
                            String temp = nombreRestaurante1 + " - " + direccionRestaurante;
                            System.out.println("Nombre del restaurante: " + nombreRestaurante1);
                            System.out.println("Dirección del restaurante: " + direccionRestaurante);
                            System.out.println("Combinado: " + temp);
                            nameProductos.setText(temp);
                        } else {
                            System.err.println("El restaurante no existe en Firestore.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        System.err.println("Error al obtener los datos del restaurante: " + e.getMessage());
                    });
        } else {
            System.err.println("El ID del restaurante es nulo o vacío.");
        }
        // Referencias a los TextViews
        TextView fechaTextView = findViewById(R.id.fecha);
        TextView repartidorTextView = findViewById(R.id.repartidor);
        TextView direccionClienteTextView = findViewById(R.id.direccio_cliente);
        TextView costoProductosTextView = findViewById(R.id.costoProductos);
        TextView deliveryTextView = findViewById(R.id.delivery);
        TextView pagoTotalTextView = findViewById(R.id.pagoTotal);
        ImageView qrPaga  = findViewById(R.id.qr_image);

// Obtener el ID del pedido desde el Intent
        if (pedidoId != null) {
            FirebaseFirestore db1 = FirebaseFirestore.getInstance();

            // Configurar un listener para escuchar cambios en el documento en tiempo real
            db1.collection("pedidos").document(pedidoId)
                    .addSnapshotListener((documentSnapshot, error) -> {
                        if (error != null) {
                            System.err.println("Error al escuchar cambios en el documento: " + error.getMessage());
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            // Obtener los datos iniciales del pedido
                            int estado = documentSnapshot.getLong("estado").intValue();
                            String direccionCliente = documentSnapshot.getString("direccion");
                            String idRepartidor = documentSnapshot.getString("idRepartidor");
                            String nombreRestaurante1 = documentSnapshot.getString("nombreRestaurante");
                            String qrUrl = documentSnapshot.getString("qrUrl"); // Obtener la URL del QR

                            System.out.println("Estado actual: " + estado);
                            System.out.println("QR PARA PAGO: " + qrUrl);

                            // Calcular el costo de los productos
                            double costoProductos = precioTotal - precioDelivery;

                            // Actualizar los TextViews
                            ordenTrackTextView.setText(obtenerEstadoPedido(estado));
                            fechaTextView.setText(" • " + fechaHora.split(",")[0]); // Solo la fecha
                            direccionClienteTextView.setText(direccionCliente);
                            costoProductosTextView.setText(String.format("S/. %.2f", costoProductos));
                            deliveryTextView.setText(String.format("S/. %.2f", precioDelivery));
                            pagoTotalTextView.setText(String.format("S/. %.2f", precioTotal));

                            // Manejo del campo repartidor/nombre restaurante
                            if (estado < 2) {
                                repartidorTextView.setText(nombreRestaurante);
                            } else {
                                if (idRepartidor != null && !idRepartidor.isEmpty()) {
                                    db.collection("repartidores").document(idRepartidor)
                                            .get()
                                            .addOnSuccessListener(repartidorSnapshot -> {
                                                if (repartidorSnapshot.exists()) {
                                                    String nombreRepartidor = repartidorSnapshot.getString("nombre");
                                                    repartidorTextView.setText(nombreRepartidor);
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                System.err.println("Error al obtener el repartidor: " + e.getMessage());
                                            });
                                }
                            }

                            // Asignar la URL del QR al ImageView usando Glide
                            if (qrUrl != null && !qrUrl.isEmpty()) {
                                Glide.with(SeguimientoPedidoActivity.this)
                                        .load(qrUrl)
                                        .placeholder(R.drawable.placeholder) // Imagen por defecto mientras se carga
                                        .error(R.drawable.placeholder) // Imagen en caso de error
                                        .into(qrPaga);
                                System.out.println("QR cargado correctamente: " + qrUrl);
                            } else {
                                System.err.println("El campo qrUrl está vacío o es nulo. Esperando actualización...");
                            }
                        }
                    });
        }


        // Enlazar las CardViews para los detalles de la orden y el QR
        orderDetailsCard = findViewById(R.id.order_details_card);
        qrCard = findViewById(R.id.qr_card);

        // Mostrar "Detalle de orden" al inicio y ocultar "Verificación de envío"
        orderDetailsCard.setVisibility(View.VISIBLE);
        qrCard.setVisibility(View.GONE);

        // Listener para la flecha de retroceso - Dirige a "RealizarPedidoActivity"
        backArrow.setOnClickListener(view -> {
            Intent intent = new Intent(SeguimientoPedidoActivity.this, RealizarPedidoActivity.class);
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
    }

    private void actualizarEstado(int estado, String idRepartidor) {
        // Cambiar la imagen del estado anterior a "check" (si no es el primer estado)
        switch (estado) {
            case 1: // Recibido
                findViewById(R.id.contenedor_recibido).setVisibility(View.VISIBLE);
                break;
            case 2: // En preparación
                findViewById(R.id.linea_1).setVisibility(View.VISIBLE);
                findViewById(R.id.contenedor_preparacion).setVisibility(View.VISIBLE);
                break;
            case 3: // En camino
                findViewById(R.id.linea_2).setVisibility(View.VISIBLE);
                findViewById(R.id.contenedor_camino).setVisibility(View.VISIBLE);

                // Mostrar el nombre del repartidor si está disponible
                if (idRepartidor != null && !idRepartidor.isEmpty()) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("repartidores").document(idRepartidor)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    String nombreRepartidor = documentSnapshot.getString("nombre");
                                    TextView repartidorTextView = findViewById(R.id.repartidor);
                                    repartidorTextView.setText(nombreRepartidor);
                                }
                            });
                }
                break;
            case 4: // Entregado
                findViewById(R.id.linea_3).setVisibility(View.VISIBLE);
                findViewById(R.id.contenedor_entregado).setVisibility(View.VISIBLE);
                break;
        }
    }

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

    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, SeguimientoPedidoActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_check_circle_outline_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, builder.build());
        }
    }

    private String obtenerEstadoPedido(int estado) {
        switch (estado) {
            case 0:
                return "Por aceptar";
            case 1:
                return "Recibido";
            case 2:
                return "En preparación";
            case 3:
                return "En camino";
            case 4:
                return "Entregado";
            case 5:
                return "Rechazado";
            case 6:
                return "Cancelado";
            default:
                return "Desconocido";
        }
    }
}
