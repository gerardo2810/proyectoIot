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

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnPreparacionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnPreparacionFragment extends Fragment {

    private RecyclerView rv_orders_list;
    private PedidoPreparadoAdapter pedidoPreparadoAdapter;
    private List<Pedido> pedidoList;
    private List<Pedido> filteredList; // Lista filtrada
    private EditText orderSearch;

    private Handler handler = new Handler(Looper.getMainLooper());
    private static final String CHANNEL_ID = "preparation_complete_channel";

    public EnPreparacionFragment() {
        // Required empty public constructor
    }

    public static EnPreparacionFragment newInstance(String param1, String param2) {
        EnPreparacionFragment fragment = new EnPreparacionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_en_preparacion, container, false);

        // Configurar el RecyclerView
        rv_orders_list = view.findViewById(R.id.rv_orders_list);
        rv_orders_list.setLayoutManager(new LinearLayoutManager(getContext()));

        // Configurar el EditText para búsqueda
        orderSearch = view.findViewById(R.id.order_search);

        // Crear la lista de órdenes
        pedidoList = new ArrayList<>();
        pedidoList.add(new Pedido("Juan Lopez", "#004", "3 productos", "S/155.00", "1", "Repartidor Asignado"));
        pedidoList.add(new Pedido("María Campos", "#005", "3 productos", "S/205.00", "2", "Repartidor Asignado"));
        pedidoList.add(new Pedido("Mar Silou", "#006", "4 productos", "S/380.00", "3", "Repartidor Asignado"));
        pedidoList.add(new Pedido("Lucas Perez", "#007", "7 productos", "S/555.00", "18", "Repartidor Asignado"));

        // Inicializar la lista filtrada con la lista original
        filteredList = new ArrayList<>(pedidoList);

        // Configurar el adaptador con la lista filtrada
        pedidoPreparadoAdapter = new PedidoPreparadoAdapter(filteredList, getContext(), this::removeOrder);
        rv_orders_list.setAdapter(pedidoPreparadoAdapter);

        // Configurar el buscador
        orderSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se necesita hacer nada aquí
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterOrders(s.toString()); // Llamar al método que filtra la lista
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No se necesita hacer nada aquí
            }
        });

        return view;
    }

    // Método para eliminar el pedido cuando se hace clic en "Listo para entregar"
    private void removeOrder(Pedido pedido) {
        int position = filteredList.indexOf(pedido);
        if (position != -1) {
            filteredList.remove(position);
            pedidoPreparadoAdapter.notifyItemRemoved(position);
        }
    }

    // Método para filtrar las órdenes
    private void filterOrders(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            // Si no hay texto en la búsqueda, mostrar todas las órdenes
            filteredList.addAll(pedidoList);
        } else {
            for (Pedido pedido : pedidoList) {
                if (pedido.getOrderId().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(pedido);
                }
            }
        }

        if (filteredList.isEmpty()) {
            // Mostrar un Toast si no se encontraron resultados
            Toast.makeText(getContext(), "No se encontraron resultados", Toast.LENGTH_SHORT).show();
        }

        pedidoPreparadoAdapter.notifyDataSetChanged(); // Notificar al adaptador sobre el cambio
    }
}
