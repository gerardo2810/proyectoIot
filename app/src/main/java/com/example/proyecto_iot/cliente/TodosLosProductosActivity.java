package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.graphics.Color;
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
import com.example.proyecto_iot.cliente.RecyclerView.ProductoTodosAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TodosLosProductosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductoTodosAdapter adapter;
    private List<Producto> productoList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todos_los_productos_cliente);


        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recycler_productos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Recuperar la lista de productos del Intent
        ArrayList<HashMap<String, Object>> productosData =
                (ArrayList<HashMap<String, Object>>) getIntent().getSerializableExtra("productos");

        // Convertir la lista recibida en objetos Producto
        productoList = new ArrayList<>();
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

                productoList.add(new Producto(id, nombre, descripcion, precio, cantidad, imageUrl));
            }
        }

        // Configurar el adaptador
        adapter = new ProductoTodosAdapter(this, productoList);
        recyclerView.setAdapter(adapter);

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
        int estado = getIntent().getIntExtra("estado", -1);
        // Mostrar el estado en el TextView
        TextView estadoTextView = findViewById(R.id.estado);
        estadoTextView.setText(obtenerEstadoPedido(estado)); // Texto del estado
        estadoTextView.setTextColor(obtenerColorEstado(estado)); // Color del estado

        // Mostrar el tamaño de la lista en el TextView "see_more"
        TextView seeMoreTextView = findViewById(R.id.see_more);
        seeMoreTextView.setText(String.format("Total productos: %d", productoList.size()));


    }
    // Método para traducir el estado numérico a texto
    private String obtenerEstadoPedido(int estado) {
        switch (estado) {
            case 4:
                return "Entregado";
            case 5:
                return "Rechazado";
            case 6:
                return "Cancelado";
            default:
                return "Desconocido";
        }
    }

    // Método para asignar un color dependiendo del estado
    private int obtenerColorEstado(int estado) {
        switch (estado) {
            case 4: // Verde para entregado
                return Color.parseColor("#00B050");
            default: // Rojo para rechazado o cancelado
                return Color.parseColor("#FF0000");
        }
    }

}
