package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Categoria;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.CategoriaAdapter;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Producto;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.ProductoAdapter;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.RestauranteViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class CartaRestauranteFragment extends Fragment {
    private RestauranteViewModel restauranteViewModel;
    private TextView restaurantNameTextView;
    private TextView cuisineTypeTextView;
    private RecyclerView recyclerCategories, recyclerProducts;
    private CategoriaAdapter categoryAdapter;
    private ProductoAdapter productAdapter;
    private List<Producto> filteredProductList;
    private EditText searchBar;
    private String idRestaurante;
    private FirebaseFirestore db;
    private List<Categoria> categoryList;
    private String selectedCategoryId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carta_restaurante, container, false);

        // Inicializa Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializa vistas
        restaurantNameTextView = view.findViewById(R.id.restaurant_name);
        cuisineTypeTextView = view.findViewById(R.id.cuisine_type);

        // Obtén el ViewModel compartido
        restauranteViewModel = new ViewModelProvider(requireActivity()).get(RestauranteViewModel.class);

        // Observa los cambios en el idRestaurante
        restauranteViewModel.getIdRestaurante().observe(getViewLifecycleOwner(), idRestaurante -> {
            if (idRestaurante != null) {
                this.idRestaurante = idRestaurante;
                Log.d("CartaRestaurante", "idRestaurante recibido: " + idRestaurante);
                fetchRestaurantData(idRestaurante);
                loadCategories();
            } else {
                Log.e("CartaRestaurante", "idRestaurante es nulo.");
            }
        });

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

        // Botones de agregar
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

        Button btnAddCategoria = view.findViewById(R.id.btn_add_categoria);
        btnAddCategoria.setEnabled(true);
        btnAddCategoria.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AgregarCategoriaActivity.class);
            intent.putExtra("idRestaurante", idRestaurante);
            startActivity(intent);
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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("CartaRestaurante", "Fragmento resumido. Recargando datos.");
        if (idRestaurante != null) {
            loadCategories();
            if (selectedCategoryId != null) {
                loadProductsByCategory(selectedCategoryId);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("selectedCategoryId", selectedCategoryId);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            selectedCategoryId = savedInstanceState.getString("selectedCategoryId");
            if (selectedCategoryId != null) {
                loadProductsByCategory(selectedCategoryId);
            }
        }
    }

    private void loadCategories() {
        if (idRestaurante == null) {
            Log.e("CartaRestaurante", "idRestaurante es nulo. No se pueden cargar categorías.");
            return;
        }
        Log.d("CartaRestaurante", "Cargando categorías para el restaurante: " + idRestaurante);
        db.collection("categorias")
                .whereEqualTo("idRestaurante", idRestaurante)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e("CartaRestaurante", "Error al escuchar categorías", e);
                        return;
                    }
                    if (querySnapshot != null) {
                        categoryList.clear();
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            Categoria categoria = doc.toObject(Categoria.class);
                            categoria.setId(doc.getId());
                            categoryList.add(categoria);
                            Log.d("CartaRestaurante", "Categoría cargada: " + categoria.getNombre());
                        }
                        categoryAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void onCategorySelected(String categoryId) {
        if (categoryId == null) {
            Log.e("CartaRestaurante", "categoryId es nulo. No se pueden cargar productos.");
            return;
        }

        selectedCategoryId = categoryId;
        Log.d("CartaRestaurante", "Categoría seleccionada: " + categoryId);

        // Habilitar el botón agregar producto
        Button btnAddProduct = getView().findViewById(R.id.btn_add_product);
        btnAddProduct.setEnabled(true);

        loadProductsByCategory(categoryId);
    }

    private void loadProductsByCategory(String categoryId) {
        Log.d("CartaRestaurante", "Cargando productos para la categoría: " + categoryId);
        db.collection("platos")
                .whereEqualTo("idCategoria", categoryId)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e("CartaRestaurante", "Error al escuchar productos", e);
                        return;
                    }
                    if (querySnapshot != null) {
                        filteredProductList.clear();
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            Producto producto = doc.toObject(Producto.class);
                            producto.setId(doc.getId());
                            filteredProductList.add(producto);
                            Log.d("CartaRestaurante", "Producto cargado: " + producto.getNombre());
                        }
                        productAdapter.updateData(filteredProductList);
                    }
                });
    }

    private void filterBySearch(String query) {
        List<Producto> searchResult = new ArrayList<>();
        for (Producto producto : filteredProductList) {
            if (producto.getNombre().toLowerCase().contains(query.toLowerCase())) {
                searchResult.add(producto);
            }
        }
        Log.d("CartaRestaurante", "Resultados de búsqueda: " + searchResult.size());
        productAdapter.updateData(searchResult);
    }

    private void fetchRestaurantData(String idRestaurante) {
        Log.d("CartaRestaurante", "Cargando datos del restaurante: " + idRestaurante);
        db.collection("restaurantes").document(idRestaurante)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String restaurantName = documentSnapshot.getString("nombre");
                        String slogan = documentSnapshot.getString("eslogan");

                        restaurantNameTextView.setText(restaurantName != null ? restaurantName : "Nombre no disponible");
                        cuisineTypeTextView.setText(slogan != null ? slogan : "Eslogan no disponible");
                        Log.d("CartaRestaurante", "Datos del restaurante cargados correctamente.");
                    } else {
                        Log.e("CartaRestaurante", "Documento del restaurante no encontrado.");
                    }
                })
                .addOnFailureListener(e -> Log.e("CartaRestaurante", "Error al cargar datos del restaurante", e));
    }
}


