package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.RecyclerView.Restaurante;
import com.example.proyecto_iot.cliente.RecyclerView.RestauranteAdapter;

import java.util.ArrayList;
import java.util.List;

public class InicioClienteActivity extends AppCompatActivity {
    private RecyclerView recyclerBestOption;
    private RestauranteAdapter bestOptionAdapter;
    private List<Restaurante> bestOptionList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_cliente);

        // Inicializar RecyclerView
        recyclerBestOption = findViewById(R.id.recycler_best_option);
        recyclerBestOption.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar lista de opciones y adaptador
        bestOptionList = new ArrayList<>();
        bestOptionList.add(new Restaurante("La Lucha", 3.49, "Desayunos", "San Miguel",R.drawable.lalucha_inicio));
        bestOptionList.add(new Restaurante("Pinkberry", 3.49, "Heladería", "San Miguel",R.drawable.lalucha_inicio));

        bestOptionAdapter = new RestauranteAdapter(this, bestOptionList);
        recyclerBestOption.setAdapter(bestOptionAdapter);

        // Listener para el ícono de perfil en el header
        ImageView iconoPerfil = findViewById(R.id.icono_perfil);
        iconoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InicioClienteActivity.this, PerfilClienteActivity.class);
                startActivity(intent);
            }
        });

        // Listener para el ícono del carrito en el header
        ImageView iconoCarrito = findViewById(R.id.icono_carrito);
        iconoCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InicioClienteActivity.this, CarritoClienteActivity.class);
                startActivity(intent);
            }
        });


        // Listener para las categorías en el carrusel
        ImageView categoriaPolleria = findViewById(R.id.categoria_pollerias);
        categoriaPolleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InicioClienteActivity.this, ListaRestaurantesCategoriasClienteActivity.class);
                startActivity(intent);
            }
        });


        // Listener para la sección de favoritos
        ImageView popularRestaurante12 = findViewById(R.id.favorite_one);
        popularRestaurante12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InicioClienteActivity.this, PerfilRestauranteActivity.class);
                startActivity(intent);
            }
        });



        // Listener para la sección de los más populares
        ImageView popularRestaurante1 = findViewById(R.id.most_favorite_one);
        popularRestaurante1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InicioClienteActivity.this, PerfilRestauranteActivity.class);
                startActivity(intent);
            }
        });


        // Listeners para la barra de navegación
        // Icono de restaurantes (irá a la misma vista de inicio)
        LinearLayout iconoRestaurantes = findViewById(R.id.nav_restaurantes);
        iconoRestaurantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InicioClienteActivity.this, InicioClienteActivity.class);
                startActivity(intent);
            }
        });

        // Icono del carrito en la barra de navegación
        LinearLayout iconoCarritoNav = findViewById(R.id.nav_carrito);
        iconoCarritoNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InicioClienteActivity.this, CarritoClienteActivity.class);
                startActivity(intent);
            }
        });

        // Icono de perfil en la barra de navegación
        LinearLayout iconoPerfilNav = findViewById(R.id.nav_perfil);
        iconoPerfilNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InicioClienteActivity.this, PerfilClienteActivity.class);
                startActivity(intent);
            }
        });
    }
}
