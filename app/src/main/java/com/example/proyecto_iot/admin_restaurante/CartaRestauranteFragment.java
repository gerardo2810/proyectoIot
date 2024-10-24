package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Categoria;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.CategoriaAdapter;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Producto;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.ProductoAdapter;

import java.util.ArrayList;
import java.util.List;


public class CartaRestauranteFragment extends Fragment {

    private RecyclerView recyclerCategories, recyclerProducts;
    private CategoriaAdapter categoryAdapter;
    private ProductoAdapter productAdapter;
    private List<Producto> productList; // Lista completa de productos
    private List<Producto> filteredProductList; // Lista filtrada
    private EditText searchBar; // Barra de búsqueda

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el layout del fragmento
        View view = inflater.inflate(R.layout.fragment_carta_restaurante, container, false);

        // Inicializar RecyclerView de categorías
        recyclerCategories = view.findViewById(R.id.recycler_categories);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerCategories.setLayoutManager(layoutManager);

        // Inicializar la lista completa de productos y la lista filtrada
        productList = getProductList();
        filteredProductList = new ArrayList<>(productList); // Inicialmente muestra todos los productos

        // Asignar adaptador para las categorías y configurar el listener
        categoryAdapter = new CategoriaAdapter(getCategoryList(), getContext(), this::filterByCategory);
        recyclerCategories.setAdapter(categoryAdapter);

        // Inicializar RecyclerView de productos
        recyclerProducts = view.findViewById(R.id.recycler_products);
        recyclerProducts.setLayoutManager(new LinearLayoutManager(getContext()));

        // Asignar adaptador para los productos
        productAdapter = new ProductoAdapter(filteredProductList, getContext());
        recyclerProducts.setAdapter(productAdapter);

        // Botón para agregar nuevo producto
        Button btnAddProduct = view.findViewById(R.id.btn_add_product);
        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AgregarProductoActivity.class);
            startActivity(intent);
        });

        // Configurar la barra de búsqueda
        searchBar = view.findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Filtrar productos por el texto ingresado en la búsqueda
                filterBySearch(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        return view; // Devolver la vista inflada
    }

    // Método para filtrar productos por categoría
    private void filterByCategory(String category) {
        filteredProductList.clear(); // Limpiar la lista filtrada
        if (category.equals("Todas")) {
            filteredProductList.addAll(productList); // Mostrar todos los productos si la categoría es "Todas"
        } else {
            for (Producto producto : productList) {
                if (producto.getCategoria().equalsIgnoreCase(category)) {
                    filteredProductList.add(producto); // Agregar productos que coincidan con la categoría
                }
            }
        }
        productAdapter.notifyDataSetChanged(); // Notificar al adaptador sobre los cambios
    }

    // Método para filtrar productos por búsqueda
    private void filterBySearch(String query) {
        filteredProductList.clear(); // Limpiar la lista filtrada
        for (Producto producto : productList) {
            if (producto.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredProductList.add(producto); // Agregar productos que coincidan con la búsqueda
            }
        }
        productAdapter.notifyDataSetChanged(); // Notificar al adaptador sobre los cambios
    }

    // Método para obtener la lista de categorías
    private List<Categoria> getCategoryList() {
        List<Categoria> categoryList = new ArrayList<>();
        categoryList.add(new Categoria("Todas", R.drawable.alllogo)); // Opción para mostrar todas las categorías
        categoryList.add(new Categoria("Entradas", R.drawable.salad));
        categoryList.add(new Categoria("Tallarines", R.drawable.pasta));
        categoryList.add(new Categoria("Pescados", R.drawable.fish));
        categoryList.add(new Categoria("Carnes", R.drawable.meat));
        categoryList.add(new Categoria("Arroces", R.drawable.rice));
        categoryList.add(new Categoria("Especiales", R.drawable.especial));
        categoryList.add(new Categoria("Nueva categoría", R.drawable.add)); // Opción para nueva categoría
        return categoryList;
    }

    // Método para obtener la lista de productos
    private List<Producto> getProductList() {
        List<Producto> productList = new ArrayList<>();
        productList.add(new Producto("1", "Langostinos Tempura 'Pop Corn'", "", "Entradas", 10, 45.50, true, R.drawable.plato));
        productList.add(new Producto("2", "Shanghai Hot Wings", "", "Entradas", 10, 39.00, true, R.drawable.plato));
        productList.add(new Producto("3", "Classic Wantan", "", "Entradas", 10, 32.00, true, R.drawable.plato));
        productList.add(new Producto("4", "Chaufa Especial", "", "Arroces", 5, 38.00, true, R.drawable.plato2));
        productList.add(new Producto("5", "Arroz Meloso Mixto", "", "Arroces", 5, 42.00, true, R.drawable.plato2));
        productList.add(new Producto("6", "Salmon Al Curry", "", "Pescados", 7, 35.00, false, R.drawable.plato3));
        productList.add(new Producto("7", "Pesacado Imperial", "", "Pescados", 7, 35.00, false, R.drawable.plato3));
        productList.add(new Producto("8", "Mongolian Beef", "", "Carnes", 20, 69.50, true, R.drawable.plato4));
        productList.add(new Producto("9", "Pollo con nueces", "", "Carnes", 20, 49.50, true, R.drawable.plato4));
        productList.add(new Producto("10", "KAM LU Wantan", "", "Especiales", 30, 63.50, true, R.drawable.plato5));
        return productList;
    }
}

