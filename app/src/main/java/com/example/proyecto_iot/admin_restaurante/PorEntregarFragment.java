package com.example.proyecto_iot.admin_restaurante;

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
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Pedido;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.PedidoEntregadoAdapter;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.PedidoPreparadoAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PorEntregarFragment extends Fragment {

    private RecyclerView rvOrdersList;
    private PedidoEntregadoAdapter pedidoEntregadoAdapter;
    private List<Pedido> pedidoList;
    private List<Pedido> filteredList; // Filtered list
    private FirebaseFirestore db;
    private String idRestaurante; // Restaurant ID
    private EditText orderSearch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get idRestaurante from arguments
        if (getArguments() != null) {
            idRestaurante = getArguments().getString("idRestaurante");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_por_entregar, container, false);

        // Initialize RecyclerView
        rvOrdersList = view.findViewById(R.id.rv_orders_list);
        rvOrdersList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize search EditText
        orderSearch = view.findViewById(R.id.order_search);

        // Initialize lists
        pedidoList = new ArrayList<>();
        filteredList = new ArrayList<>();

        // Initialize adapter
        pedidoEntregadoAdapter = new PedidoEntregadoAdapter(filteredList, getContext());
        rvOrdersList.setAdapter(pedidoEntregadoAdapter);

        // Fetch orders from Firestore
        fetchOrders();

        // Setup search functionality
        orderSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterOrders(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void fetchOrders() {
        if (idRestaurante == null) return;

        db.collection("pedidos")
                .whereEqualTo("idRestaurante", idRestaurante)
                .whereIn("estado", Arrays.asList(2, 3)) // Filtrar estado 2 o 3
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Toast.makeText(getContext(), "Error fetching orders: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (querySnapshot != null) {
                        pedidoList.clear();
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            Pedido pedido = doc.toObject(Pedido.class);
                            pedido.setId(doc.getId()); // Asigna el ID del documento
                            fetchRepartidor(pedido); // Obtiene el nombre del repartidor si aplica
                        }
                    }
                });
    }

    private void fetchRepartidor(Pedido pedido) {
        if (pedido.getIdRepartidor() == null || pedido.getIdRepartidor().isEmpty()) {
            pedido.setNombreRepartidor("Sin repartidor asignado");
            pedido.setRepartidorAsignado(false);
            pedidoList.add(pedido);
            updateFilteredList();
        } else {
            db.collection("repartidores").document(pedido.getIdRepartidor())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nombreRepartidor = documentSnapshot.getString("nombre");
                            pedido.setNombreRepartidor(nombreRepartidor);
                        } else {
                            pedido.setNombreRepartidor("Repartidor desconocido");
                        }
                        pedido.setRepartidorAsignado(true);
                        pedidoList.add(pedido);
                        updateFilteredList();
                    })
                    .addOnFailureListener(e -> {
                        pedido.setNombreRepartidor("Error al cargar repartidor");
                        pedidoList.add(pedido);
                        updateFilteredList();
                    });
        }
    }

    private void updateFilteredList() {
        filteredList.clear();
        filteredList.addAll(pedidoList);
        pedidoEntregadoAdapter.notifyDataSetChanged();
    }

    private void filterOrders(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(pedidoList);
        } else {
            for (Pedido pedido : pedidoList) {
                if (pedido.getNombreCliente().toLowerCase().contains(query.toLowerCase()) ||
                        pedido.getNombreRepartidor().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(pedido);
                }
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No se encontraron resultados", Toast.LENGTH_SHORT).show();
        }

        pedidoEntregadoAdapter.notifyDataSetChanged();
    }
}
