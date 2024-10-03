package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.RecyclerView.Producto;
import com.example.proyecto_iot.cliente.RecyclerView.ProductoCarritoAdapter;
import java.util.ArrayList;
import java.util.List;

public class VerMasProductosClienteActivity extends AppCompatActivity implements ProductoCarritoAdapter.OnProductUpdateListener {

    private RecyclerView recyclerView;
    private ProductoCarritoAdapter adapter;
    private List<Producto> productoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_mas_productos_cliente);

        // Inicializar el RecyclerView
        recyclerView = findViewById(R.id.recycler_carrito);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar la lista de productos
        productoList = new ArrayList<>();
        productoList.add(new Producto("Pavo a la leña", "Con tártara de la casa", 15.00, 1,R.drawable.lalucha_inicio));
        productoList.add(new Producto("Pollo a la brasa", "Acompañado de papas fritas", 20.00, 2,R.drawable.lalucha_inicio));
        productoList.add(new Producto("Hamburguesa", "Con papas y gaseosa", 12.00, 1,R.drawable.lalucha_inicio));

        // Configurar el adaptador
        adapter = new ProductoCarritoAdapter(productoList, this);
        recyclerView.setAdapter(adapter);


// Configurar la flecha de retroceso
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a la vista de Historial de Pedidos
                Intent intent = new Intent(VerMasProductosClienteActivity.this, RealizarPedidoActivity.class);
                startActivity(intent);
                finish(); // Finaliza la actividad actual para no volver a ella con el botón de retroceso
            }
        });

    }

    @Override
    public void onProductUpdated() {
        // Aquí puedes actualizar la UI cuando un producto se actualice, por ejemplo, recalcular el total del carrito
    }
}
