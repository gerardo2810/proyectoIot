package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.RecyclerView.Producto;
import com.example.proyecto_iot.cliente.RecyclerView.ProductoCarritoAdapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CarritoClienteActivity extends AppCompatActivity implements ProductoCarritoAdapter.OnProductUpdateListener {

    private RecyclerView recyclerViewCarrito;
    private ProductoCarritoAdapter productoCarritoAdapter;
    private List<Producto> productos;
    private TextView subtotalTextView;
    private Button payButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito_cliente);

        recyclerViewCarrito = findViewById(R.id.recycler_carrito);
        recyclerViewCarrito.setLayoutManager(new LinearLayoutManager(this));

        productos = new ArrayList<>();
        productos.add(new Producto("Producto 1", "Descripción del Producto 1", 15.00,1));
        productos.add(new Producto("Producto 2", "Descripción del Producto 2", 25.00,1));
        productos.add(new Producto("Producto 3", "Descripción del Producto 3", 10.00,1));

        productoCarritoAdapter = new ProductoCarritoAdapter(productos, this);
        recyclerViewCarrito.setAdapter(productoCarritoAdapter);

        subtotalTextView = findViewById(R.id.subtotal_value);
        payButton = findViewById(R.id.pay_button);

        updateSubtotal();

        // Listener para vaciar el carrito
        TextView clearCart = findViewById(R.id.clear_cart);
        clearCart.setOnClickListener(v -> {
            productos.clear();
            productoCarritoAdapter.notifyDataSetChanged();
            updateSubtotal();
        });
    }

    // Actualiza el subtotal
    private void updateSubtotal() {
        double subtotal = 0.0;
        for (Producto producto : productos) {
            subtotal += producto.getTotal();
        }
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "PE"));
        subtotalTextView.setText(currencyFormat.format(subtotal));
    }

    // Este método se llama cada vez que se actualiza un producto
    @Override
    public void onProductUpdated() {
        updateSubtotal();
    }
}
