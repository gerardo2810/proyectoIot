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

import java.util.ArrayList;
import java.util.List;

public class SearchRestaurantesActivity extends AppCompatActivity {

    private RestauranteAdapter restauranteAdapter;
    private List<Restaurante> restauranteList;
    private RecyclerView recyclerView;
    private EditText searchInput;
    private ImageView backArrow, clearSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_restaurantes);

        // Inicializar el RecyclerView
        recyclerView = findViewById(R.id.recycler_search_restaurantes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar lista de restaurantes (deberías obtener esta lista de una base de datos o fuente real)
        restauranteList = getRestaurantesList();

        // Configurar el adaptador
        restauranteAdapter = new RestauranteAdapter(this, restauranteList);
        recyclerView.setAdapter(restauranteAdapter);

        // EditText para la búsqueda
        searchInput = findViewById(R.id.search_input);

        // Inicializar la flecha de retroceso y el botón para limpiar búsqueda
        backArrow = findViewById(R.id.back_arrow);
        clearSearch = findViewById(R.id.clear_search);

        // Añadir TextWatcher para filtrar los resultados en tiempo real
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No necesitamos implementar este método
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filtrar la lista de restaurantes cuando el usuario escribe
                filterRestaurantes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No necesitamos implementar este método
            }
        });

        // Acción de la flecha de retroceso para volver a InicioClienteActivity
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar de regreso a InicioClienteActivity
                Intent intent = new Intent(SearchRestaurantesActivity.this, InicioClienteActivity.class);
                startActivity(intent);
                finish(); // Cierra la actividad actual
            }
        });

        // Acción del botón para limpiar la búsqueda
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInput.setText(""); // Limpiar el texto del campo de búsqueda
            }
        });
    }

    // Método para filtrar la lista de restaurantes
    private void filterRestaurantes(String query) {
        List<Restaurante> filteredList = new ArrayList<>();
        for (Restaurante restaurante : restauranteList) {
            if (restaurante.getNameTitlte().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(restaurante);
            }
        }
        // Actualiza la lista del adaptador con los resultados filtrados
        restauranteAdapter.updateList(filteredList);
    }

    // Método para obtener la lista de restaurantes
    // Aquí deberías obtener la lista real desde tu base de datos
    private List<Restaurante> getRestaurantesList() {
        List<Restaurante> list = new ArrayList<>();
        list.add(new Restaurante("Normita", 4.5, "Postres", "San Miguel", R.drawable.mlalucha));
        list.add(new Restaurante("Norkys", 4.3, "Pollos", "San Miguel", R.drawable.mchinawok));
        list.add(new Restaurante("La Norteña", 4.0, "Anticuchos", "Miraflores", R.drawable.mpapajhons));
        list.add(new Restaurante("Normita", 4.5, "Postres", "San Miguel", R.drawable.mlalucha));
        list.add(new Restaurante("Norkys", 4.3, "Pollos", "San Miguel", R.drawable.mchinawok));
        list.add(new Restaurante("La Norteña", 4.0, "Anticuchos", "Miraflores", R.drawable.mpapajhons));
        list.add(new Restaurante("Normita", 4.5, "Postres", "San Miguel", R.drawable.mlalucha));
        list.add(new Restaurante("Norkys", 4.3, "Pollos", "San Miguel", R.drawable.mchinawok));
        list.add(new Restaurante("La Norteña", 4.0, "Anticuchos", "Miraflores", R.drawable.mpapajhons));
        return list;
    }
}
