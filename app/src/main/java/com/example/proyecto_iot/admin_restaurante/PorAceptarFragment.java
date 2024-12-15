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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
    private List<Pedido> filteredList;
    private FirebaseFirestore db;
    private String idRestaurante;
    private EditText orderSearch;
    private ListenerRegistration ordersListener;
    private static final String CHANNEL_ID = "Nuevo_Pedido";
    private Set<String> pedidosAnteriores = new HashSet<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            idRestaurante = getArguments().getString("idRestaurante");
        }

        if (idRestaurante == null) {
            Log.e("PorAceptarFragment", "onCreate: idRestaurante es nulo.");
            return;
        }

        Log.d("PorAceptarFragment", "onCreate: idRestaurante recibido: " + idRestaurante);

        db = FirebaseFirestore.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }

        createNotificationChannel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_por_aceptar, container, false);

        rvOrdersList = view.findViewById(R.id.rv_orders_list);
        rvOrdersList.setLayoutManager(new LinearLayoutManager(getContext()));

        orderSearch = view.findViewById(R.id.order_search);

        pedidoList = new ArrayList<>();
        filteredList = new ArrayList<>();

        pedidoAdapter = new PedidoAdapter(filteredList, getContext());
        rvOrdersList.setAdapter(pedidoAdapter);

        attachFirestoreListener();

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
        attachFirestoreListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        detachFirestoreListener();
    }

    private void attachFirestoreListener() {
        if (ordersListener != null) {
            ordersListener.remove();
        }

        ordersListener = db.collection("pedidos")
                .whereEqualTo("idRestaurante", idRestaurante)
                .whereEqualTo("estado", 0) // Solo pedidos en estado 0
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e("PorAceptarFragment", "Error obteniendo pedidos: " + e.getMessage());
                        return;
                    }

                    if (querySnapshot != null) {
                        // Registro temporal de IDs actuales
                        Set<String> pedidosActuales = new HashSet<>();
                        List<Pedido> nuevosPedidos = new ArrayList<>(); // Lista de pedidos nuevos

                        // Limpiar lista local para refrescar con los datos actuales de Firestore
                        pedidoList.clear();

                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            Pedido pedido = doc.toObject(Pedido.class);
                            if (pedido != null) {
                                pedido.setId(doc.getId());
                                pedidoList.add(pedido);
                                pedidosActuales.add(pedido.getId());

                                // Si el pedido no estaba en la lista anterior, es nuevo
                                if (!pedidosAnteriores.contains(pedido.getId())) {
                                    nuevosPedidos.add(pedido);
                                }
                            }
                        }

                        // Enviar notificaciones solo para los pedidos nuevos
                        for (Pedido nuevoPedido : nuevosPedidos) {
                            sendNotification(nuevoPedido);
                        }

                        // Actualizar el registro de IDs anteriores
                        pedidosAnteriores = pedidosActuales;

                        Log.d("PorAceptarFragment", "Lista de pedidos actualizada: " + pedidoList.size() + " pedidos.");
                        actualizarListaPedidos(); // Actualiza el RecyclerView
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

    private void actualizarListaPedidos() {
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

        filteredList.clear();
        filteredList.addAll(pedidoList);
        pedidoAdapter.notifyDataSetChanged();
    }

    private void filterOrders(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(pedidoList);
        } else {
            for (Pedido pedido : pedidoList) {
                // Verificar si el texto coincide con el código del pedido o el nombre del cliente
                if ((pedido.getCodigo() != null && pedido.getCodigo().toLowerCase().contains(query.toLowerCase())) ||
                        (pedido.getNombreCliente() != null && pedido.getNombreCliente().toLowerCase().contains(query.toLowerCase()))) {
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
            int importance = NotificationManager.IMPORTANCE_HIGH;
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
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.new_orden_noti)
                .setContentTitle("Nueva Orden - " + pedido.getNombreRestaurante())
                .setContentText("Total: S/" + pedido.getPagoTotal() + " - " + pedido.getProductos().size() + " productos.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}

