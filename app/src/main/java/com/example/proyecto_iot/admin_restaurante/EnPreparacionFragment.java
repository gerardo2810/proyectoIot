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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private Handler handler = new Handler(Looper.getMainLooper());
    private static final String CHANNEL_ID = "preparation_complete_channel";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EnPreparacionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EnPreparacionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EnPreparacionFragment newInstance(String param1, String param2) {
        EnPreparacionFragment fragment = new EnPreparacionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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

        // Crear la lista de órdenes
        pedidoList = new ArrayList<>();
        pedidoList.add(new Pedido("Juan Lopez", "#004", "3 productos", "S/155.00", "1", "Repartidor Asignado"));
        pedidoList.add(new Pedido("María Campos", "#005", "3 productos", "S/205.00", "2", "Repartidor Asignado"));
        pedidoList.add(new Pedido("Mar Silou", "#006", "4 productos", "S/380.00", "3", "Repartidor Asignado"));
        pedidoList.add(new Pedido("Lucas Perez", "#007", "7 productos", "S/555.00", "18", "Repartidor Asignado"));

        // Configurar el adaptador
        pedidoPreparadoAdapter = new PedidoPreparadoAdapter(pedidoList, getContext(), this::removeOrder);
        rv_orders_list.setAdapter(pedidoPreparadoAdapter);

        return view;
    }

    // Método para eliminar el pedido cuando se hace clic en "Listo para entregar"
    private void removeOrder(Pedido pedido) {
        int position = pedidoList.indexOf(pedido);
        if (position != -1) {
            pedidoList.remove(position);
            pedidoPreparadoAdapter.notifyItemRemoved(position);
        }
    }
}