package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

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

public class TodosLosProductosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductoDetallesAdapter adapter;
    private List<Producto> productoList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todos_los_productos_cliente);

        // Inicializar Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recycler_productos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar la lista de productos
        productoList = new ArrayList<>();

        // Configurar el adaptador
        adapter = new ProductoDetallesAdapter(this, productoList);
        recyclerView.setAdapter(adapter);

        // Cargar los productos desde Firebase
        fetchProductosFromFirebase();

        // Configurar la flecha de retroceso
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a la vista de Historial de Pedidos
                Intent intent = new Intent(TodosLosProductosActivity.this, DetallesPedidoActivity.class);
                startActivity(intent);
                finish(); // Finaliza la actividad actual para no volver a ella con el botón de retroceso
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_restaurantes) {
                    startActivity(new Intent(TodosLosProductosActivity.this, InicioClienteActivity.class));
                    return true;
                } else if (id == R.id.nav_carrito) {
                    startActivity(new Intent(TodosLosProductosActivity.this, CarritoClienteActivity.class));
                    return true;
                } else if (id == R.id.navigation_ordenes) {
                    startActivity(new Intent(TodosLosProductosActivity.this, HistorialPedidosActivity.class));
                    return true;
                } else if (id == R.id.nav_perfil) {
                    startActivity(new Intent(TodosLosProductosActivity.this, PerfilClienteActivity.class));
                    return true;
                }

                return false;
            }
        });
    }

    // Método para obtener productos desde Firebase Firestore
    private void fetchProductosFromFirebase() {
        db.collection("platos") // Asegúrate de que esta sea tu colección de productos en Firebase
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        productoList.clear(); // Limpiar la lista antes de agregar nuevos datos
                        for (DocumentSnapshot document : task.getResult()) {
                            // Extraer los datos del documento
                            String id =document.getId();

                            String nombre = document.getString("nombre");
                            String descripcion = document.getString("descripcion");
                            double precio = document.contains("precio") ? document.getDouble("precio") : 0.0;
                            String imageUrl = document.getString("imagen"); // Supone que tienes un campo para la URL de la imagen
                            int cantidad = 1; // Valor inicial de la cantidad

                            // Agregar el producto a la lista
                            productoList.add(new Producto(id,nombre, descripcion, precio, cantidad, imageUrl));
                        }
                        adapter.notifyDataSetChanged(); // Actualizar el adaptador con los nuevos datos
                    } else {
                        Log.e("Firestore", "Error al obtener productos", task.getException());
                    }
                });
    }
}
