package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_restaurante_cliente);

        // Configurar el RecyclerView
        RecyclerView recyclerProductos = findViewById(R.id.recycler_perfil_restaurante);
        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));

        // Crear la lista de productos
        productosList = new ArrayList<>();
        productosList.add(new Producto("Pavo a la leña", "Con tártara de la casa", 15.00,1,R.drawable.lalucha_inicio));
        productosList.add(new Producto("Hamburguesa", "Con papas fritas", 10.00,1,R.drawable.lalucha_inicio));
        productosList.add(new Producto("Pizza", "Con salsa napolitana", 20.00,1,R.drawable.lalucha_inicio));
        // Añadir más productos aquí

        // Crear el adaptador y establecerlo en el RecyclerView
        adapter = new ProductoAdapter(productosList);
        recyclerProductos.setAdapter(adapter);

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
            Intent intent = new Intent(PerfilRestauranteActivity.this, InicioClienteActivity.class);
            startActivity(intent);
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
                    startActivity(new Intent(PerfilRestauranteActivity.this, CarritoClienteActivity.class));
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
}
