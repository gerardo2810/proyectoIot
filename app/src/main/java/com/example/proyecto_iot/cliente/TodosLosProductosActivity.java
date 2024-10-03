package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.RecyclerView.Producto;
import com.example.proyecto_iot.cliente.RecyclerView.ProductoDetallesAdapter;

import java.util.ArrayList;
import java.util.List;

public class TodosLosProductosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductoDetallesAdapter adapter;
    private List<Producto> productoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todos_los_productos_cliente);

        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recycler_productos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar la lista de productos
        productoList = new ArrayList<>();
        productoList.add(new Producto("Pavo a la leña", "Con tártara de la casa", 15.00, 27));
        productoList.add(new Producto("Pollo a la brasa", "Con papas fritas", 12.00, 15));
        productoList.add(new Producto("Pollo a la brasa", "Con papas fritas", 12.00, 15));
        productoList.add(new Producto("Pollo a la brasa", "Con papas fritas", 12.00, 15));
        productoList.add(new Producto("Pollo a la brasa", "Con papas fritas", 12.00, 15));
        productoList.add(new Producto("Pollo a la brasa", "Con papas fritas", 12.00, 15));
        productoList.add(new Producto("Pollo a la brasa", "Con papas fritas", 12.00, 15));
        productoList.add(new Producto("Pollo a la brasa", "Con papas fritas", 12.00, 15));
        productoList.add(new Producto("Pollo a la brasa", "Con papas fritas", 12.00, 15));

        // Configurar el adaptador
        adapter = new ProductoDetallesAdapter(productoList);
        recyclerView.setAdapter(adapter);


        // Configurar la flecha de retroceso
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a la vista de Historial de Pedidos
                Intent intent = new Intent(TodosLosProductosActivity.this, HistorialPedidosActivity.class);
                startActivity(intent);
                finish(); // Finaliza la actividad actual para no volver a ella con el botón de retroceso
            }
        });


    }
}