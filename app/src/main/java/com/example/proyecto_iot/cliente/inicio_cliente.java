package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyecto_iot.R;

public class inicio_cliente extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_cliente);

        // Listener para el ícono de perfil en el header
        ImageView iconoPerfil = findViewById(R.id.icono_perfil);
        iconoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(inicio_cliente.this, perfil_cliente.class);
                startActivity(intent);
            }
        });

        // Listener para el ícono del carrito en el header
        ImageView iconoCarrito = findViewById(R.id.icono_carrito);
        iconoCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(inicio_cliente.this, carrito_cliente.class);
                startActivity(intent);
            }
        });


        // Listener para las categorías en el carrusel
        ImageView categoriaPolleria = findViewById(R.id.categoria_pollerias);
        categoriaPolleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(inicio_cliente.this, lista_restaurantes_categorias_cliente.class);
                startActivity(intent);
            }
        });


        // Listener para la sección de favoritos
        ImageView popularRestaurante12 = findViewById(R.id.favorite_one);
        popularRestaurante12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(inicio_cliente.this, perfil_restaurante_cliente.class);
                startActivity(intent);
            }
        });



        // Listener para la sección de los más populares
        ImageView popularRestaurante1 = findViewById(R.id.most_favorite_one);
        popularRestaurante1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(inicio_cliente.this, perfil_restaurante_cliente.class);
                startActivity(intent);
            }
        });


        // Listeners para la barra de navegación
        // Icono de restaurantes (irá a la misma vista de inicio)
        LinearLayout iconoRestaurantes = findViewById(R.id.nav_restaurantes);
        iconoRestaurantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(inicio_cliente.this, inicio_cliente.class);
                startActivity(intent);
            }
        });

        // Icono del carrito en la barra de navegación
        LinearLayout iconoCarritoNav = findViewById(R.id.nav_carrito);
        iconoCarritoNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(inicio_cliente.this, carrito_cliente.class);
                startActivity(intent);
            }
        });

        // Icono de perfil en la barra de navegación
        LinearLayout iconoPerfilNav = findViewById(R.id.nav_perfil);
        iconoPerfilNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(inicio_cliente.this, perfil_cliente.class);
                startActivity(intent);
            }
        });
    }
}
