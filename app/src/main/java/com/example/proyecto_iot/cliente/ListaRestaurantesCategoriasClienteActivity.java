package com.example.proyecto_iot.cliente;

import android.os.Bundle;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_restaurantes_categorias_cliente);

        // Obtener la categoría seleccionada del Intent
        String selectedCategory = getIntent().getStringExtra("selectedCategory");

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
