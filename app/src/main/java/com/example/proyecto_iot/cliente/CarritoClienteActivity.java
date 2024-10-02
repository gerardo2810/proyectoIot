package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.RecyclerView.Producto;
import com.example.proyecto_iot.cliente.RecyclerView.ProductoCarritoAdapter;
import java.util.ArrayList;
import java.util.List;

public class CarritoClienteActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCarrito;
    private ProductoCarritoAdapter productoCarritoAdapter;
    private List<Producto> productos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito_cliente);

        recyclerViewCarrito = findViewById(R.id.recycler_carrito);
        recyclerViewCarrito.setLayoutManager(new LinearLayoutManager(this));

        productos = new ArrayList<>();
        productos.add(new Producto("La Lucha", "Descripción del Producto 1", 15.00));
        productos.add(new Producto("Papa Jhons", "Descripción del Producto 2", 25.00));
        productos.add(new Producto("Chifa", "Descripción del Producto 3", 10.00));

        productoCarritoAdapter = new ProductoCarritoAdapter(productos);
        recyclerViewCarrito.setAdapter(productoCarritoAdapter);

        Button payButton = findViewById(R.id.pay_button);
        payButton.setOnClickListener(view -> {
            // Acción para pagar
        });
    }
}
