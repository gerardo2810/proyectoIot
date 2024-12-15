package com.example.proyecto_iot.admin_restaurante;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InfoPedidoHistorialActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String pedidoId; // ID del pedido recibido del Intent
    private Pedido pedidoActual;

    private TextView tvClienteNombre, tvDireccion, tvFechaHora, tvPrecioTotal, orderStatusTextView, tvCodigoPedido, tvRepartidor;
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
        tvCodigoPedido = findViewById(R.id.codigoPedido);
        tvClienteNombre = findViewById(R.id.tv_cliente_nombre);
        tvDireccion = findViewById(R.id.tv_cliente_direccion);
        tvRepartidor = findViewById(R.id.repartidor);
        tvFechaHora = findViewById(R.id.tv_pedido_fecha);
        tvPrecioTotal = findViewById(R.id.tv_total_precio);
        orderStatusTextView = findViewById(R.id.order_status_button);
        rvListaProductos = findViewById(R.id.rv_lista_productos);
        rvListaProductos.setLayoutManager(new LinearLayoutManager(this));

        // Cargar los detalles del pedido
        cargarDetallesPedido();

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void cargarDetallesPedido() {
        db.collection("pedidos").document(pedidoId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        pedidoActual = documentSnapshot.toObject(Pedido.class);
                        if (pedidoActual != null) {
                            mostrarDetallesPedido();
                            fetchRepartidor(pedidoActual); // Obtener información del repartidor
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
        tvCodigoPedido.setText(pedidoActual.getCodigo());
        tvDireccion.setText(pedidoActual.getDireccion());
        tvPrecioTotal.setText(String.format("S/ %.2f", pedidoActual.getPagoTotal()));

        // Extraer solo la hora
        String horaFormateada = obtenerSoloHora(pedidoActual.getFechaHora());
        tvFechaHora.setText(horaFormateada);

        tvClienteNombre.setText(
                (pedidoActual.getNombreCliente() != null ? pedidoActual.getNombreCliente() : "") + " " +
                        (pedidoActual.getApellidoCliente() != null ? pedidoActual.getApellidoCliente() : "Cliente desconocido")
        );

        // Configurar el RecyclerView para los productos
        productoPedidoAdapter = new ProductoPedidoAdapter(pedidoActual.getProductos(), this);
        rvListaProductos.setAdapter(productoPedidoAdapter);

        // Configurar el estado del pedido
        configurarEstadoPedido();
    }

    private void fetchRepartidor(Pedido pedido) {
        if (pedido.getIdRepartidor() == null || pedido.getIdRepartidor().isEmpty()) {
            asignarRepartidor("Sin repartidor asignado", false);
        } else {
            db.collection("repartidores").document(pedido.getIdRepartidor())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nombreRepartidor = documentSnapshot.getString("nombre");
                            asignarRepartidor(nombreRepartidor, true);
                        } else {
                            asignarRepartidor("Repartidor desconocido", false);
                        }
                    })
                    .addOnFailureListener(e -> asignarRepartidor("Error al cargar repartidor", false));
        }
    }

    private void asignarRepartidor(String nombreRepartidor, boolean asignado) {
        tvRepartidor.setText(nombreRepartidor);
        if (!asignado) {
            tvRepartidor.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        } else {
            tvRepartidor.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        }
    }

    private void configurarEstadoPedido() {
        if (orderStatusTextView == null || pedidoActual == null) {
            return;
        }

        String estadoText = "";
        int estadoColor = R.color.black; // Color por defecto

        // Asignar estado y color según el estado del pedido
        switch (pedidoActual.getEstado()) {
            case 0:
                estadoText = "Por Aceptar";
                estadoColor = R.color.poraceptar;
                break;
            case 1:
                estadoText = "En preparación";
                estadoColor = R.color.order_in_preparation;
                break;
            case 2:
                estadoText = "En el restaurante";
                estadoColor = R.color.blue;
                break;
            case 3:
                estadoText = "En el restaurante";
                estadoColor = R.color.blue;
                break;
            case 7:
                estadoText = "En camino";
                estadoColor = R.color.amarillo_estado;
                break;
            case 4:
                estadoText = "Entregado";
                estadoColor = R.color.green;
                break;
            case 5:
                estadoText = "Rechazado";
                estadoColor = R.color.sin_repartidor;
                break;
            case 6:
                estadoText = "Cancelado";
                estadoColor = R.color.md_theme_error_highContrast;
                break;
            case 8:
                estadoText = "En camino";
                estadoColor = R.color.amarillo_estado;
                break;
            default:
                estadoText = "Desconocido";
                estadoColor = R.color.black;
                break;
        }

        // Asignar el texto y el color del texto
        orderStatusTextView.setText(estadoText);
        orderStatusTextView.setTextColor(ContextCompat.getColor(this, estadoColor));
    }



    private String obtenerSoloHora(String fechaCompleta) {
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy, hh:mm:ss a", Locale.getDefault());
            SimpleDateFormat formatoHora = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
            Date fecha = formatoEntrada.parse(fechaCompleta);
            return formatoHora.format(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Hora no disponible";
        }
    }
}

