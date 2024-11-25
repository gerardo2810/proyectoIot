package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.RecyclerView.Categoria;
import com.example.proyecto_iot.cliente.RecyclerView.CategoriaAdapter;
import com.example.proyecto_iot.cliente.RecyclerView.Producto;
import com.example.proyecto_iot.cliente.RecyclerView.ProductoAdapter;
import com.example.proyecto_iot.cliente.RecyclerView.Restaurante;
import com.example.proyecto_iot.cliente.RecyclerView.RestauranteAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class InicioClienteActivity extends AppCompatActivity {
    private RecyclerView recyclerBestOption;
    private RestauranteAdapter bestOptionAdapter;
    private FirebaseFirestore db;
    private RecyclerView recyclerCategories;
    private RecyclerView recyclerFavoritos;
    private RestauranteAdapter popularesAdapter;
    private RecyclerView recyclerPopulares;
    private com.example.proyecto_iot.cliente.RecyclerView.CategoriaAdapter categoryAdapter;


    private List<Restaurante> bestOptionList;
    private List<Restaurante> popularesList;
    private EditText orderSearch; // El cuadro de búsqueda

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_cliente);
        // Enlazar el EditText de búsqueda
        orderSearch = findViewById(R.id.order_search);
        db = FirebaseFirestore.getInstance();
        // Configurar un listener para que, al hacer clic, abra la actividad de búsqueda
        orderSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a la actividad de búsqueda
                Intent intent = new Intent(InicioClienteActivity.this, SearchRestaurantesActivity.class);
                startActivity(intent);
            }
        });

        // Configurar RecyclerView
        recyclerCategories = findViewById(R.id.recycler_categories);
        recyclerCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoriaAdapter(new ArrayList<>(), this);
        recyclerCategories.setAdapter(categoryAdapter);
        fetchCategoriesFromFirebase();
        // Inicializar RecyclerView de favoritos
        recyclerFavoritos = findViewById(R.id.recycler_favoritos);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerFavoritos.setLayoutManager(layoutManager1);

        /* Asignar adaptador para las favoritos
        favoritosAdapter = new RestauranteAdapter(this, getFavoritosList());
        recyclerFavoritos.setAdapter(favoritosAdapter);*/


        // Inicializar RecyclerView de populares
        recyclerPopulares = findViewById(R.id.recycler_populares);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerPopulares.setLayoutManager(layoutManager2);
        popularesList = new ArrayList<>();
        popularesAdapter = new RestauranteAdapter(this, popularesList);
        recyclerPopulares.setAdapter(popularesAdapter);
        fetchPopularesFromFirebase();

        // Cargar productos desde Firebase
        // Inicializar RecyclerView
        recyclerBestOption = findViewById(R.id.recycler_best_option);
        recyclerBestOption.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        bestOptionList = new ArrayList<>();
        bestOptionAdapter = new RestauranteAdapter(this, bestOptionList);
        recyclerBestOption.setAdapter(bestOptionAdapter);
        fetchBestOptionsFromFirebase();

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

    private void fetchCategoriesFromFirebase() {
        db.collection("categorias").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Categoria> categoryList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            String nombre = document.getString("Nombre");
                            String iconFoto = document.getString("iconFoto");
                            String idRestaurante = document.getString("idRestaurante");
                            categoryList.add(new Categoria(nombre, iconFoto, idRestaurante));
                        }
                        categoryAdapter.updateData(categoryList); // Método para actualizar datos del adaptador
                    } else {
                        Log.e("Firestore", "Error al obtener categorías", task.getException());
                    }
                });
    }

    // Método para obtener los restaurantes desde Firebase que esten abiertos
    private void fetchBestOptionsFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("restaurantes").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        bestOptionList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            String nombre = document.getString("nombre");
                            double precioDelivery = document.getDouble("precioDelivery");
                            String tipoDeComida = document.getString("tipoDeComida");
                            String ubicacion = document.getString("ubicacion");
                            String fotoPortada = document.getString("fotoPortada");
                            String fotoLogo = document.getString("fotoLogo");
                            Boolean open = document.getBoolean("open");

                            if (open == true){
                                bestOptionList.add(new Restaurante(nombre, precioDelivery, tipoDeComida, ubicacion,fotoPortada, fotoLogo,0, open));
                            }
                        }
                        bestOptionAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firestore", "Error al obtener los datos", task.getException());
                    }
                });
    }

    // Método para obtener los restaurantes desde Firebase con cantidad de ventas >= 50 y que estén abiertos
    private void fetchPopularesFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("restaurantes").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                popularesList.clear(); // Limpiar la lista antes de agregar nuevos elementos
                for (DocumentSnapshot document : task.getResult()) {
                    try {
                        String nombre = document.contains("nombre") ? document.getString("nombre") : "Nombre no disponible";
                        double precioDelivery = document.contains("precioDelivery") ? document.getDouble("precioDelivery") : 0.0;
                        String tipoDeComida = document.contains("tipoDeComida") ? document.getString("tipoDeComida") : "Desconocido";
                        String ubicacion = document.contains("ubicacion") ? document.getString("ubicacion") : "Ubicación no disponible";
                        String fotoPortada = document.contains("fotoPortada") ? document.getString("fotoPortada") : null;
                        String fotoLogo = document.contains("fotoLogo") ? document.getString("fotoLogo") : null;
                        int ventas = document.contains("ventas") ? document.getLong("ventas").intValue() : 0;
                        Boolean open = document.contains("open") ? document.getBoolean("open") : false;

                        Log.d("FirestoreData", "Procesando restaurante: " + nombre);

                        // Filtrar restaurantes con ventas >= 50 y abiertos
                        if (ventas >= 50 && Boolean.TRUE.equals(open)) {
                            popularesList.add(new Restaurante(
                                    nombre,
                                    precioDelivery,
                                    tipoDeComida,
                                    ubicacion,
                                    fotoPortada,
                                    fotoLogo,
                                    ventas,
                                    open
                            ));
                        }
                    } catch (Exception e) {
                        Log.e("Firestore", "Error procesando documento: " + document.getId(), e);
                    }
                }
                popularesAdapter.notifyDataSetChanged(); // Notificar cambios al adaptador
            } else {
                Log.e("Firestore", "Error al obtener los datos", task.getException());
            }
        });
    }



}


    /*private List<Restaurante> getFavoritosList() {
        List<Restaurante> favoritosList = new ArrayList<>();
        favoritosList.add(new Restaurante("La Lucha", 3.49, "Desayunos", "San Miguel", R.drawable.mlalucha));
        favoritosList.add(new Restaurante("Pinkberry", 3.49, "Heladería", "San Miguel", R.drawable.mpinkberry));
        favoritosList.add(new Restaurante("Taco Bell", 2.50, "Comida mexicana", "San Miguel", R.drawable.mtacobell));
        favoritosList.add(new Restaurante("Popeyes", 1.20, "Pollos", "San Miguel", R.drawable.mpopeyes));
        favoritosList.add(new Restaurante("EDO Sushi Bar", 3.49, "Sushi", "San Miguel", R.drawable.medo));
        favoritosList.add(new Restaurante("Mediterráneo", 3.49, "Pollos", "San Miguel", R.drawable.mmediterraneo));
        return favoritosList;
    }

    }*/


