package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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

public class SearchRestaurantesActivity extends AppCompatActivity {

    private RestauranteAdapter restauranteAdapter;
    private List<Restaurante> restauranteList = new ArrayList<>(); // Inicialización de la lista
    private RecyclerView recyclerView;
    private EditText searchInput;
    private ImageView backArrow, clearSearch;
    private FirebaseFirestore db; // Inicializar Firebase Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_restaurantes);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();

        // Inicializar el RecyclerView
        recyclerView = findViewById(R.id.recycler_search_restaurantes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar el adaptador (vacío inicialmente)
        restauranteAdapter = new RestauranteAdapter(this, restauranteList);
        recyclerView.setAdapter(restauranteAdapter);

        // Cargar los restaurantes desde Firebase
        getRestaurantesList();

        // EditText para la búsqueda
        searchInput = findViewById(R.id.search_input);

        // Inicializar la flecha de retroceso y el botón para limpiar búsqueda
        backArrow = findViewById(R.id.back_arrow);
        clearSearch = findViewById(R.id.clear_search);

        // Añadir TextWatcher para filtrar los resultados en tiempo real
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filtrar la lista de restaurantes cuando el usuario escribe
                filterRestaurantes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Acción de la flecha de retroceso para volver a InicioClienteActivity
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(SearchRestaurantesActivity.this, InicioClienteActivity.class);
            startActivity(intent);
            finish(); // Cierra la actividad actual
        });

        // Acción del botón para limpiar la búsqueda
        clearSearch.setOnClickListener(v -> searchInput.setText("")); // Limpiar el texto del campo de búsqueda
    }

    // Método para filtrar la lista de restaurantes
    private void filterRestaurantes(String query) {
        List<Restaurante> filteredList = new ArrayList<>();
        for (Restaurante restaurante : restauranteList) {
            if (restaurante.getNombre().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(restaurante);
            }
        }
        // Actualiza la lista del adaptador con los resultados filtrados
        restauranteAdapter.updateList(filteredList);
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
                            String fotoPortada =document.getString("fotoPortada");

                            // Agregar el restaurante a la lista
                            restauranteList.add(new Restaurante(nombre, precioDelivery, tipoDeComida, ubicacion, fotoPortada,fotoLogo));
                        }
                        // Notificar al adaptador que los datos han cambiado
                        restauranteAdapter.notifyDataSetChanged();
                    } else {
                        // Manejar error al obtener los datos de Firebase
                        System.err.println("Error al obtener los restaurantes: " + task.getException());
                    }
                });
    }
}
