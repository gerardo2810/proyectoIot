package com.example.proyecto_iot.cliente;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.RecyclerView.Producto;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreandoPedidoActivity extends AppCompatActivity {

    private TextView restaurantNameText;
    private TextView pedidoStatusText;
    private Button cancelButton;
    private FirebaseFirestore db;
    private String pedidoId;
    private boolean isCancelled = false;
    private final int MAX_WAIT_TIME_MS = 240000; // 4 minutos en milisegundos
    private Handler timeoutHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creando_pedido);

        // Referencia a Firestore
        db = FirebaseFirestore.getInstance();

        // Obtener datos del intent
        pedidoId = getIntent().getStringExtra("pedidoId");
        String nombreRestaurante = getIntent().getStringExtra("nombreRestaurante");

        // Configurar vistas
        restaurantNameText = findViewById(R.id.restaurant_name_text);
        pedidoStatusText = findViewById(R.id.pedido_status_text);
        cancelButton = findViewById(R.id.cancel_order_button);

        // Obtener los datos del Intent
        String pedidoId = getIntent().getStringExtra("pedidoId");
        String direccionCliente = getIntent().getStringExtra("direccion");
        double subtotal = getIntent().getDoubleExtra("subtotal", 0.0);
        double precioTotal = getIntent().getDoubleExtra("precioTotal", 0.0);
        double precioDelivery = getIntent().getDoubleExtra("precioDelivery", 0.0);
        ArrayList<Producto> productos = (ArrayList<Producto>) getIntent().getSerializableExtra("productos");

        // Mostrar el nombre del restaurante en el preloader
        TextView restaurantNameText = findViewById(R.id.restaurant_name_text);
        restaurantNameText.setText("Restaurante: " + nombreRestaurante);

        restaurantNameText.setText("Restaurante: " + nombreRestaurante);

        // Configurar botón de cancelar
        cancelButton.setOnClickListener(v -> cancelOrder());

        // Comenzar a escuchar cambios en el estado del pedido
        listenForOrderStatus();

        // Iniciar timeout de 4 minutos
        timeoutHandler = new Handler(Looper.getMainLooper());
        timeoutHandler.postDelayed(() -> {
            if (!isCancelled) {
                showOrderCancelledAlert();
                updateOrderStatus(5); // Cambiar a estado "5" si excede el tiempo
            }
        }, MAX_WAIT_TIME_MS);
    }

    private void listenForOrderStatus() {
        DocumentReference pedidoRef = db.collection("pedidos").document(pedidoId);

        pedidoRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                System.err.println("Error al escuchar el estado del pedido: " + error.getMessage());
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                int estado = snapshot.getLong("estado").intValue();

                // Actualizar texto de estado
                String statusText = "Estado del pedido: " + estado;
                pedidoStatusText.setText(statusText);

                // Verificar cambios de estado
                if (estado == 1 || estado==2) {
                    // Pedido aceptado, pasar a SeguimientoPedidoActivity
                    navigateToTracking();
                } else if (estado == 5) {
                    // Pedido cancelado
                    showOrderCancelledAlert();
                }
            }
        });
    }

    private void cancelOrder() {
        // Cambiar estado del pedido a "6"
        updateOrderStatus(6);

        // Regresar a RealizarPedidoActivity
        Intent intent = new Intent(CreandoPedidoActivity.this, RealizarPedidoActivity.class);
        startActivity(intent);
        finish(); // Finalizar esta actividad
    }

    private void showOrderCancelledAlert() {
        if (!isCancelled) {
            isCancelled = true; // Evitar múltiples alertas
            new AlertDialog.Builder(this)
                    .setTitle("Orden Cancelada")
                    .setMessage("Lo sentimos, tu orden fue cancelada.")
                    .setPositiveButton("Aceptar", (dialog, which) -> {
                        Intent intent = new Intent(CreandoPedidoActivity.this, RealizarPedidoActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    private void updateOrderStatus(int newStatus) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("estado", newStatus);

        db.collection("pedidos").document(pedidoId)
                .update(updateData)
                .addOnSuccessListener(aVoid -> System.out.println("Estado del pedido actualizado a: " + newStatus))
                .addOnFailureListener(e -> System.err.println("Error al actualizar el estado: " + e.getMessage()));
    }

    private void navigateToTracking() {
        // Cancelar timeout
        timeoutHandler.removeCallbacksAndMessages(null);

        // Navegar a SeguimientoPedidoActivity con los datos
        Intent intent = new Intent(CreandoPedidoActivity.this, SeguimientoPedidoActivity.class);
        intent.putExtras(getIntent()); // Pasar todos los datos originales
        startActivity(intent);
        finish(); // Finalizar esta actividad
    }
}
