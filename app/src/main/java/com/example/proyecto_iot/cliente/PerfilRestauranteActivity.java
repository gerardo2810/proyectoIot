package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.RecyclerView.Producto;
import com.example.proyecto_iot.cliente.RecyclerView.ProductoAdapter;

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
        productos.add(new Producto("Pavo a la leña", "Con tártara de la casa", 15.00,1));
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

        // Listener para el carrito en el menú inferior
        ImageView cartIcon = findViewById(R.id.shopping_cart);
        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PerfilRestauranteActivity.this, CarritoClienteActivity.class);
                startActivity(intent);
            }
        });



        // Listeners para la barra de navegación
        // Icono de restaurantes
        LinearLayout iconoRestaurantes = findViewById(R.id.nav_restaurantes);
        iconoRestaurantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PerfilRestauranteActivity.this, InicioClienteActivity.class);
                startActivity(intent);
            }
        });

        // Icono del carrito en la barra de navegación
        LinearLayout iconoCarritoNav = findViewById(R.id.nav_carrito);
        iconoCarritoNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PerfilRestauranteActivity.this, CarritoClienteActivity.class);
                startActivity(intent);
            }
        });

        // Icono de perfil en la barra de navegación
        LinearLayout iconoPerfilNav = findViewById(R.id.nav_perfil);
        iconoPerfilNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PerfilRestauranteActivity.this, PerfilClienteActivity.class);
                startActivity(intent);
            }
        });
    }


}
