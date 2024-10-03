package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Categoria;
import com.example.proyecto_iot.cliente.RecyclerView.CategoriaAdapter;
import com.example.proyecto_iot.cliente.RecyclerView.Restaurante;
import com.example.proyecto_iot.cliente.RecyclerView.RestauranteAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

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
        bestOptionList.add(new Restaurante("La Lucha", 3.49, "Desayunos", "San Miguel", R.drawable.mlalucha));
        bestOptionList.add(new Restaurante("Pinkberry", 3.49, "Heladería", "San Miguel", R.drawable.mpinkberry));

        bestOptionAdapter = new RestauranteAdapter(this, bestOptionList);
        recyclerBestOption.setAdapter(bestOptionAdapter);

        // Inicializar BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Marcar el ítem de "Restaurantes" como seleccionado
        bottomNavigationView.setSelectedItemId(R.id.nav_restaurantes);

        // Configurar listener para la navegación
            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();

                    if (id == R.id.nav_restaurantes) {
                        // Si ya está en la actividad de Restaurantes, no hacer nada
                        return true;
                    } else if (id == R.id.nav_carrito) {
                        startActivity(new Intent(InicioClienteActivity.this, CarritoClienteActivity.class));
                        return true;
                    } else if (id == R.id.navigation_ordenes) {
                        startActivity(new Intent(InicioClienteActivity.this, HistorialPedidosActivity.class));
                        return true;
                    } else if (id == R.id.nav_perfil) {
                        startActivity(new Intent(InicioClienteActivity.this, PerfilClienteActivity.class));
                        return true;
                    }

                    return false;
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
        favoritosList.add(new Restaurante("La Lucha", 3.49, "Desayunos", "San Miguel", R.drawable.mlalucha));
        favoritosList.add(new Restaurante("Pinkberry", 3.49, "Heladería", "San Miguel", R.drawable.mpinkberry));
        favoritosList.add(new Restaurante("Taco Bell", 2.50, "Comida mexicana", "San Miguel", R.drawable.mtacobell));
        favoritosList.add(new Restaurante("Popeyes", 1.20, "Pollos", "San Miguel", R.drawable.mpopeyes));
        favoritosList.add(new Restaurante("EDO Sushi Bar", 3.49, "Sushi", "San Miguel", R.drawable.medo));
        favoritosList.add(new Restaurante("Mediterráneo", 3.49, "Pollos", "San Miguel", R.drawable.mmediterraneo));
        return favoritosList;
    }

    private List<Restaurante> getPopularesList() {
        List<Restaurante> popularesList = new ArrayList<>();
        popularesList.add(new Restaurante("Papa John's", 3.49, "Pizzas", "San Miguel", R.drawable.mpapajhons));
        popularesList.add(new Restaurante("Bembos", 1.39, "Hamburguesas", "San Miguel", R.drawable.mbembos));
        popularesList.add(new Restaurante("Chinawok", 2.39, "Chifa", "San Miguel", R.drawable.mchinawok));
        popularesList.add(new Restaurante("Don Belisario", 4.49, "Pollos", "San Miguel", R.drawable.mdonbelisario));
        popularesList.add(new Restaurante("Santoku Sushi Bar", 2.79, "Sushi", "San Miguel", R.drawable.msantoku));
        popularesList.add(new Restaurante("Wing House", 1.49, "Alitas", "San Miguel", R.drawable.mwinhouse));
        return popularesList;
    }
}
