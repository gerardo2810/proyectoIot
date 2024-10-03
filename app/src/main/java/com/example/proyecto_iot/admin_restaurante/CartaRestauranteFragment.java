package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Categoria;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.CategoriaAdapter;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Producto;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.ProductoAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartaRestauranteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartaRestauranteFragment extends Fragment {

    private RecyclerView recyclerCategories, recyclerProducts;
    private CategoriaAdapter categoryAdapter;
    private ProductoAdapter productAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CartaRestauranteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartaRestauranteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartaRestauranteFragment newInstance(String param1, String param2) {
        CartaRestauranteFragment fragment = new CartaRestauranteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el layout del fragmento
        View view = inflater.inflate(R.layout.fragment_carta_restaurante, container, false);

        // Inicializar RecyclerView de categorías
        recyclerCategories = view.findViewById(R.id.recycler_categories);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerCategories.setLayoutManager(layoutManager);

        // Asignar adaptador para las categorías
        categoryAdapter = new CategoriaAdapter(getCategoryList(), getContext());
        recyclerCategories.setAdapter(categoryAdapter);

        // Inicializar RecyclerView de productos
        recyclerProducts = view.findViewById(R.id.recycler_products);
        recyclerProducts.setLayoutManager(new LinearLayoutManager(getContext()));

        // Asignar adaptador para los productos
        productAdapter = new ProductoAdapter(getProductList(), getContext());
        recyclerProducts.setAdapter(productAdapter);

        // Botón para agregar nuevo producto
        Button btnAddProduct = view.findViewById(R.id.btn_add_product);
        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AgregarProductoActivity.class);
            startActivity(intent);
        });

        return view; // Devolver la vista inflada
    }

    // Método para obtener la lista de categorías
    private List<Categoria> getCategoryList() {
        List<Categoria> categoryList = new ArrayList<>();
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
        productList.add(new Producto("1", "Langostinos Tempura 'Pop Corn'", "",10, 45.50, true, R.drawable.plato));
        productList.add(new Producto("2", "Chaufa Especial", "",5,  38.00, true, R.drawable.plato2));
        productList.add(new Producto("3", "Salmon Al Curry", "", 7, 35.00, false, R.drawable.plato3));
        productList.add(new Producto("4", "Mongolian Beef", "",20, 69.50, true, R.drawable.plato4));
        productList.add(new Producto("5", "KAM LU Wantan", "",30, 63.50, true, R.drawable.plato5));
        return productList;
    }
}