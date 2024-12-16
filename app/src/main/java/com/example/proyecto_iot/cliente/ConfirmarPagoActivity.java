package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class ConfirmarPagoActivity extends AppCompatActivity {

    private TextView nombreRestauranteTextView;
    private TextView montoPagarTextView;
    private Button btnAceptarPago;
    private FirebaseFirestore db;
    private String pedidoId;
    private double precioTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_pago);

        db = FirebaseFirestore.getInstance();

        // Obtener referencias de las vistas
        nombreRestauranteTextView = findViewById(R.id.nombre_restaurante);
        montoPagarTextView = findViewById(R.id.monto_pagar);
        btnAceptarPago = findViewById(R.id.btn_aceptar_pago);

        // Obtener datos del Intent
        Intent intent = getIntent();
        pedidoId = intent.getStringExtra("pedidoId");
        precioTotal = intent.getDoubleExtra("precioTotal", 0.0);

        montoPagarTextView.setText(String.format("S/. %.2f", precioTotal));

        // Configurar botón de pago
        btnAceptarPago.setOnClickListener(v -> confirmarPago());
        btnAceptarPago.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Cambiar el estado del pedido a 4 (Entregado)
            db.collection("pedidos").document(pedidoId)
                    .update("estado", 4)
                    .addOnSuccessListener(aVoid -> {
                        // Guardar el pedido en el historial del cliente
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        db.collection("clientes").document(userId)
                                .update("historialPedidos", FieldValue.arrayUnion(pedidoId))
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(this, "Pago completado correctamente.", Toast.LENGTH_SHORT).show();
                                    // Redirigir al inicio
                                    Intent intent1 = new Intent(this, SeguimientoPedidoActivity.class);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error al guardar el historial.", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al actualizar estado del pedido.", Toast.LENGTH_SHORT).show();
                    });
        });

    }

    private void confirmarPago() {
        // Actualizar el estado del pedido a 4 ("Entregado")
        if (pedidoId != null) {
            db.collection("pedidos").document(pedidoId)
                    .update("estado", 4)
                    .addOnSuccessListener(aVoid -> {
                        // Agregar el pedido al historial del cliente
                        agregarPedidoAlHistorial();

                        // Notificar al usuario y regresar al inicio
                        Toast.makeText(this, "Pago completado correctamente.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, InicioClienteActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al completar el pago: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void agregarPedidoAlHistorial() {
        // Obtener el ID del cliente (este debe ser definido según tu implementación)
        String clienteId = "vJB3EEcNL9ZS5EybdVHWXwtDhL22"; // Ejemplo, reemplaza con el real

        db.collection("clientes").document(clienteId)
                .update("historialPedidos", FieldValue.arrayUnion(pedidoId))
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Pedido agregado al historial del cliente.");
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error al agregar el pedido al historial: " + e.getMessage());
                });
    }
}
