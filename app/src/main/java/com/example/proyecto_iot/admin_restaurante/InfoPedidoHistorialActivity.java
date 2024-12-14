package com.example.proyecto_iot.admin_restaurante;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Pedido;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.ProductoPedidoAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

public class InfoPedidoHistorialActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String pedidoId; // ID del pedido recibido del Intent
    private Pedido pedidoActual;

    private TextView tvClienteNombre, tvDireccion, tvFechaHora, tvPrecioTotal;
    private Button orderStatusButton; // Botón para el estado del pedido
    private RecyclerView rvListaProductos;
    private ProductoPedidoAdapter productoPedidoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_info_pedido_historial);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Obtener ID del pedido del Intent
        pedidoId = getIntent().getStringExtra("pedidoId");

        if (pedidoId == null || pedidoId.isEmpty()) {
            Toast.makeText(this, "ID del pedido no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar vistas
        tvClienteNombre = findViewById(R.id.tv_cliente_nombre);
        tvDireccion = findViewById(R.id.tv_cliente_direccion);
        tvFechaHora = findViewById(R.id.tv_pedido_fecha);
        tvPrecioTotal = findViewById(R.id.tv_total_precio);
        orderStatusButton = findViewById(R.id.order_status_button);
        if (orderStatusButton == null) {
            Log.e("InfoPedidoHistorial", "El botón order_status_button no se encontró en el diseño.");
        }
        rvListaProductos = findViewById(R.id.rv_lista_productos);
        rvListaProductos.setLayoutManager(new LinearLayoutManager(this));

        // Cargar los detalles del pedido
        cargarDetallesPedido();
    }

    private void cargarDetallesPedido() {
        db.collection("pedidos").document(pedidoId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        pedidoActual = documentSnapshot.toObject(Pedido.class);
                        if (pedidoActual != null) {
                            mostrarDetallesPedido();
                        } else {
                            Toast.makeText(this, "Error al cargar el pedido.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Pedido no encontrado.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al obtener el pedido: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void mostrarDetallesPedido() {
        // Configurar los detalles generales del pedido
        tvDireccion.setText(pedidoActual.getDireccion());
        tvPrecioTotal.setText(String.format("S/ %.2f", pedidoActual.getPagoTotal()));
        tvFechaHora.setText(pedidoActual.getFechaHora());

        // Obtener el nombre del cliente desde Firestore
        db.collection("clientes").document(pedidoActual.getIdCliente())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombreCliente = documentSnapshot.getString("Nombre");
                        tvClienteNombre.setText("Cliente: " + (nombreCliente != null ? nombreCliente : "Desconocido"));
                    } else {
                        tvClienteNombre.setText("Cliente: Desconocido");
                    }
                });

        // Configurar el RecyclerView para mostrar los productos
        productoPedidoAdapter = new ProductoPedidoAdapter(pedidoActual.getProductos(), this);
        rvListaProductos.setAdapter(productoPedidoAdapter);

        // Configurar el estado del pedido y color del botón
        configurarEstadoPedido();
    }

    private void configurarEstadoPedido() {
        if (orderStatusButton == null || pedidoActual == null) {
            return; // Evitar errores si el botón o pedido es nulo
        }

        String estadoText = "";
        int estadoColor = android.R.color.darker_gray; // Color por defecto

        switch (pedidoActual.getEstado()) {
            case 0:
                estadoText = "POR ACEPTAR";
                estadoColor = R.color.blue_light;
                break;
            case 1:
                estadoText = "EN PREPARACIÓN";
                estadoColor = R.color.order_in_preparation;
                break;
            case 2:
                estadoText = "EN TIENDA";
                estadoColor = R.color.order_in_store;
                break;
            case 7:
                estadoText = "EN CAMINO";
                estadoColor = R.color.order_on_the_way;
                break;
            case 4:
                estadoText = "ENTREGADO";
                estadoColor = R.color.order_delivered;
                break;
            case 5:
                estadoText = "RECHAZADO";
                estadoColor = R.color.sin_repartidor;
                break;
            default:
                estadoText = "DESCONOCIDO";
                break;
        }

        orderStatusButton.setText(estadoText);
        orderStatusButton.setBackgroundTintList(ContextCompat.getColorStateList(this, estadoColor));
    }
}
