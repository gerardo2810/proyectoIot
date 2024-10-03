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
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Categoria;
import com.example.proyecto_iot.cliente.RecyclerView.CategoriaAdapter;
import com.example.proyecto_iot.cliente.RecyclerView.Restaurante;
import com.example.proyecto_iot.cliente.RecyclerView.RestauranteAdapter;

import java.util.ArrayList;
import java.util.List;

public class InicioClienteActivity extends AppCompatActivity {
    private RecyclerView recyclerBestOption;
    private RestauranteAdapter bestOptionAdapter;

    private RecyclerView recyclerCategories;
    private RecyclerView recyclerFavoritos;
    private RecyclerView recyclerPopulares;
    private com.example.proyecto_iot.cliente.RecyclerView.CategoriaAdapter categoryAdapter;
    private RestauranteAdapter popularesAdapter;
    private RestauranteAdapter favoritosAdapter;

    private List<Restaurante> bestOptionList;
    private List<Restaurante> popularesList;
    private List<Restaurante> favoritosList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_cliente);


        // Inicializar RecyclerView de categorías
        recyclerCategories = findViewById(R.id.recycler_categories);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerCategories.setLayoutManager(layoutManager);

        // Asignar adaptador para las categorías
        categoryAdapter = new CategoriaAdapter(getCategoryList(), this);
        recyclerCategories.setAdapter(categoryAdapter);


        // Inicializar RecyclerView de favoritos
        recyclerFavoritos = findViewById(R.id.recycler_favoritos);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerFavoritos.setLayoutManager(layoutManager1);

        // Asignar adaptador para las favoritos
        favoritosAdapter = new RestauranteAdapter(this, getFavoritosList());
        recyclerFavoritos.setAdapter(favoritosAdapter);


        // Inicializar RecyclerView de populares
        recyclerPopulares = findViewById(R.id.recycler_populares);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerPopulares.setLayoutManager(layoutManager2);

        // Asignar adaptador para las populares
        popularesAdapter = new RestauranteAdapter(this, getPopularesList());
        recyclerPopulares.setAdapter(popularesAdapter);


        // Inicializar RecyclerView
        recyclerBestOption = findViewById(R.id.recycler_best_option);
        recyclerBestOption.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Inicializar lista de opciones y adaptador
        bestOptionList = new ArrayList<>();
        bestOptionList.add(new Restaurante("La Lucha", 3.49, "Desayunos", "San Miguel",R.drawable.chinawok_portada));
        bestOptionList.add(new Restaurante("Pinkberry", 3.49, "Heladería", "San Miguel",R.drawable.chinawok_portada));

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


    // Método para obtener la lista de categorías
    private List<Categoria> getCategoryList() {
        List<Categoria> categoryList = new ArrayList<>();
        categoryList.add(new Categoria("Pollo", R.drawable.chicken));
        categoryList.add(new Categoria("Carnes", R.drawable.carne));
        categoryList.add(new Categoria("Hamburguesas", R.drawable.hamburguesa));
        categoryList.add(new Categoria("Pizzas", R.drawable.pizza));
        categoryList.add(new Categoria("Pastas", R.drawable.pasta));
        categoryList.add(new Categoria("Sushi", R.drawable.makis));
        categoryList.add(new Categoria("Postres", R.drawable.postre));
        categoryList.add(new Categoria("Helados", R.drawable.helados));
        categoryList.add(new Categoria("Jugos", R.drawable.jugo));
        categoryList.add(new Categoria("Bowls", R.drawable.bowls));
        return categoryList;
    }


    private List<Restaurante> getFavoritosList() {
        List<Restaurante> favoritosList = new ArrayList<>();
        favoritosList.add(new Restaurante("La Lucha", 3.49, "Desayunos", "San Miguel",R.drawable.lalucha));
        favoritosList.add(new Restaurante("Pinkberry", 3.49, "Heladería", "San Miguel",R.drawable.pinkberry));
        favoritosList.add(new Restaurante("Taco Bell", 2.50, "Comida mexicana", "San Miguel",R.drawable.tacobell));
        favoritosList.add(new Restaurante("Popeyes", 1.20, "Pollos", "San Miguel",R.drawable.popeyes));
        favoritosList.add(new Restaurante("EDO Sushi Bar", 3.49, "Sushi", "San Miguel",R.drawable.edo));
        favoritosList.add(new Restaurante("Mediterráneo", 3.49, "Pollos", "San Miguel",R.drawable.mediterraneo));
        return favoritosList;
    }


    private List<Restaurante> getPopularesList() {
        List<Restaurante> popularesList = new ArrayList<>();
        popularesList.add(new Restaurante("Papa John's", 3.49, "Pizzas", "San Miguel",R.drawable.papajhons));
        popularesList.add(new Restaurante("Bembos", 1.39, "Hamburguesas", "San Miguel",R.drawable.bembos));
        popularesList.add(new Restaurante("Chinawok", 2.39, "Chifa", "San Miguel",R.drawable.chinawok));
        popularesList.add(new Restaurante("Don Belisario", 4.49, "Pollos", "San Miguel",R.drawable.donbelisario));
        popularesList.add(new Restaurante("Santoku Sushi Bar", 2.79, "Sushi", "San Miguel",R.drawable.santoku));
        popularesList.add(new Restaurante("Wing House", 1.49, "Alitas", "San Miguel",R.drawable.wings));
        return popularesList;
    }


}
