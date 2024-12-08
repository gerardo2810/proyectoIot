package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.RecyclerView.Restaurante;
import com.example.proyecto_iot.cliente.RecyclerView.RestauranteAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ListaRestaurantesCategoriasClienteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RestauranteAdapter restauranteAdapter;
    private List<Restaurante> restauranteList = new ArrayList<>();
    private EditText searchText;
    private String selectedCategory; // Categoría seleccionada
    private FirebaseFirestore db; // Referencia a Firebase Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_restaurantes_categorias_cliente);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Obtener la categoría seleccionada del Intent
        selectedCategory = getIntent().getStringExtra("selectedCategory");
        System.out.println(selectedCategory);

        // Inicializar el RecyclerView
        recyclerView = findViewById(R.id.recycler_lista_restaurantes_categoria);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Cargar la lista de restaurantes desde Firebase
        getRestaurantesList();

        // Lógica para el botón de retroceso
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(ListaRestaurantesCategoriasClienteActivity.this, InicioClienteActivity.class);
            startActivity(intent);
            finish(); // Finaliza la actividad actual
        });

        // Inicializar el buscador
        searchText = findViewById(R.id.search_text);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRestaurantsByName(s.toString()); // Filtrar los restaurantes por nombre
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Método para filtrar los restaurantes por categoría
    private void filterRestaurantsByCategory(String category) {
        List<Restaurante> filteredList = new ArrayList<>();
        for (Restaurante restaurante : restauranteList) {
            if (restaurante.getTipoDeComida() != null && restaurante.getTipoDeComida().equalsIgnoreCase(category)) {
                filteredList.add(restaurante);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No hay restaurantes en la categoría seleccionada.", Toast.LENGTH_SHORT).show();
        }

        restauranteAdapter = new RestauranteAdapter(this, filteredList);
        recyclerView.setAdapter(restauranteAdapter);
    }

    // Método para filtrar los restaurantes por nombre dentro de la misma categoría
    private void filterRestaurantsByName(String name) {
        List<Restaurante> filteredList = new ArrayList<>();
        for (Restaurante restaurante : restauranteList) {
            if (restaurante.getTipoDeComida().equalsIgnoreCase(selectedCategory) &&
                    restaurante.getNombre().toLowerCase().contains(name.toLowerCase())) {
                filteredList.add(restaurante);
            }
        }

        if (restauranteAdapter != null) {
            restauranteAdapter.updateList(filteredList);
        }
    }

    // Método para obtener la lista de restaurantes desde Firebase
    private void getRestaurantesList() {
        db.collection("restaurantes").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        restauranteList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            String nombre = document.getString("nombre");
                            double precioDelivery = document.contains("precioDelivery")
                                    ? document.getDouble("precioDelivery") : 0.0;
                            String tipoDeComida = document.getString("tipoDeComida");
                            String ubicacion = document.getString("ubicacion");
                            String fotoLogo = document.getString("fotoLogo");
                            String fotoPortada = document.getString("fotoPortada");

                            restauranteList.add(new Restaurante(nombre, precioDelivery, tipoDeComida, ubicacion, fotoPortada,fotoLogo));
                        }

                        // Filtrar restaurantes por categoría seleccionada
                        if (selectedCategory != null) {
                            filterRestaurantsByCategory(selectedCategory);
                        } else {
                            restauranteAdapter = new RestauranteAdapter(this, restauranteList);
                            recyclerView.setAdapter(restauranteAdapter);
                        }
                    } else {
                        Log.e("Firestore", "Error al obtener los restaurantes", task.getException());
                        Toast.makeText(this, "Error al cargar los restaurantes.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
