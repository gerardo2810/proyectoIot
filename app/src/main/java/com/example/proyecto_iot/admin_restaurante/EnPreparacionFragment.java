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
import androidx.appcompat.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    private List<Pedido> filteredList; // Lista filtrada
    private FirebaseFirestore db;
    private String idRestaurante; // ID del restaurante

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Obtener idRestaurante de los argumentos
        if (getArguments() != null) {
            idRestaurante = getArguments().getString("idRestaurante");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_en_preparacion, container, false);

        // Inicializar RecyclerView
        rvOrdersList = view.findViewById(R.id.rv_orders_list);
        rvOrdersList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializar listas
        pedidoList = new ArrayList<>();
        filteredList = new ArrayList<>();

        // Inicializar adapter
        pedidoPreparadoAdapter = new PedidoPreparadoAdapter(filteredList, getContext());
        rvOrdersList.setAdapter(pedidoPreparadoAdapter);

        // Cargar pedidos desde Firestore
        fetchOrders();

        return view;
    }

    private void fetchOrders() {
        if (idRestaurante == null) {
            Toast.makeText(getContext(), "ID del restaurante no disponible.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("pedidos")
                .whereEqualTo("idRestaurante", idRestaurante)
                .whereEqualTo("estado", 1) // Filtrar pedidos en estado "1"
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Toast.makeText(getContext(), "Error al obtener pedidos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (querySnapshot != null) {
                        pedidoList.clear();
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            Pedido pedido = doc.toObject(Pedido.class);
                            pedido.setId(doc.getId()); // Asigna manualmente el ID del documento
                            buscarNombreCliente(pedido);
                        }
                    }
                });
    }

    private void buscarNombreCliente(Pedido pedido) {
        db.collection("clientes").document(pedido.getIdCliente())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombreCliente = documentSnapshot.getString("Nombre");
                        pedido.setNombreCliente(nombreCliente != null ? nombreCliente : "Cliente Desconocido");
                    } else {
                        pedido.setNombreCliente("Cliente Desconocido");
                    }
                    pedidoList.add(pedido);
                    filteredList.clear();
                    filteredList.addAll(pedidoList);
                    pedidoPreparadoAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al obtener nombre del cliente: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}


