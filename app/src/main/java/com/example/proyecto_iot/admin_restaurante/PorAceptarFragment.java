package com.example.proyecto_iot.admin_restaurante;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.proyecto_iot.MainActivity;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Order;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.OrderAdapter;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Pedido;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.PedidoAdapter;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PorAceptarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class PorAceptarFragment extends Fragment {

    private RecyclerView rvOrdersList;
    private PedidoAdapter pedidoAdapter;
    private List<Pedido> pedidoList;
    private List<Pedido> filteredList; // Filtered list
    private FirebaseFirestore db;
    private String idRestaurante; // Restaurant ID
    private EditText orderSearch;
    private ListenerRegistration ordersListener; // Firestore listener registration
    private static final String CHANNEL_ID = "Nuevo_Pedido";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtener el idRestaurante del Bundle
        if (getArguments() != null) {
            idRestaurante = getArguments().getString("idRestaurante");
        }

        if (idRestaurante == null) {
            Log.e("PorAceptarFragment", "onCreate: idRestaurante es nulo.");
            return;
        }

        Log.d("PorAceptarFragment", "onCreate: idRestaurante recibido: " + idRestaurante);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get idRestaurante from arguments
        if (getArguments() != null) {
            idRestaurante = getArguments().getString("idRestaurante");
            Log.d("PorAceptarFragment", "idRestaurante recibido: " + idRestaurante);
        } else {
            Log.e("PorAceptarFragment", "No se recibió idRestaurante en los argumentos.");
        }

        // Verify notification permissions for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }

        // Create notification channel (Android 8+)
        createNotificationChannel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_por_aceptar, container, false);

        // Initialize RecyclerView
        rvOrdersList = view.findViewById(R.id.rv_orders_list);
        rvOrdersList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize search EditText
        orderSearch = view.findViewById(R.id.order_search);

        // Initialize lists
        pedidoList = new ArrayList<>();
        filteredList = new ArrayList<>();

        // Initialize adapter
        pedidoAdapter = new PedidoAdapter(filteredList, getContext());
        rvOrdersList.setAdapter(pedidoAdapter);

        // Manually trigger initial data load
        fetchOrdersInitially();

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

    @Override
    public void onResume() {
        super.onResume();

        if (idRestaurante == null) {
            Log.e("PorAceptarFragment", "onResume: idRestaurante es nulo.");
            return;
        }

        Log.d("PorAceptarFragment", "Fragment visible. Recargando datos.");
        fetchOrders();
    }

    @Override
    public void onPause() {
        super.onPause();
        detachFirestoreListener(); // Detach listener to avoid leaks
    }

    /**
     * Fetch orders initially to ensure the list is loaded on fragment start.
     */
    private void fetchOrdersInitially() {
        if (idRestaurante == null) {
            Log.e("PorAceptarFragment", "fetchOrdersInitially: idRestaurante es nulo.");
            Toast.makeText(getContext(), "ID de restaurante no definido.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("pedidos")
                .whereEqualTo("idRestaurante", idRestaurante)
                .whereEqualTo("estado", 0)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        pedidoList.clear();
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            Pedido pedido = doc.toObject(Pedido.class);
                            Log.d("Pedido", "Pedido recibido: " + pedido.getIdCliente() + ", Estado: " + pedido.getEstado());
                            pedidoList.add(pedido);
                        }

                        filteredList.clear();
                        filteredList.addAll(pedidoList);
                        pedidoAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("Pedido", "No se encontraron pedidos.");
                        Toast.makeText(getContext(), "No hay pedidos por aceptar.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Pedido", "Error obteniendo pedidos: " + e.getMessage());
                    Toast.makeText(getContext(), "Error obteniendo pedidos.", Toast.LENGTH_SHORT).show();
                });
    }

    private void attachFirestoreListener() {
        if (idRestaurante == null) {
            Log.e("PorAceptarFragment", "attachFirestoreListener: idRestaurante es nulo.");
            return;
        }

        if (ordersListener == null) {
            ordersListener = db.collection("pedidos")
                    .whereEqualTo("idRestaurante", idRestaurante)
                    .whereEqualTo("estado", 0)
                    .addSnapshotListener((querySnapshot, e) -> {
                        if (e != null) {
                            Log.e("Pedido", "Error obteniendo pedidos: " + e.getMessage());
                            Toast.makeText(getContext(), "Error obteniendo pedidos.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (querySnapshot != null) {
                            pedidoList.clear();
                            for (QueryDocumentSnapshot doc : querySnapshot) {
                                Pedido pedido = doc.toObject(Pedido.class);
                                Log.d("Pedido", "Pedido actualizado: " + pedido.getIdCliente());
                                pedidoList.add(pedido);
                            }

                            filteredList.clear();
                            filteredList.addAll(pedidoList);
                            pedidoAdapter.notifyDataSetChanged();

                            // Notify on new orders
                            for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                                if (change.getType() == DocumentChange.Type.ADDED) {
                                    Pedido newPedido = change.getDocument().toObject(Pedido.class);
                                    sendNotification(newPedido);
                                }
                            }
                        }
                    });
        }
    }

    private void detachFirestoreListener() {
        if (ordersListener != null) {
            ordersListener.remove();
            ordersListener = null;
        }
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

        pedidoAdapter.notifyDataSetChanged();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "New Order Notifications";
            String description = "Channel for new order notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(Pedido pedido) {
        Intent intent = new Intent(requireContext(), InicioRestauranteActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                requireContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.new_orden_noti) // Replace with your notification icon
                .setContentTitle("Nueva Orden - " + pedido.getNombreRestaurante())
                .setContentText("Detalles: " + pedido.getProductos().size() + " productos - S/" + pedido.getPagoTotal())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void fetchOrders() {
        if (idRestaurante == null) {
            Log.e("PorAceptarFragment", "ID del restaurante no disponible.");
            return;
        }

        db.collection("pedidos")
                .whereEqualTo("idRestaurante", idRestaurante)
                .whereEqualTo("estado", 0)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    pedidoList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Pedido pedido = doc.toObject(Pedido.class);
                        pedidoList.add(pedido);
                    }
                    filteredList.clear();
                    filteredList.addAll(pedidoList);
                    pedidoAdapter.notifyDataSetChanged();
                    Log.d("PorAceptarFragment", "Pedidos cargados correctamente. Total: " + pedidoList.size());
                })
                .addOnFailureListener(e -> {
                    Log.e("PorAceptarFragment", "Error al cargar pedidos: " + e.getMessage());
                });
    }
}
