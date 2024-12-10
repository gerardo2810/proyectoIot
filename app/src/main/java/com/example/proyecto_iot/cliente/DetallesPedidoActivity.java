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

        // Inicializar la lista de productos
        productoList = new ArrayList<>();

        // Configurar el adaptador
        adapter = new ProductoDetallesAdapter(this,productoList);
        recyclerView.setAdapter(adapter);

        // Cargar productos desde Firebase
        fetchProductosFromFirebase();

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

    private void fetchProductosFromFirebase() {
        db.collection("platos").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        productoList.clear(); // Limpiar la lista antes de agregar nuevos elementos
                        for (DocumentSnapshot document : task.getResult()) {
                            try {
                                // Obtener los valores desde Firestore
                                String id =document.getId();

                                String nombre = document.getString("Nombre");
                                String descripcion = document.getString("Descripcion");
                                String imageUrl = document.getString("Imagen");

                                // Obtener `Precio` como Double directamente
                                double precio = document.contains("Precio") ? document.getDouble("Precio") : 0.0;

                                // Obtener `cantidadDeVentas` como Long y convertirlo a int
                                int cantidad = document.contains("cantidadDeVentas") ?
                                        document.getLong("cantidadDeVentas").intValue() : 0;

                                // Agregar el producto a la lista
                                productoList.add(new Producto(id,nombre, descripcion, precio, cantidad, imageUrl));
                            } catch (Exception e) {
                                Log.e("Firestore", "Error al procesar producto", e);
                            }
                        }
                        adapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
                    } else {
                        Log.e("Firestore", "Error al obtener productos", task.getException());
                    }
                });
    }
}
