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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.proyecto_iot.MainActivity;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Order;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.OrderAdapter;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Pedido;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.PedidoAdapter;

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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class PorAceptarFragment extends Fragment {

    private RecyclerView rv_orders_list;
    private PedidoAdapter pedidoAdapter;
    private List<Pedido> pedidoList;
    private Handler handler;
    private Runnable runnable;
    private int orderCount = 7;
    private static final String CHANNEL_ID = "Nuevo_Pedido";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verificar y solicitar permiso para notificaciones en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }

        // Crear el canal de notificaciones (necesario para Android 8 y superior)
        createNotificationChannel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_por_aceptar, container, false);

        // Configurar el RecyclerView
        rv_orders_list = view.findViewById(R.id.rv_orders_list);
        rv_orders_list.setLayoutManager(new LinearLayoutManager(getContext()));

        // Crear la lista de órdenes inicial
        pedidoList = new ArrayList<>();
        pedidoList.add(new Pedido("Juan Lopez","#007", "2 productos", "S/45.00", "20 min", "Repartidor Asignado"));
        pedidoList.add(new Pedido("María Lopez","#006", "1 producto", "S/55.00", "30 min", "Repartidor Asignado"));
        pedidoList.add(new Pedido("Jonatan Hernandez","#005", "2 productos", "S/85.00", "15 min", "Repartidor Asignado"));
        pedidoList.add(new Pedido("Frank Córdova","#004", "3 productos", "S/155.00", "20 min", "Repartidor Asignado"));
        pedidoList.add(new Pedido("Leslie Gomez","#003", "3 productos", "S/205.00", "10 min", "Repartidor Asignado"));
        pedidoList.add(new Pedido("Camila Sanchez","#002", "4 productos", "S/380.00", "18 min", "Repartidor Asignado"));
        pedidoList.add(new Pedido("Juan Lopez","#001", "7 productos", "S/555.00", "35 min", "Repartidor Asignado"));

        // Configurar el adaptador
        pedidoAdapter = new PedidoAdapter(pedidoList, getContext());
        rv_orders_list.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_orders_list.setAdapter(pedidoAdapter);

        // Configurar el Handler para añadir órdenes cada minuto
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                // Agregar una nueva orden
                orderCount++;
                Pedido newOrder = new Pedido("Matias Cordova","#00" + orderCount, "1 producto", "S/100.00", "25 min", "Repartidor Asignado");
                pedidoList.add(0, newOrder);
                pedidoAdapter.notifyItemInserted(0);
                rv_orders_list.scrollToPosition(0);

                // Enviar la notificación
                sendNotification(newOrder);

                // Ejecutar el runnable cada minuto (60000ms)
                handler.postDelayed(this, 40000);
            }
        };

        // Iniciar el runnable
        handler.postDelayed(runnable, 40000);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
    }

    // Crear el canal de notificaciones (solo para Android 8+)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "New Order Channel";
            String description = "Canal para nuevas órdenes";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Método para enviar notificación
    private void sendNotification(Pedido pedido) {
        // Crear la intención para cuando el usuario haga clic en la notificación
        Intent intent = new Intent(requireContext(), PorAceptarFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Agregar FLAG_IMMUTABLE
        PendingIntent pendingIntent = PendingIntent.getActivity(
                requireContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Crear la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.resource_new)
                .setContentTitle("Nueva Orden #" + pedido.getOrderId())
                .setContentText("Detalles: " + pedido.getCantidad() + " - " + pedido.getPrecio())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Mostrar la notificación
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permisos si no están otorgados
            return;
        }
        notificationManager.notify(orderCount, builder.build());
    }
}
