package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Pedido;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Producto;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.ProductoPedido;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.ProductoPedidoAdapter;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;

public class PedidoDetallesActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String pedidoId; // ID del pedido recibido del Intent
    private Pedido pedidoActual;

    private TextView tvCodigoPedido, tvClienteNombre, tvDireccion, tvFechaHora, tvPrecioTotal;
    private RecyclerView rvListaProductos;
    private ProductoPedidoAdapter productoPedidoAdapter;
    private Button btnAceptar, btnRechazar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurante_activity_mas_detalles);

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
        tvCodigoPedido = findViewById(R.id.codigoPedido);
        tvClienteNombre = findViewById(R.id.tv_cliente_nombre);
        tvDireccion = findViewById(R.id.tv_cliente_direccion);
        tvFechaHora = findViewById(R.id.tv_pedido_fecha);
        tvPrecioTotal = findViewById(R.id.tv_total_precio);
        rvListaProductos = findViewById(R.id.rv_lista_productos);
        rvListaProductos.setLayoutManager(new LinearLayoutManager(this));
        btnAceptar = findViewById(R.id.btn_aceptar);
        btnRechazar = findViewById(R.id.btn_rechazar);

        // Cargar los detalles del pedido
        cargarDetallesPedido();

        // Configurar botones con confirmación
        btnAceptar.setOnClickListener(v -> mostrarConfirmacionCambioEstado(1)); // Estado "1" = Aceptado
        btnRechazar.setOnClickListener(v -> mostrarConfirmacionCambioEstado(5)); // Estado "5" = Rechazado
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
        tvCodigoPedido.setText(pedidoActual.getCodigo());
        tvDireccion.setText(pedidoActual.getDireccion());
        tvPrecioTotal.setText(String.format("S/ %.2f", pedidoActual.getPagoTotal()));
        tvFechaHora.setText(pedidoActual.getFechaHora());
        tvClienteNombre.setText(pedidoActual.getNombreCliente() + " " + pedidoActual.getApellidoCliente() != null ? pedidoActual.getNombreCliente() + " " + pedidoActual.getApellidoCliente()  : "Desconocido");

        // Configurar el RecyclerView para mostrar los productos
        productoPedidoAdapter = new ProductoPedidoAdapter(pedidoActual.getProductos(), this);
        rvListaProductos.setAdapter(productoPedidoAdapter);
    }

    private void mostrarConfirmacionCambioEstado(int nuevoEstado) {
        // Crear un AlertDialog personalizado
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = LayoutInflater.from(this).inflate(R.layout.restaurante_dialog_confirmacion, null);
        builder.setView(customView);

        // Referencias a los elementos del diseño
        TextView tvTitulo = customView.findViewById(R.id.tv_titulo_confirmacion);
        TextView tvMensaje = customView.findViewById(R.id.tv_mensaje_confirmacion);
        Button btnConfirmar = customView.findViewById(R.id.btn_confirmar);
        Button btnCancelar = customView.findViewById(R.id.btn_cancelar);

        // Personalizar el título y mensaje
        tvTitulo.setText(nuevoEstado == 1 ? "Aceptar Pedido" : "Rechazar Pedido");
        tvMensaje.setText(nuevoEstado == 1
                ? "¿Estás seguro de aceptar este pedido?"
                : "¿Estás seguro de rechazar este pedido?");

        AlertDialog dialog = builder.create();

        // Configurar el botón "Confirmar"
        btnConfirmar.setOnClickListener(v -> {
            actualizarEstadoPedido(nuevoEstado); // Actualiza el estado
            dialog.dismiss(); // Cierra el diálogo
        });

        // Configurar el botón "Cancelar"
        btnCancelar.setOnClickListener(v -> dialog.dismiss()); // Solo cierra el diálogo

        // Mostrar el diálogo
        dialog.show();
    }

    private void actualizarEstadoPedido(int nuevoEstado) {
        db.collection("pedidos").document(pedidoId)
                .update("estado", nuevoEstado)
                .addOnSuccessListener(aVoid -> {
                    Log.d("PedidoDetalles", "Estado actualizado en Firestore.");

                    // Actualización secundaria: fuerza un trigger lógico de REMOVED
                    db.collection("pedidos").document(pedidoId)
                            .update("estado_updated_at", FieldValue.serverTimestamp())
                            .addOnSuccessListener(aVoid2 -> {
                                if (nuevoEstado == 1) {
                                    actualizarCantidadDeVentas(() -> {
                                        Toast.makeText(this, "Pedido aceptado y actualizado.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    });
                                } else {
                                    Toast.makeText(this, "Pedido rechazado.", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al actualizar el estado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



    private void actualizarCantidadDeVentas(Runnable onComplete) {
        List<ProductoPedido> productos = pedidoActual.getProductos();
        if (productos == null || productos.isEmpty()) {
            Toast.makeText(this, "No hay productos para actualizar.", Toast.LENGTH_SHORT).show();
            onComplete.run();
            return;
        }

        for (ProductoPedido productoPedido : productos) {
            String idProducto = productoPedido.getId();
            int cantidad = productoPedido.getCantidad();

            db.collection("platos").document(idProducto)
                    .update("cantidadDeVentas", FieldValue.increment(cantidad))
                    .addOnSuccessListener(aVoid -> Log.d("PedidoDetalles", "Cantidad de ventas actualizada: " + idProducto))
                    .addOnFailureListener(e -> Log.e("PedidoDetalles", "Error al actualizar cantidad de ventas: " + e.getMessage()));
        }
        onComplete.run();
    }
}
