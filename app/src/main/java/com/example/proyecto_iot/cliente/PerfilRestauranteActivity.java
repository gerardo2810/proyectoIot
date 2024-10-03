package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_restaurante_cliente);
        RecyclerView recyclerProductos = findViewById(R.id.recycler_perfil_restaurante);
        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));

        List<Producto> productos = new ArrayList<>();
        productos.add(new Producto("Pavo a la leña", "Con tártara de la casa", 15.00,1,R.drawable.lalucha_inicio));
        // Añadir más productos aquí

        ProductoAdapter adapter = new ProductoAdapter(productos);
        recyclerProductos.setAdapter(adapter);

        // Listener para el botón de retroceso que regresa a inicio_cliente
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PerfilRestauranteActivity.this, InicioClienteActivity.class);
                startActivity(intent);
                finish(); // Cierra la actividad actual
            }
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
