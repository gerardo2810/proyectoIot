package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView; // Importar para mostrar la cantidad en el icono del carrito

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.RecyclerView.Producto;
import com.example.proyecto_iot.cliente.RecyclerView.ProductoAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class PerfilRestauranteActivity extends AppCompatActivity {

    private ProductoAdapter adapter;
    private List<Producto> productosList;
    private List<Producto> carritoList = new ArrayList<>(); // Lista para almacenar productos añadidos al carrito
    private TextView carritoCantidadTextView; // Muestra la cantidad total en el icono del carrito
    private ImageView carritoIcon; // Icono del carrito
    private int totalCantidadCarrito = 0; // Contador de productos en el carrito

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_restaurante_cliente);

        // Obtener los datos del Intent
        Intent intent = getIntent();
        String nombreRestaurante = intent.getStringExtra("nombre_restaurante");
        String categoriaRestaurante = intent.getStringExtra("categoria_restaurante");
        double precioDelivery = intent.getDoubleExtra("precio_delivery", 0.0);
        String direccionRestaurante = intent.getStringExtra("direccion_restaurante");

        // Configurar los TextViews con los datos recibidos
        TextView nombreTextView = findViewById(R.id.restaurant_name);
        TextView categoriaTextView = findViewById(R.id.restaurant_category);
        TextView deliveryTextView = findViewById(R.id.delivery_price);
        TextView direccionTextView = findViewById(R.id.restaurant_address);

        // Asignar los valores a los TextViews
        nombreTextView.setText(nombreRestaurante);
        categoriaTextView.setText(categoriaRestaurante);
        deliveryTextView.setText(String.format("S/. %.2f", precioDelivery));
        direccionTextView.setText(direccionRestaurante);


        // Configurar el RecyclerView
        RecyclerView recyclerProductos = findViewById(R.id.recycler_perfil_restaurante);
        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));

        // Crear la lista de productos
        productosList = new ArrayList<>();
        productosList.add(new Producto("Pavo a la leña", "Con tártara de la casa", 15.00, 1, R.drawable.lalucha_inicio));
        productosList.add(new Producto("Hamburguesa", "Con papas fritas", 10.00, 1, R.drawable.lalucha_inicio));
        productosList.add(new Producto("Pizza", "Con salsa napolitana", 20.00, 1, R.drawable.lalucha_inicio));
        // Añadir más productos aquí

        // Crear el adaptador y establecerlo en el RecyclerView
        adapter = new ProductoAdapter(productosList, this::agregarProductoAlCarrito);
        recyclerProductos.setAdapter(adapter);

        // Inicializar el TextView que muestra la cantidad total en el carrito
        carritoCantidadTextView = findViewById(R.id.cart_count);

        // Inicializar el icono del carrito
        carritoIcon = findViewById(R.id.shopping_cart); // Asumimos que el id es 'shopping_cart' en el XML
        carritoIcon.setVisibility(View.GONE); // Ocultar inicialmente el icono del carrito

        // Listener para el icono del carrito, que abre la actividad del carrito
        carritoIcon.setOnClickListener(view -> {
            Intent intent1 = new Intent(PerfilRestauranteActivity.this, CarritoClienteActivity.class);
            intent1.putExtra("carrito", new ArrayList<>(carritoList)); // Pasar la lista del carrito
            startActivity(intent1);
        });

        // Buscar el campo de búsqueda
        EditText searchText = findViewById(R.id.search_text);

        // Añadir un TextWatcher para escuchar los cambios en el campo de búsqueda
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No se requiere implementar
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Filtrar la lista de productos cuando se cambia el texto
                adapter.filterList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No se requiere implementar
            }
        });

        // Listener para el botón de retroceso que regresa a inicio_cliente
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(view -> {
            Intent intent2 = new Intent(PerfilRestauranteActivity.this, InicioClienteActivity.class);
            startActivity(intent2);
            finish(); // Cierra la actividad actual
        });

        // Configurar el BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_restaurantes) {
                    startActivity(new Intent(PerfilRestauranteActivity.this, InicioClienteActivity.class));
                    return true;
                } else if (id == R.id.nav_carrito) {
                    Intent intent = new Intent(PerfilRestauranteActivity.this, CarritoClienteActivity.class);
                    intent.putExtra("carrito", new ArrayList<>(carritoList)); // Pasar la lista del carrito
                    startActivity(intent);
                    return true;
                } else if (id == R.id.navigation_ordenes) {
                    startActivity(new Intent(PerfilRestauranteActivity.this, HistorialPedidosActivity.class));
                    return true;
                } else if (id == R.id.nav_perfil) {
                    startActivity(new Intent(PerfilRestauranteActivity.this, PerfilClienteActivity.class));
                    return true;
                }

                return false;
            }
        });
    }

    // Método para agregar productos al carrito
    private void agregarProductoAlCarrito(Producto producto, int cantidad) {
        // Verificar si el producto ya está en el carrito
        boolean productoExistente = false;
        for (Producto p : carritoList) {
            if (p.getNombre().equals(producto.getNombre())) {
                // Si el producto ya está en el carrito, actualizar la cantidad
                p.setCantidad(p.getCantidad() + cantidad);
                productoExistente = true;
                break;
            }
        }

        // Si el producto no está en el carrito, añadirlo
        if (!productoExistente) {
            producto.setCantidad(cantidad); // Establecer la cantidad seleccionada
            carritoList.add(producto);
        }

        // Actualizar el contador total del carrito
        totalCantidadCarrito += cantidad;
        actualizarCarritoCantidad();
    }

    // Método para actualizar el TextView del carrito con la cantidad total y mostrar el icono
    private void actualizarCarritoCantidad() {
        if (totalCantidadCarrito > 0) {
            carritoIcon.setVisibility(View.VISIBLE); // Mostrar el icono del carrito
            carritoCantidadTextView.setVisibility(View.VISIBLE); // Mostrar el número en el círculo
            carritoCantidadTextView.setText(String.valueOf(totalCantidadCarrito));
        } else {
            carritoIcon.setVisibility(View.GONE); // Ocultar el icono del carrito si no hay productos
            carritoCantidadTextView.setVisibility(View.GONE); // Ocultar el número en el círculo
        }
    }
}
