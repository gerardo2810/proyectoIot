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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.RecyclerView.Restaurante;
import com.example.proyecto_iot.cliente.RecyclerView.RestauranteAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListaRestaurantesCategoriasClienteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RestauranteAdapter restauranteAdapter;
    private List<Restaurante> restauranteList = new ArrayList<>(); // Asegúrate de inicializar la lista
    private EditText searchText;
    private String selectedCategory; // Declaro selectedCategory como variable global

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_restaurantes_categorias_cliente);

        // Obtener la categoría seleccionada del Intent y asignarla a la variable global
        selectedCategory = getIntent().getStringExtra("selectedCategory");

        // Inicializar el RecyclerView
        recyclerView = findViewById(R.id.recycler_lista_restaurantes_categoria);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar la lista de restaurantes (puedes obtener la lista desde tu base de datos)
        restauranteList = getRestaurantesList();

        // Filtrar la lista de restaurantes por la categoría seleccionada
        if (selectedCategory != null) {
            filterRestaurantsByCategory(selectedCategory); // Filtrar los restaurantes
        } else {
            Toast.makeText(this, "No se recibió ninguna categoría.", Toast.LENGTH_SHORT).show();
        }

        // Lógica para el botón de retroceso (flecha)
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent para regresar a la vista de inicio
                Intent intent = new Intent(ListaRestaurantesCategoriasClienteActivity.this, InicioClienteActivity.class);
                startActivity(intent);
                finish(); // Finaliza la actividad actual
            }
        });

        // Inicializar el buscador
        searchText = findViewById(R.id.search_text);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se necesita acción antes del cambio de texto
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRestaurantsByName(s.toString()); // Filtrar los restaurantes por nombre
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No se necesita acción después del cambio de texto
            }
        });
    }

    // Método para filtrar los restaurantes por categoría
    private void filterRestaurantsByCategory(String category) {
        List<Restaurante> filteredList = new ArrayList<>();
        for (Restaurante restaurante : restauranteList) {
            if (restaurante.getCategory().equalsIgnoreCase(category)) {
                filteredList.add(restaurante);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No hay restaurantes en la categoría seleccionada.", Toast.LENGTH_SHORT).show();
        }

        // Actualizar el adaptador con la lista filtrada
        if (restauranteAdapter != null) {
            restauranteAdapter.updateList(filteredList);
        } else {
            // Inicializar el adaptador si aún no está inicializado
            restauranteAdapter = new RestauranteAdapter(this, filteredList);
            recyclerView.setAdapter(restauranteAdapter);
        }
    }

    // Método para filtrar los restaurantes por nombre dentro de la misma categoría
    private void filterRestaurantsByName(String name) {
        List<Restaurante> filteredList = new ArrayList<>();
        for (Restaurante restaurante : restauranteList) {
            // Filtrar solo los restaurantes que pertenezcan a la categoría seleccionada
            if (restaurante.getCategory().equalsIgnoreCase(selectedCategory) &&
                    restaurante.getNameTitlte().toLowerCase().contains(name.toLowerCase())) {
                filteredList.add(restaurante);
            }
        }

        // Actualizar el adaptador con la lista filtrada por nombre y categoría
        if (restauranteAdapter != null) {
            restauranteAdapter.updateList(filteredList);
        }
    }

    // Método para obtener la lista de restaurantes (debes reemplazarlo con tu fuente de datos real)
    private List<Restaurante> getRestaurantesList() {
        List<Restaurante> list = new ArrayList<>();
        list.add(new Restaurante("Normita", 4.5, "Pizzas", "San Miguel", R.drawable.mlalucha));
        list.add(new Restaurante("Norkys", 4.3, "Pollo", "San Miguel", R.drawable.mchinawok));
        list.add(new Restaurante("La Norteña", 4.0, "Carnes", "Miraflores", R.drawable.mpapajhons));
        // Añade más restaurantes según sea necesario
        return list;
    }
}
