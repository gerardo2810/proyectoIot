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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        // Trigger initial data load
        attachFirestoreListener();

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

        Log.d("PorAceptarFragment", "Fragment visible. Activando listener en tiempo real.");
        attachFirestoreListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        detachFirestoreListener(); // Detach listener to avoid leaks
    }

    private void attachFirestoreListener() {
        if (idRestaurante == null) {
            Log.e("PorAceptarFragment", "attachFirestoreListener: idRestaurante es nulo.");
            return;
        }

        if (ordersListener != null) {
            Log.d("PorAceptarFragment", "Listener ya activo.");
            return;
        }

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
                        pedidoList.clear(); // Limpiar la lista para evitar duplicados
                        for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                            DocumentSnapshot doc = change.getDocument();
                            Pedido pedido = doc.toObject(Pedido.class);
                            pedido.setId(doc.getId()); // Asignar manualmente el ID del documento
                            Log.d("PorAceptarFragment", "Pedido recibido con ID: " + pedido.getId());

                            switch (change.getType()) {
                                case ADDED:
                                    pedidoList.add(pedido);
                                    break;
                                case MODIFIED:
                                    for (int i = 0; i < pedidoList.size(); i++) {
                                        if (pedidoList.get(i).getId().equals(pedido.getId())) {
                                            pedidoList.set(i, pedido);
                                            break;
                                        }
                                    }
                                    break;
                                case REMOVED:
                                    pedidoList.removeIf(p -> p.getId().equals(pedido.getId()));
                                    break;
                            }
                        }

                        // Ordenar la lista por fechaHora (más reciente primero)
                        pedidoList.sort((p1, p2) -> {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy, hh:mm:ss a", Locale.getDefault());
                                Date date1 = sdf.parse(p1.getFechaHora());
                                Date date2 = sdf.parse(p2.getFechaHora());
                                return date2.compareTo(date1);
                            } catch (ParseException ex) {
                                Log.e("Pedido", "Error al parsear fechaHora: " + ex.getMessage());
                                return 0;
                            }
                        });

                        // Actualizar la lista filtrada y notificar al adaptador
                        filteredList.clear();
                        filteredList.addAll(pedidoList);
                        pedidoAdapter.notifyDataSetChanged();
                    }
                });
    }


    private void detachFirestoreListener() {
        if (ordersListener != null) {
            ordersListener.remove();
            ordersListener = null;
            Log.d("PorAceptarFragment", "Listener desconectado.");
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
}
