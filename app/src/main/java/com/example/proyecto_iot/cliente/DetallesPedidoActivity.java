package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.RecyclerView.Producto;
import com.example.proyecto_iot.cliente.RecyclerView.ProductoDetallesAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetallesPedidoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductoDetallesAdapter adapter;
    private List<Producto> productoList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_pedido_cliente);

        // Inicializar FirebaseFirestore
        db = FirebaseFirestore.getInstance();
        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recycler_productos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productoList = new ArrayList<>();
        adapter = new ProductoDetallesAdapter(this,productoList);
        recyclerView.setAdapter(adapter);

        // Cargar productos desde Firebase

        // Referencias a los TextViews
        TextView orderTitleTextView = findViewById(R.id.order_title);
        TextView estadoTextView = findViewById(R.id.estado);
        TextView fechaTextView = findViewById(R.id.fecha);
        TextView productsTitleTextView = findViewById(R.id.products_title);

        // Obtener datos del Intent
        Intent intent = getIntent();
        String nombreRestaurante = intent.getStringExtra("nombreRestaurante");
        String direccion = intent.getStringExtra("direccion");
        int estado = intent.getIntExtra("estado", -1);
        String fechaHora = intent.getStringExtra("fechaHora");

        ArrayList<HashMap<String, Object>> productosData = (ArrayList<HashMap<String, Object>>) intent.getSerializableExtra("productos");

// Lista de productos finales que se pasará al adaptador
        List<Producto> productos = new ArrayList<>();

        if (productosData != null) {
            for (HashMap<String, Object> productoMap : productosData) {
                String id = (String) productoMap.get("id");
                String nombre = (String) productoMap.get("nombre");
                String descripcion = (String) productoMap.get("descripcion");
                double precio = productoMap.containsKey("precio") ? ((Number) productoMap.get("precio")).doubleValue() : 0.0;
                int cantidad = productoMap.containsKey("cantidad") ? ((Number) productoMap.get("cantidad")).intValue() : 0;
                String imageUrl = (String) productoMap.get("imageUrl");

                // Crear objeto Producto y agregarlo a la lista
                productos.add(new Producto(id, nombre, descripcion, precio, cantidad, imageUrl));
            }
        }

// Pasar la lista convertida al adaptador
        adapter = new ProductoDetallesAdapter(this, productos);
        recyclerView.setAdapter(adapter);

        // Combinar nombre del restaurante y dirección
        if (nombreRestaurante != null && direccion != null) {
            orderTitleTextView.setText(String.format("%s - %s", nombreRestaurante, direccion));
        }

        // Asignar estado
        estadoTextView.setText(obtenerEstadoPedido(estado)); // Llama a un método helper para traducir el estado numérico a texto

        // Asignar fecha
        if (fechaHora != null) {
            // Extraer solo la fecha si fechaHora está en formato "dd/MM/yyyy HH:mm:ss"
            String[] fechaPartes = fechaHora.split(",");
            if (fechaPartes.length > 0) {
                fechaTextView.setText(" • " + fechaPartes[0].trim());
            }
        }

        // Asignar cantidad de productos
        if (productos != null) {
            int totalProductos = productos.size();
            productsTitleTextView.setText(String.format("Productos - %d", totalProductos));
        }

        // Configurar la flecha de retroceso
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a la vista de Historial de Pedidos
                Intent intent = new Intent(DetallesPedidoActivity.this, HistorialPedidosActivity.class);
                startActivity(intent);
                finish(); // Finaliza la actividad actual para no volver a ella con el botón de retroceso
            }
        });

        // Configurar el botón "Ver más"
        TextView verMas = findViewById(R.id.see_more);
        verMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a la vista de Todos los Productos
                Intent intent = new Intent(DetallesPedidoActivity.this, TodosLosProductosActivity.class);
                startActivity(intent);
            }
        });

        // Referencias a los TextViews
        TextView direccionTextView = findViewById(R.id.direccion);
        TextView repartidorTextView = findViewById(R.id.repartidor);
        TextView costoProductosTextView = findViewById(R.id.costoProductos);
        TextView precioDeliveryTextView = findViewById(R.id.precioDelivery);
        TextView pagoTotalTextView = findViewById(R.id.pagoTotal);
        // Obtener datos del Intent
        String pedidoId = intent.getStringExtra("pedidoId");

        if (pedidoId != null) {
            // Consultar Firestore para obtener los datos de la orden
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("pedidos").document(pedidoId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Obtener los datos del pedido
                            String idRepartidor = documentSnapshot.getString("idRepartidor");

                            double costoProductos = documentSnapshot.contains("pagoTotal")
                                    ? documentSnapshot.getDouble("pagoTotal") - documentSnapshot.getDouble("precioDelivery")
                                    : 0.0;
                            double precioDelivery = documentSnapshot.contains("precioDelivery")
                                    ? documentSnapshot.getDouble("precioDelivery")
                                    : 0.0;
                            double pagoTotal = costoProductos + precioDelivery;

                            // Actualizar dirección del cliente
                            direccionTextView.setText(direccion != null ? direccion : "Sin dirección");

                            // Obtener el nombre del repartidor desde la colección "repartidores"
                            if (idRepartidor != null && !idRepartidor.isEmpty()) {
                                db.collection("repartidores").document(idRepartidor)
                                        .get()
                                        .addOnSuccessListener(repartidorSnapshot -> {
                                            if (repartidorSnapshot.exists()) {
                                                String nombreRepartidor = repartidorSnapshot.getString("nombre");
                                                repartidorTextView.setText(nombreRepartidor != null ? nombreRepartidor : "Sin repartidor asignado");
                                            } else {
                                                repartidorTextView.setText("Sin repartidor asignado");
                                            }
                                        })
                                        .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener repartidor", e));
                            } else {
                                repartidorTextView.setText("Sin repartidor asignado");
                            }

                            // Actualizar los costos
                            costoProductosTextView.setText(String.format("S/. %.2f", costoProductos));
                            precioDeliveryTextView.setText(String.format("S/. %.2f", precioDelivery));
                            pagoTotalTextView.setText(String.format("S/. %.2f", pagoTotal));
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener pedido", e));
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_restaurantes) {
                    startActivity(new Intent(DetallesPedidoActivity.this, InicioClienteActivity.class));
                    return true;
                } else if (id == R.id.nav_carrito) {
                    startActivity(new Intent(DetallesPedidoActivity.this, CarritoClienteActivity.class));
                    return true;
                } else if (id == R.id.navigation_ordenes) {
                    startActivity(new Intent(DetallesPedidoActivity.this, HistorialPedidosActivity.class));
                    return true;
                } else if (id == R.id.nav_perfil) {
                    startActivity(new Intent(DetallesPedidoActivity.this, PerfilClienteActivity.class));
                    return true;
                }

                return false;
            }
        });
    }


    private String obtenerEstadoPedido(int estado) {
        switch (estado) {
            case 4:
                return "Orden entregada";
            case 5:
                return "Orden rechazada";
            case 6:
                return "Orden cancelada";
            default:
                return "Estado desconocido";
        }
    }

}
