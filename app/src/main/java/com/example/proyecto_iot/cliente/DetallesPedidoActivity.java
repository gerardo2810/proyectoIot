package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

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
        List<Producto> productos = new ArrayList<>();

        // Obtener productos desde el Intent
        ArrayList<HashMap<String, Object>> productosData =
                (ArrayList<HashMap<String, Object>>) intent.getSerializableExtra("productos");

        // Convertir los datos del Intent a una lista de objetos Producto
        if (productosData != null) {
            for (HashMap<String, Object> productoMap : productosData) {
                String id = (String) productoMap.get("id");
                String nombre = (String) productoMap.get("nombre");
                String descripcion = (String) productoMap.get("descripcion");
                double precio = productoMap.containsKey("precio") ?
                        ((Number) productoMap.get("precio")).doubleValue() : 0.0;
                int cantidad = productoMap.containsKey("cantidad") ?
                        ((Number) productoMap.get("cantidad")).intValue() : 0;
                String imageUrl = (String) productoMap.get("imageUrl");


                productos.add(new Producto(id, nombre, descripcion, precio, cantidad, imageUrl));
            }
        }

        // Calcular el costo total de los productos
        final double[] costoProductos1 = {0.0};
        if (productosData != null) {
            for (HashMap<String, Object> productoMap : productosData) {
                double precio = productoMap.containsKey("precio") ?
                        ((Number) productoMap.get("precio")).doubleValue() : 0.0;
                int cantidad = productoMap.containsKey("cantidad") ?
                        ((Number) productoMap.get("cantidad")).intValue() : 0;

                // Multiplicar precio por cantidad y sumar al total
                costoProductos1[0] += precio * cantidad;
            }
        }


        // Pasar la lista convertida al adaptador
        adapter = new ProductoDetallesAdapter(this, productos);
        recyclerView.setAdapter(adapter);
        String idRestaurante = intent.getStringExtra("idRestaurante");
        System.out.println("DETALLES PEIDDO "+  idRestaurante);

        // Use a final array to hold the direccionRestaurante
        final String[] direccionRestauranteHolder = {""};

        // Consultar Firestore para obtener la dirección del restaurante
        if (idRestaurante != null) {
            db.collection("restaurantes").document(idRestaurante)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            direccionRestauranteHolder[0] = documentSnapshot.getString("ubicacion");
                            // Combinar nombre del restaurante y dirección
                            if (nombreRestaurante != null && direccionRestauranteHolder[0] != null) {
                                orderTitleTextView.setText(String.format("%s - %s", nombreRestaurante, direccionRestauranteHolder[0]));
                            } else {
                                orderTitleTextView.setText(nombreRestaurante != null ? nombreRestaurante : "Nombre desconocido");
                            }
                        } else {
                            orderTitleTextView.setText(nombreRestaurante != null ? nombreRestaurante : "Nombre desconocido");
                            Log.e("Firestore", "Restaurante no encontrado.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error al obtener restaurante", e);
                        orderTitleTextView.setText(nombreRestaurante != null ? nombreRestaurante : "Nombre desconocido");
                    });
        } else {
            orderTitleTextView.setText(nombreRestaurante != null ? nombreRestaurante : "Nombre desconocido");
        }

        // Asignar estado
        estadoTextView.setText(obtenerEstadoPedido(estado)); // Setea el texto del estado
        estadoTextView.setTextColor(obtenerColorEstado(estado)); // Cambia dinámicamente el color

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

                // Pasar la lista de productos
                ArrayList<HashMap<String, Object>> productosData = new ArrayList<>();
                for (Producto producto : productos) {
                    HashMap<String, Object> productoMap = new HashMap<>();
                    productoMap.put("id", producto.getId());
                    productoMap.put("nombre", producto.getNombre());
                    productoMap.put("descripcion", producto.getDescripcion());
                    productoMap.put("precio", producto.getPrecio());
                    productoMap.put("cantidad", producto.getCantidad());
                    productoMap.put("imageUrl", producto.getImageUrl());
                    productosData.add(productoMap);
                }
                intent.putExtra("productos", productosData);

                // Pasar el estado
                intent.putExtra("estado", estado);

                startActivity(intent);
            }
        });

        // Referencias a los TextViews
        TextView direccionTextView = findViewById(R.id.direccion);
        TextView repartidorTextView = findViewById(R.id.repartidor);
        TextView costoProductosTextView = findViewById(R.id.costoProductos);
        TextView precioDeliveryTextView = findViewById(R.id.precioDelivery);
        TextView pagoTotalTextView = findViewById(R.id.pagoTotal);
        // Mostrar el costo total de los productos
        costoProductosTextView.setText(String.format("S/. %.2f", costoProductos1[0]));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("restaurantes").document(idRestaurante).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Obtener el precio del delivery
                        double precioDelivery = documentSnapshot.contains("precioDelivery") ?
                                documentSnapshot.getDouble("precioDelivery") : 0.0;
                        System.out.println("Precio Delivery" + precioDelivery);

                        // Mostrar el precio del delivery
                        precioDeliveryTextView.setText(String.format("S/. %.2f", precioDelivery));

                        // Calcular y mostrar el pago total
                        double pagoTotal = costoProductos1[0] + precioDelivery;
                        pagoTotalTextView.setText(String.format("S/. %.2f", pagoTotal));
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al obtener datos del restaurante", Toast.LENGTH_SHORT).show();
                });

        // Obtener datos del Intent
        String pedidoId = intent.getStringExtra("idPedido");
        if (pedidoId != null) {
            db.collection("pedidos").document(pedidoId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String idRepartidor = documentSnapshot.getString("idRepartidor");

                            direccionTextView.setText(direccion != null ? direccion : "Sin dirección");

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

                        }
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener pedido", e));
        }


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
    // Método para asignar un color dependiendo del estado
    private int obtenerColorEstado(int estado) {
        // Verifica el estado y retorna el color correspondiente
        switch (estado) {
            case 4: // Verde para estado 4
                return Color.parseColor("#00B050");
            default: // Rojo para cualquier otro estado
                return Color.parseColor("#FF0000");
        }
    }

}
