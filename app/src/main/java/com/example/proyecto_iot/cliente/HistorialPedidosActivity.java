package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.RecyclerView.HistorialPedidosAdapter;
import com.example.proyecto_iot.cliente.RecyclerView.Pedido;
import com.example.proyecto_iot.cliente.RecyclerView.Producto;
import com.example.proyecto_iot.cliente.RecyclerView.SpaceItemDecoration; // Asegúrate de importar esta clase
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HistorialPedidosActivity extends AppCompatActivity {

    private RecyclerView recyclerViewHistorialPedidos;
    private HistorialPedidosAdapter adapter;
    private List<Pedido> listaPedidos;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_pedidos_cliente);

        // Inicializar RecyclerView
        recyclerViewHistorialPedidos = findViewById(R.id.recycler_orders);
        recyclerViewHistorialPedidos.setLayoutManager(new LinearLayoutManager(this));

        // Configurar espacio entre elementos
        int spaceInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_space);
        recyclerViewHistorialPedidos.addItemDecoration(new SpaceItemDecoration(spaceInPixels));

        listaPedidos = new ArrayList<>();
        adapter = new HistorialPedidosAdapter(listaPedidos, this);
        recyclerViewHistorialPedidos.setAdapter(adapter);

        // Obtener pedidos desde Firebase
        fetchPedidosFromFirebase();

        // Configurar la flecha de retroceso
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(HistorialPedidosActivity.this, CarritoClienteActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void fetchPedidosFromFirebase() {
        String clienteId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // ID del cliente autenticado
        System.out.println(clienteId);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pedidos")
                .whereEqualTo("idCliente", clienteId) // Filtrar pedidos por idCliente
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        listaPedidos.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            // Obtener los datos del pedido desde Firestore
                            String idPedido = document.getId();
                            String nombreRestaurante = document.getString("nombreRestaurante");
                            int estado = document.getLong("estado").intValue();
                            System.out.println("PEDIDO " + estado);
                            // Filtrar pedidos por estado (4, 5 o 6)
                            if (estado == 4 || estado == 5 || estado == 6) {
                                String fechaHora = document.getString("fechaHora");
                                String direccion = document.getString("direccion");
                                System.out.println("ENTRO" + fechaHora);
                                double pagoTotal = document.getDouble("pagoTotal");
                                String idRestaurante = document.getString("idRestaurante");
                                ArrayList<HashMap<String, Object>> productosData = (ArrayList<HashMap<String, Object>>) document.get("productos");

                                // Convertir productos
                                List<Producto> productos = new ArrayList<>();
                                if (productosData != null) {
                                    for (HashMap<String, Object> productoMap : productosData) {
                                        String id = (String) productoMap.get("id");
                                        String nombre = (String) productoMap.get("nombre");
                                        String descripcion = (String) productoMap.get("descripcion");
                                        double precio = productoMap.containsKey("precio") ? ((Number) productoMap.get("precio")).doubleValue() : 0.0;
                                        int cantidad = productoMap.containsKey("cantidad") ? ((Number) productoMap.get("cantidad")).intValue() : 0;
                                        String imageUrl = (String) productoMap.get("imageUrl"); // Obtener la URL de la imagen
                                        System.out.println("Historial PEDIDOS: " + imageUrl);
                                        productos.add(new Producto(id, nombre, descripcion, precio, cantidad, imageUrl));
                                    }
                                }

                                // Crear objeto Pedido y agregarlo a la lista
                                Pedido pedido = new Pedido(idPedido, idRestaurante, nombreRestaurante, estado, fechaHora, direccion, pagoTotal, productos);
                                listaPedidos.add(pedido);
                            }
                        }
                        adapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
                    } else {
                        Log.e("Firebase", "Error al obtener pedidos: ", task.getException());
                    }
                });
    }
}
