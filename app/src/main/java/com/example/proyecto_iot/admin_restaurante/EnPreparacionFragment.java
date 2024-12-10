package com.example.proyecto_iot.admin_restaurante;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Pedido;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.PedidoAdapter;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.PedidoPreparadoAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EnPreparacionFragment extends Fragment {

    private RecyclerView rvOrdersList;
    private PedidoPreparadoAdapter pedidoPreparadoAdapter;
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
        View view = inflater.inflate(R.layout.fragment_en_preparacion, container, false);

        // Initialize RecyclerView
        rvOrdersList = view.findViewById(R.id.rv_orders_list);
        rvOrdersList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize search EditText
        orderSearch = view.findViewById(R.id.order_search);

        // Initialize lists
        pedidoList = new ArrayList<>();
        filteredList = new ArrayList<>();

        // Initialize adapter
        pedidoPreparadoAdapter = new PedidoPreparadoAdapter(filteredList, getContext(), this::removeOrder);
        rvOrdersList.setAdapter(pedidoPreparadoAdapter);

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
                .whereEqualTo("estado", 1)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Toast.makeText(getContext(), "Error fetching orders: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (querySnapshot != null) {
                        pedidoList.clear();
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            Pedido pedido = doc.toObject(Pedido.class);
                            pedidoList.add(pedido);
                        }

                        filteredList.clear();
                        filteredList.addAll(pedidoList);
                        pedidoPreparadoAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void filterOrders(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(pedidoList);
        } else {
            for (Pedido pedido : pedidoList) {
                if (pedido.getIdCliente().toLowerCase().contains(query.toLowerCase()) ||
                        pedido.getNombreRestaurante().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(pedido);
                }
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No se encontraron resultados", Toast.LENGTH_SHORT).show();
        }

        pedidoPreparadoAdapter.notifyDataSetChanged();
    }

    private void removeOrder(Pedido pedido) {
        int position = filteredList.indexOf(pedido);
        if (position != -1) {
            filteredList.remove(position);
            pedidoPreparadoAdapter.notifyItemRemoved(position);
        }
    }
}
