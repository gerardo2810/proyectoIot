package com.example.proyecto_iot.admin_restaurante;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
public class PorAceptarFragment extends Fragment {

    private RecyclerView rv_orders_list;
    private PedidoAdapter pedidoAdapter;
    private List<Pedido> pedidoList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PorAceptarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PorAceptarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PorAceptarFragment newInstance(String param1, String param2) {
        PorAceptarFragment fragment = new PorAceptarFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_por_aceptar, container, false);

        // Configurar el RecyclerView
        rv_orders_list = view.findViewById(R.id.rv_orders_list);
        rv_orders_list.setLayoutManager(new LinearLayoutManager(getContext()));

        // Crear la lista de órdenes
        pedidoList = new ArrayList<>();
        pedidoList.add(new Pedido("#001","2 productos", "S/45.00", "20 min", "Repartidor Asiganado"));
        pedidoList.add(new Pedido("#002","1 producto", "S/55.00", "30 min", "Repartidor Asiganado"));
        pedidoList.add(new Pedido("#003","2 productos", "S/85.00", "15 min", "Repartidor Asiganado"));
        pedidoList.add(new Pedido("#004","3 productos", "S/155.00", "20 min", "Repartidor Asiganado"));
        pedidoList.add(new Pedido("#005","3 productos", "S/205.00", "10 min", "Repartidor Asiganado"));
        pedidoList.add(new Pedido("#006","4 productos", "S/380.00", "18 min", "Repartidor Asiganado"));
        pedidoList.add(new Pedido("#007","7 productos", "S/555.00", "35 min", "Repartidor Asiganado"));

        // Configurar el adaptador
        pedidoAdapter = new PedidoAdapter(pedidoList,getContext());
        rv_orders_list.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_orders_list.setAdapter(pedidoAdapter);

        return view;
    }
}