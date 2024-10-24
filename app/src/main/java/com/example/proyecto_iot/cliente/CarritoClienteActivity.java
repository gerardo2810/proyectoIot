package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.RecyclerView.Producto;
import com.example.proyecto_iot.cliente.RecyclerView.ProductoCarritoAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

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


        // Recibir productos del carrito desde PerfilRestauranteActivity
        productos = (List<Producto>) getIntent().getSerializableExtra("carrito");

        // Si no hay productos en el carrito, inicializamos la lista vacía
        if (productos == null) {
            productos = new ArrayList<>();
        }

        productoCarritoAdapter = new ProductoCarritoAdapter(productos, this);
        recyclerViewCarrito.setAdapter(productoCarritoAdapter);

        subtotalTextView = findViewById(R.id.subtotal_value);
        payButton = findViewById(R.id.pay_button);

        updateSubtotal();

        // Listener para vaciar el carrito
        TextView clearCart = findViewById(R.id.clear_cart);
        clearCart.setOnClickListener(v -> {
            productos.clear();  // Limpiar lista de productos
            productoCarritoAdapter.notifyDataSetChanged();  // Actualizar el RecyclerView
            updateSubtotal();  // Actualizar subtotal a 0
            payButton.setVisibility(View.GONE);  // Ocultar el botón de pago
        });

        // Configurar la flecha de retroceso
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a la vista de PerfilRestauranteActivity
                Intent intent = new Intent(CarritoClienteActivity.this, PerfilRestauranteActivity.class);
                startActivity(intent);
                finish(); // Finaliza la actividad actual
            }
        });

        // Configurar el botón "Ir a pagar"
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Almacenar la lista de productos seleccionados en el Singleton
                CarritoSingleton.getInstance().setProductos(new ArrayList<>(productos));

                // Navegar a la actividad RealizarPedidoActivity
                Intent intent = new Intent(CarritoClienteActivity.this, RealizarPedidoActivity.class);
                startActivity(intent);
            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_restaurantes) {
                    startActivity(new Intent(CarritoClienteActivity.this, InicioClienteActivity.class));
                    return true;
                } else if (id == R.id.nav_carrito) {
                    return true;
                } else if (id == R.id.navigation_ordenes) {
                    startActivity(new Intent(CarritoClienteActivity.this, HistorialPedidosActivity.class));
                    return true;
                } else if (id == R.id.nav_perfil) {
                    startActivity(new Intent(CarritoClienteActivity.this, PerfilClienteActivity.class));
                    return true;
                }

                return false;
            }
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

        // Ocultar el botón "Ir a pagar" si el carrito está vacío
        if (subtotal == 0) {
            payButton.setVisibility(View.GONE);
        } else {
            payButton.setVisibility(View.VISIBLE);
        }
    }

    // Este método se llama cada vez que se actualiza un producto
    @Override
    public void onProductUpdated() {
        updateSubtotal();  // Recalcular el subtotal cuando un producto cambie
    }
}
