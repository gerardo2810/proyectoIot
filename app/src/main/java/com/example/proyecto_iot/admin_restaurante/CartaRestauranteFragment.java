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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Categoria;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.CategoriaAdapter;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Producto;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.ProductoAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class CartaRestauranteFragment extends Fragment {

    private RecyclerView recyclerCategories, recyclerProducts;
    private CategoriaAdapter categoryAdapter;
    private ProductoAdapter productAdapter;
    private List<Producto> filteredProductList; // Lista filtrada de productos
    private EditText searchBar;
    private String idRestaurante;
    private FirebaseFirestore db;
    private List<Categoria> categoryList;
    private String selectedCategoryId; // ID de la categoría seleccionada

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carta_restaurante, container, false);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Recuperar el idRestaurante desde los argumentos
        if (getArguments() != null) {
            idRestaurante = getArguments().getString("idRestaurante");
        }

        // Inicializar RecyclerView de categorías
        recyclerCategories = view.findViewById(R.id.recycler_categories);
        recyclerCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Inicializar RecyclerView de productos
        recyclerProducts = view.findViewById(R.id.recycler_products);
        recyclerProducts.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializar adaptadores
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoriaAdapter(categoryList, getContext(), this::onCategorySelected);
        recyclerCategories.setAdapter(categoryAdapter);

        filteredProductList = new ArrayList<>();
        productAdapter = new ProductoAdapter(filteredProductList, getContext());
        recyclerProducts.setAdapter(productAdapter);


        Button btnAddProduct = view.findViewById(R.id.btn_add_product);
        btnAddProduct.setEnabled(false);

        btnAddProduct.setOnClickListener(v -> {
            if (selectedCategoryId != null) {
                Intent intent = new Intent(getContext(), AgregarProductoActivity.class);
                intent.putExtra("idCategoria", selectedCategoryId);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Por favor, selecciona una categoría primero.", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar la barra de búsqueda
        searchBar = view.findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterBySearch(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Cargar categorías desde Firestore
        loadCategories();

        return view;
    }

    // Método para cargar categorías según el ID del restaurante
    private void loadCategories() {
        db.collection("categorias")
                .whereEqualTo("idRestaurante", idRestaurante)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    categoryList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Categoria categoria = doc.toObject(Categoria.class);
                        categoria.setId(doc.getId());
                        categoryList.add(categoria);
                    }
                    categoryAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("CartaRestaurante", "Error al cargar categorías", e));
    }

    // Método para manejar la selección de categoría
    private void onCategorySelected(String categoryId) {
        selectedCategoryId = categoryId;

        // Habilitar el botón cuando se seleccione una categoría
        Button btnAddProduct = getView().findViewById(R.id.btn_add_product);
        btnAddProduct.setEnabled(true);

        loadProductsByCategory(categoryId);
    }

    // Método para cargar productos según la categoría seleccionada
    private void loadProductsByCategory(String categoryId) {
        db.collection("platos")
                .whereEqualTo("idCategoria", categoryId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    filteredProductList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Producto producto = doc.toObject(Producto.class);
                        producto.setId(doc.getId());
                        filteredProductList.add(producto);
                    }
                    productAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("CartaRestaurante", "Error al cargar productos", e));
    }

    // Método para filtrar productos por búsqueda
    private void filterBySearch(String query) {
        List<Producto> searchResult = new ArrayList<>();
        for (Producto producto : filteredProductList) {
            if (producto.getNombre().toLowerCase().contains(query.toLowerCase())) {
                searchResult.add(producto);
            }
        }
        productAdapter.updateData(searchResult);
    }
}


