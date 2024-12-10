package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.RecyclerView.Producto;
import com.example.proyecto_iot.cliente.RecyclerView.ProductoAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PerfilRestauranteActivity extends AppCompatActivity {

    private ProductoAdapter adapter;
    private List<Producto> productosList = new ArrayList<>();
    private List<Producto> carritoList = new ArrayList<>();
    private TextView carritoCantidadTextView;
    private ImageView carritoIcon;
    private int totalCantidadCarrito = 0;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_restaurante_cliente);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Obtener los datos del Intent
        Intent intent = getIntent();
        String nombreRestaurante = intent.getStringExtra("nombre_restaurante");
        String categoriaRestaurante = intent.getStringExtra("categoria_restaurante");
        double precioDelivery = intent.getDoubleExtra("precio_delivery", 0.0);
        String direccionRestaurante = intent.getStringExtra("direccion_restaurante");
        String fotoLogo = intent.getStringExtra("foto_logo");
        String fotoPortada = intent.getStringExtra("foto_portada");

        String restauranteId = intent.getStringExtra("restauranteId"); // Recibir el ID del restaurante
        System.out.println("perfil resta");
        System.out.println(precioDelivery);

        // Configurar los TextViews con los datos recibidos
        TextView nombreTextView = findViewById(R.id.restaurant_name);
        TextView categoriaTextView = findViewById(R.id.restaurant_category);
        TextView deliveryTextView = findViewById(R.id.delivery_price);
        TextView direccionTextView = findViewById(R.id.restaurant_address);

        nombreTextView.setText(nombreRestaurante);
        categoriaTextView.setText(categoriaRestaurante);
        deliveryTextView.setText(String.format("S/. %.2f", precioDelivery));
        direccionTextView.setText(direccionRestaurante);

        // Configurar el RecyclerView
        RecyclerView recyclerProductos = findViewById(R.id.recycler_perfil_restaurante);
        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));

        // Crear el adaptador
        adapter = new ProductoAdapter(this, productosList, this::agregarProductoAlCarrito);
        recyclerProductos.setAdapter(adapter);

        // Inicializar el TextView y el icono del carrito
        carritoCantidadTextView = findViewById(R.id.cart_count);
        carritoIcon = findViewById(R.id.shopping_cart);
        carritoIcon.setVisibility(View.GONE);

        carritoIcon.setOnClickListener(view -> {
            Intent intent1 = new Intent(PerfilRestauranteActivity.this, CarritoClienteActivity.class);

            // Pasar la lista de productos del carrito
            intent1.putExtra("carrito", new ArrayList<>(carritoList));

            // Pasar los datos del restaurante
            intent1.putExtra("nombre_restaurante", nombreRestaurante);
            intent1.putExtra("fotoLogo", fotoLogo);
            intent1.putExtra("precioDelivery", precioDelivery);
            intent1.putExtra("restauranteId", restauranteId); // Pasar el ID del restaurante

            // Iniciar la actividad del carrito
            startActivity(intent1);
        });


        EditText searchText = findViewById(R.id.search_text);

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.filterList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(view -> {
            Intent intent2 = new Intent(PerfilRestauranteActivity.this, InicioClienteActivity.class);
            startActivity(intent2);
            finish();
        });

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
                    intent.putExtra("carrito", new ArrayList<>(carritoList));
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

        fetchProductosFromFirebase();

    }

    private void fetchProductosFromFirebase() {
        // Obtener el ID del restaurante del Intent
        String restauranteId = getIntent().getStringExtra("restauranteId");

        if (restauranteId == null || restauranteId.isEmpty()) {
            Toast.makeText(this, "Error: Restaurante no válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Paso 1: Obtener las categorías asociadas al restaurante
        db.collection("categorias")
                .whereEqualTo("idRestaurante", restauranteId) // Filtrar por el ID del restaurante
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<String> categoriaIds = new ArrayList<>();

                        // Extraer los IDs de las categorías
                        for (DocumentSnapshot document : task.getResult()) {
                            categoriaIds.add(document.getId());
                        }

                        if (!categoriaIds.isEmpty()) {
                            // Paso 2: Obtener los platos asociados a las categorías
                            fetchPlatosPorCategorias(categoriaIds);
                        } else {
                            Toast.makeText(this, "No hay platos para este restaurante.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Error al cargar categorías.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchPlatosPorCategorias(List<String> categoriaIds) {
        // Consultar los platos que pertenecen a las categorías
        db.collection("platos")
                .whereIn("idCategoria", categoriaIds) // Filtrar por las categorías obtenidas
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        productosList.clear();

                        for (DocumentSnapshot document : task.getResult()) {
                            String id =document.getId();
                            String nombre = document.getString("Nombre");
                            String descripcion = document.getString("Descripcion");
                            double precio = document.contains("Precio") ? document.getDouble("Precio") : 0.0;
                            String imageUrl = document.getString("Imagen");
                            int cantidadInicial = 1;

                            productosList.add(new Producto(id,nombre, descripcion, precio, cantidadInicial, imageUrl));
                        }

                        // Notificar al adaptador de los cambios
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error al cargar platos.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void agregarProductoAlCarrito(Producto producto, int cantidad) {
        boolean productoExistente = false;
        for (Producto p : carritoList) {
            if (p.getNombre().equals(producto.getNombre())) {
                p.setCantidad(p.getCantidad() + cantidad);
                productoExistente = true;
                break;
            }
        }

        if (!productoExistente) {
            producto.setCantidad(cantidad);
            carritoList.add(producto);
        }

        totalCantidadCarrito += cantidad;
        actualizarCarritoCantidad();
    }

    private void actualizarCarritoCantidad() {
        if (totalCantidadCarrito > 0) {
            carritoIcon.setVisibility(View.VISIBLE);
            carritoCantidadTextView.setVisibility(View.VISIBLE);
            carritoCantidadTextView.setText(String.valueOf(totalCantidadCarrito));
        } else {
            carritoIcon.setVisibility(View.GONE);
            carritoCantidadTextView.setVisibility(View.GONE);
        }
    }
}
